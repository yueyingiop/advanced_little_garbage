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
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectRemoveModule;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.util.thread.EffectiveSide;

public class EffectRemoveEntity extends ModuleEntity<EffectData> {
    private EffectData effects = EffectData.EMPTY;
    private int tickCounter = 0;
    private boolean wasEquipped = false;

    
    public static final Codec<EffectRemoveEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(EffectRemoveEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            EffectData.CODEC.fieldOf("effects").forGetter(e -> e.effects)
    ).apply(builder, EffectRemoveEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectRemoveEntity> STREAM_CODEC = StreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            EffectData.STREAM_CODEC, e -> e.effects,
            EffectRemoveEntity::new
    );

    public EffectRemoveEntity(Module<EffectData> module) {
        super(module);
    }

    @SuppressWarnings("unchecked")
    EffectRemoveEntity(Module<?> module, int gridX, int gridY, EffectData dummy) {
        super((Module<EffectData>) module, gridX, gridY);
        this.effects = dummy;
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
            wasEquipped = false;
            return;
        }
        if (!isEquipped) return;
        wasEquipped = true;

        if (tickCounter++ % 100 != 0) return;

        clearCaches();
        markDirty();

        if (isCreateModule()) {
            // 创造模块：更新负面效果列表并移除
            updateCreativeEffects();
            removeEffects(player);
            return;
        }

        // 普通模块需要能量
        IOPStorage storage = context.getOpStorage();
        if (storage == null) return;
        int basePower = ALGConfig.EFFECT_REMOVE_BASE_POWER.get();
        int effectCount = effects.effects().size();
        if (effectCount > 0) {
            long energyCost = effectCount * basePower;
            if (storage.getOPStored() < energyCost) return;
            storage.modifyEnergyStored(-energyCost);
        }

        removeEffects(player);
    }

    @Override
    public void onInstalled(ModuleContext context) {
        super.onInstalled(context);
        if (!(context instanceof StackModuleContext stackContext && EffectiveSide.get().isServer())) return;
        if (stackContext.getEntity() instanceof ServerPlayer player && stackContext.isEquipped()) {
            if (isCreateModule()) {
                updateCreativeEffects();
                removeEffects(player);
            } else {
                IOPStorage storage = context.getOpStorage();
                if (storage != null) {
                    removeEffects(player);
                }
            }
        }
    }

    @Override
    public void onRemoved(ModuleContext context) {
        super.onRemoved(context);
    }

    //#endregion

    @Override
    public Module<EffectData> getModule() {
        return super.getModule();
    }

    public EffectData getEffects() {
        return effects;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new EffectRemoveEntity(module, getGridX(), getGridY(), effects);
    }

    // 存储
    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        if (ALGItemData.EFFECT_REMOVE_MODULE != null) {
            stack.set(ALGItemData.EFFECT_REMOVE_MODULE.get(), effects);
            markDirty();
        }
    }

    // 加载
    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        if (isCreateModule()) {
            updateCreativeEffects();
        } else {
            if (ALGItemData.EFFECT_REMOVE_MODULE != null) {
                effects = stack.getOrDefault(ALGItemData.EFFECT_REMOVE_MODULE.get(), EffectData.EMPTY);
            }
        }
    }

    //#region 辅助函数
    /**
     * 创造模块专用：构建包含所有负面效果（等级 I）的 EffectData
     */
    private void updateCreativeEffects() {
        Map<Holder<MobEffect>, Integer> map = new HashMap<>();
        List<? extends String> blacklist = ALGConfig.CREATE_EFFECT_REMOVE_BLACKLIST.get();

        BuiltInRegistries.MOB_EFFECT.iterator().forEachRemaining(effect -> {
            if (!effect.isBeneficial()) {
                String id = BuiltInRegistries.MOB_EFFECT.getKey(effect).toString();
                if (!blacklist.contains(id)) {
                    map.put(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), 0);
                }
            }
        });
        this.effects = new EffectData(Collections.unmodifiableMap(map));
    }
    /**
     * 移除 effects 列表中指定的负面效果
     */
    private void removeEffects(ServerPlayer player) {
        for (Holder<MobEffect> effectHolder : effects.effects().keySet()) {
            player.removeEffect(effectHolder);
        }
    }

    private boolean isCreateModule() {
        EffectRemoveModule em = (EffectRemoveModule) module;
        return em.getItem() == RegistryModuleItem.ITEM_CREATE_EFFECT_REMOVE.get();
    }
    //#endregion
}
