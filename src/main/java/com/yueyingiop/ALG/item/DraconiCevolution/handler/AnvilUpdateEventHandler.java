package com.yueyingiop.ALG.item.DraconiCevolution.handler;

import java.util.HashMap;
import java.util.Map;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.ALGItemData;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.item.DraconiCevolution.moduleItem.EffectModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectLoadModule;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectRemoveModule;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AnvilUpdateEventHandler {
    @SubscribeEvent
    public void AnvilEffectModule(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft(); // 药水模块物品
        ItemStack right = event.getRight(); // 药水
        if (!(left.getItem() instanceof EffectModuleItem) || !right.has(DataComponents.POTION_CONTENTS)) return;
    
        // 获取药水模块
        Module<?> rawModule = ModuleItem.getModule(left);
        if (!(rawModule instanceof EffectLoadModule) && !(rawModule instanceof EffectRemoveModule)) return;

        // 获取药水效果
        PotionContents contents = right.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return;

        // 创建药水效果到EffectData
        Map<Holder<MobEffect>, Integer> potionEffects  = new HashMap<>();
        for (MobEffectInstance instance : contents.getAllEffects()) {
            potionEffects.put(instance.getEffect(), instance.getAmplifier());
        }
        EffectData potionData = new EffectData(potionEffects);

        // 根据模块类型选择数据组件和最大效果数
        DeferredHolder<DataComponentType<?>, DataComponentType<EffectData>> dataComponent;
        int maxEffects;
        if (rawModule instanceof EffectLoadModule loadModule) {
            // EffectLoadModule
            dataComponent = ALGItemData.EFFECT_LOAD_MODULE;
            maxEffects = loadModule.maxEffects();
        } else {
             // EffectRemoveModule
            dataComponent = ALGItemData.EFFECT_REMOVE_MODULE;
            EffectRemoveModule removeModule = (EffectRemoveModule) rawModule;
            maxEffects = removeModule.maxEffects();
        }

        // 读取模块已有的效果数据（若无，则用 EMPTY）
        EffectData existingData = EffectData.EMPTY;
        if (dataComponent != null) {
            existingData = left.getOrDefault(dataComponent.get(), EffectData.EMPTY);
        }

        // 合并：相同效果保留最高等级，并新增药水效果
        EffectData mergedData = existingData.combine(potionData);

        // 效果数量限制
        if (mergedData.effects().size() > maxEffects) return;

        ItemStack output = left.copy();
        output.set(dataComponent.get(), mergedData);

        event.setOutput(output);
        event.setCost(1);          // 消耗 1 级经验
        event.setMaterialCost(1);
    }
}
