package com.core.ALG.item.DraconiCevolution.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;
import com.core.ALG.item.DraconiCevolution.data.EffectData;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.thread.EffectiveSide;

@SuppressWarnings({"null","deprecation"})
public class EffectLoadEntity extends ModuleEntity<EffectData> {

    private EffectData effects = new EffectData(Collections.emptyMap());
    private int tickCounter = 0;
    private boolean wasEquipped = false;

    public EffectLoadEntity(Module<EffectData> module) {
        super(module);
    }

    //#region 核心逻辑
    @Override
    public void tick(ModuleContext context) {
        if (!(
            context instanceof StackModuleContext stackContext && 
            EffectiveSide.get().isServer()
        )) return;

        LivingEntity entity = stackContext.getEntity();
        if (!(entity instanceof ServerPlayer player)) return;

        updateEffects(context, player);

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
        

        // 创建模块则直接应用所有效果
        if (isCreateModule()) {
            updateCreativeEffects();
            applyEffects(player);
            return;
        }

        // 计算能量消耗
        IOPStorage storage = context.getOpStorage();
        if (storage == null) return;
        // TODO: 等待替换为配置文件
        int basePower =  200;//ALGConfig.EFFECT_LOAD_BASE_POWER.get();
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
                    loadEffectsFromStack(stackContext.getStack());
                    updateEffects(context, player);
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
        wasEquipped = false;
    }
    //#endregion

    //#region 数据读写
    @Override
    public void writeToItemStack(ItemStack stack, ModuleContext context) {
        super.writeToItemStack(stack, context);
        if (!effects.equals(EffectData.EMPTY)) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.put("EffectData", effects.serializeNBT());
        }
    }

    @Override
    public void readFromItemStack(ItemStack stack, ModuleContext context) {
        super.readFromItemStack(stack, context);
        if (stack.hasTag() && stack.getTag().contains("EffectData")) {
            effects = EffectData.deserializeNBT(stack.getTag().getCompound("EffectData"));
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        if (!effects.equals(EffectData.EMPTY)) {
            compound.put("EffectData", effects.serializeNBT());
        }
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        effects = EffectData.deserializeNBT(compound.getCompound("EffectData"));
    }

    @Override
    protected CompoundTag writeExtraData(CompoundTag nbt) {
        if (!effects.equals(EffectData.EMPTY)) {
            nbt.put("EffectData", effects.serializeNBT());
        }
        return nbt;
    }

    @Override
    protected void readExtraData(CompoundTag nbt) {
        effects = EffectData.deserializeNBT(nbt.getCompound("EffectData"));
    }
    //#endregion

    //#region 辅助函数
    /**
     * 手动从物品栈加载配置（供 onInstalled 调用，因为此时可能还没调用 readFromItemStack）
     */
    private void loadEffectsFromStack(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("EffectData")) {
            effects = EffectData.deserializeNBT(stack.getTag().getCompound("EffectData"));
        }
    }

    /**
     * 更新效果列表：优先使用已经加载的 effects（来自物品栈），否则回退到模块固定数据
     */
    private void updateEffects(ModuleContext context, ServerPlayer player) {
        EffectData moduleData = getModule().getData();
        if (effects.equals(EffectData.EMPTY) && !moduleData.equals(EffectData.EMPTY)) {
            effects = moduleData;
        }
    }

    /**
     * 创造模块专用：构建包含所有正面效果（等级 V）的 EffectData
     */
    private void updateCreativeEffects() {
        Map<MobEffect, Integer> map = new HashMap<>();
        // TODO: 等待替换为配置文件
        List<String> blacklist = List.of();
        int level = 4;

        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            if (effect.isBeneficial()) {
                String id = BuiltInRegistries.MOB_EFFECT.getKey(effect).toString();
                if (!blacklist.contains(id)) {
                    map.put(effect, level);
                }
            }
        }
        this.effects = new EffectData(Collections.unmodifiableMap(map));
    }

    /**
     * 将 EffectData 中的所有效果施加到玩家，若玩家已拥有更高等级效果则保留。
     */
    private void applyEffects(ServerPlayer player) {

        for (var entry : effects.effects().entrySet()) {
            MobEffect effect = entry.getKey();
            int amplifier = entry.getValue();

            int duration = 400;
            MobEffectInstance instance = new MobEffectInstance(effect, duration, amplifier,
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
        for (MobEffect effect : effects.effects().keySet()) {
            player.removeEffect(effect);
        }
    }
    
    /**
     * 是否为创造模块
    */
    private boolean isCreateModule() {
        return getModule().getItem() == RegistryModuleItem.ITEM_CREATE_EFFECT_LOAD.get();
    }
    //#endregion
}
