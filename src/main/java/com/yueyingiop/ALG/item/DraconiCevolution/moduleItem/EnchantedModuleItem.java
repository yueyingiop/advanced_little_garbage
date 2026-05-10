package com.yueyingiop.ALG.item.DraconiCevolution.moduleItem;

import java.util.List;
import java.util.function.Supplier;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.ALGItemData;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EnchantedData;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EnchantedLoadModule;
import com.yueyingiop.ALG.util.TextHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantedModuleItem extends ModuleItem<EnchantedData>{
    public EnchantedModuleItem(Properties properties, Supplier<Module<?>> moduleSupplier) {
        super(properties, moduleSupplier);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        // 获取模块类型
        Module<?> rawModule = ModuleItem.getModule(stack);
        if (!(rawModule instanceof EnchantedLoadModule loadModule)) return;

        int maxEnchantments = loadModule.maxEnchantments();

        // 读取已存储的附魔数据
        EnchantedData data = stack.getOrDefault(ALGItemData.ENCHANTED_LOAD_MODULE.get(), EnchantedData.EMPTY);
        var enchantments = data.enchantments();
        int current = enchantments.size();

        // 显示附魔数量 / 最大容量
        tooltip.add(
            Component.translatable(
                "tooltip.advanced_little_garbage.enchanted_module.count",
                Component.literal(current + " / " + maxEnchantments)
                    .copy()
                    .withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN)))
            ).withStyle(style -> style.withColor(0xAAAAAA))
        );

        // 按住 Shift 显示详细附魔列表
        if (!enchantments.isEmpty() && Screen.hasShiftDown()) {
            if (context.registries() != null) {
                Registry<Enchantment> registry = context.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                for (var entry : enchantments.entrySet()) {
                    ResourceKey<Enchantment> key = entry.getKey();
                    int level = entry.getValue();
                    Enchantment enchantment = registry.get(key);
                    if (enchantment != null) {
                        Component name = enchantment.description();
                        Component romanLevel = Component.literal(TextHelper.toRoman(level));
                        tooltip.add(
                            Component.translatable(
                                "tooltip.advanced_little_garbage.enchanted_module.enchantment",
                                name.copy().withStyle(style -> style.withColor(0xFFAA00)),
                                romanLevel.copy().withStyle(style -> style.withColor(0xAAAAAA))
                            )
                        );
                    }
                }
            }
        }
    }
}
