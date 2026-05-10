package com.yueyingiop.ALG.item.DraconiCevolution.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.ALGItemData;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EnchantedData;
import com.yueyingiop.ALG.item.DraconiCevolution.moduleItem.EffectModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.moduleItem.EnchantedModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectLoadModule;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectRemoveModule;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EnchantedLoadModule;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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

    @SubscribeEvent
    public void AnvilEnchantedModule(AnvilUpdateEvent event){
        ItemStack left = event.getLeft();   // 附魔书
        ItemStack right = event.getRight(); // 附魔模块
        if (!left.has(DataComponents.STORED_ENCHANTMENTS) || !(right.getItem() instanceof EnchantedModuleItem)) return;
        
        // 获取右侧模块的类型
        Module<?> rawModule = ModuleItem.getModule(right);
        if (!(rawModule instanceof EnchantedLoadModule loadModule)) return;

        // 从左侧附魔书读取附魔
        ItemEnchantments bookEnchantments = left.get(DataComponents.STORED_ENCHANTMENTS);
        if (bookEnchantments == null || bookEnchantments.isEmpty()) return;

        Map<ResourceKey<Enchantment>, Integer> enchMap = new HashMap<>();
        bookEnchantments.entrySet().forEach(entry -> {
            entry.getKey().unwrapKey().ifPresent(key -> enchMap.put(key, entry.getIntValue()));
        });
        EnchantedData bookData = new EnchantedData(Collections.unmodifiableMap(enchMap));

        // 从右侧模块读取已有数据
        EnchantedData existingData = right.getOrDefault(ALGItemData.ENCHANTED_LOAD_MODULE.get(), EnchantedData.EMPTY);

        // 合并（取最高等级）
        EnchantedData mergedData = existingData.combine(bookData);

        // 检查是否超出最大附魔数量
        if (mergedData.enchantments().size() > loadModule.maxEnchantments()) return;

        // 输出为右侧模块的副本，并将合并后的数据写入
        ItemStack output = right.copy();
        output.set(ALGItemData.ENCHANTED_LOAD_MODULE.get(), mergedData);

        event.setOutput(output);
        event.setCost(1);          // 消耗 1 级经验
        event.setMaterialCost(1);  // 消耗 1 个右侧物品（模块）
    }
}
