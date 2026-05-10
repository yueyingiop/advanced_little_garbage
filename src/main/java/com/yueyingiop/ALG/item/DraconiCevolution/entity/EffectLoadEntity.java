package com.yueyingiop.ALG.item.DraconiCevolution.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEModules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yueyingiop.ALG.ALGConfig;
import com.yueyingiop.ALG.item.DraconiCevolution.ALGItemData;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectLoadModule;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.util.thread.EffectiveSide;

public class EffectLoadEntity extends ModuleEntity<EffectData>{

    private EffectData effects = EffectData.EMPTY;
    private int tickCounter = 0;
    private boolean wasEquipped = false;

    public static final Codec<EffectLoadEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(EffectLoadEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            EffectData.CODEC.fieldOf("effects").forGetter(e -> e.effects)
    ).apply(builder, EffectLoadEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectLoadEntity> STREAM_CODEC = StreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            EffectData.STREAM_CODEC, e -> e.effects,
            EffectLoadEntity::new
    );

    public EffectLoadEntity(Module<EffectData> module) {
        super(module);
    }

    @SuppressWarnings("unchecked")
    EffectLoadEntity(Module<?> module, int gridX, int gridY, EffectData effects) {
        super((Module<EffectData>) module, gridX, gridY);
        this.effects = effects;
    }

    //#region 核心处理函数
    @Override
    public void tick(ModuleContext context) {
        if (!(
            context instanceof StackModuleContext stackContext && 
            EffectiveSide.get().isServer()
            )
        ) return;

        LivingEntity entity = stackContext.getEntity();
        if (!(entity instanceof ServerPlayer player)) return;

        boolean isEquipped = stackContext.isEquipped();
        if (!isEquipped && wasEquipped) {
            // 刚从装备状态变为未装备，移除所有效果
            removeEffects(player);
            wasEquipped = false;
            return;
        }
        if (!isEquipped) return;
        wasEquipped = true;

        if (tickCounter++ % 100 != 0) return;

        clearCaches();
        markDirty();

        // 创建模块则直接应用所有效果
        if (isCreateModule()) {
            updateCreativeEffects();
            applyEffects(player);
            return;
        }

        // 计算能量消耗
        IOPStorage storage = context.getOpStorage();
        if (storage == null) return;
        int basePower = ALGConfig.EFFECT_LOAD_BASE_POWER.get();
        int effectCount = effects.effects().size();
        if (effectCount > 0) {
            int totalLevel = effects.effects().values().stream().mapToInt(amp -> amp + 1).sum();
            long energyCost = (long) totalLevel * effectCount * basePower;

            // 能量不足则清除效果并停止
            if (storage.getOPStored() < energyCost) {
                removeEffects(player);
                return;
            }

            storage.modifyEnergyStored(-energyCost);
        }

        applyEffects(player);
    }

    // 安装
    @Override
    public void onInstalled(ModuleContext context) {
        super.onInstalled(context);
        if (!(context instanceof StackModuleContext stackContext && EffectiveSide.get().isServer())) return;
        if (stackContext.getEntity() instanceof ServerPlayer player && stackContext.isEquipped()) {
            if (isCreateModule()) {
                updateCreativeEffects();
                applyEffects(player);
            } else {
                IOPStorage storage = context.getOpStorage();
                if (storage != null) {
                    applyEffects(player);
                }
            }
        }
    }

    // 移除
    @Override
    public void onRemoved(ModuleContext context) {
        super.onRemoved(context);
        if (context instanceof StackModuleContext stackContext && EffectiveSide.get().isServer()) {
            if (stackContext.getEntity() instanceof ServerPlayer player) {
                removeEffects(player);
            }
        }
    }
    //#endregion

    @Override
    public Module<EffectData> getModule() {
        return super.getModule();
    }

    // 获取当前存储的效果数据
    public EffectData getEffects() {
        return effects;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new EffectLoadEntity(module, getGridX(), getGridY(), effects);
    }

    // 存储
    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ALGItemData.EFFECT_LOAD_MODULE.get(), effects);
        markDirty();
    }

    // 加载
    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        if (isCreateModule()) {
            updateCreativeEffects();
        } else {
            effects = stack.getOrDefault(ALGItemData.EFFECT_LOAD_MODULE.get(), EffectData.EMPTY);
        }
    }

    //#region 辅助函数
    /**
     * 创造模块专用：构建包含所有正面效果（等级 V）的 EffectData
     */
    private void updateCreativeEffects() {
        Map<Holder<MobEffect>, Integer> map = new HashMap<>();
        List<? extends String> blacklist = ALGConfig.CREATE_EFFECT_LOAD_BLACKLIST.get();
        int level = ALGConfig.CREATE_EFFECT_LOAD_LEVEL.get();

        BuiltInRegistries.MOB_EFFECT.iterator().forEachRemaining(effect -> {
            if (effect.isBeneficial()) {
                String id = BuiltInRegistries.MOB_EFFECT.getKey(effect).toString();
                if (!blacklist.contains(id)) {
                    map.put(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), level);
                }
            }
        });
        this.effects = new EffectData(Collections.unmodifiableMap(map));
    }

    /**
     * 将 EffectData 中的所有效果施加到玩家，若玩家已拥有更高等级效果则保留。
     */
    private void applyEffects(ServerPlayer player) {

        for (var entry : effects.effects().entrySet()) {
            Holder<MobEffect> effectHolder = entry.getKey();
            int amplifier = entry.getValue();

            int duration = 400;
            MobEffectInstance instance = new MobEffectInstance(effectHolder, duration, amplifier,
                    false, // ambient：不显示半透明气泡
                    false, // visible：不显示右上角图标
                    true   // showIcon：在背包界面显示图标
            );

            player.addEffect(instance);
        }
    }

    /**
     * 移除玩家中的所有效果
     */
    private void removeEffects(ServerPlayer player) {
        for (Holder<MobEffect> effectHolder : effects.effects().keySet()) {
            player.removeEffect(effectHolder);
        }
    }

    private boolean isCreateModule() {
        EffectLoadModule em = (EffectLoadModule) module;
        return em.getItem() == RegistryModuleItem.ITEM_CREATE_EFFECT_LOAD.get();
    }
    //#endregion
}
