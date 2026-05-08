package com.yueyingiop.ALG.item.DraconiCevolution.moduleItem;

import java.util.List;
import java.util.function.Supplier;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.ALGItemData;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectLoadModule;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EffectRemoveModule;
import com.yueyingiop.ALG.util.TextHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EffectModuleItem extends ModuleItem<EffectData> {

    public EffectModuleItem(Properties properties, Supplier<Module<?>> moduleSupplier) {
        super(properties, moduleSupplier);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        // 判断模块类型
        Module<?> rawModule = ModuleItem.getModule(stack);
        boolean isLoad = rawModule instanceof EffectLoadModule;
        boolean isRemove = rawModule instanceof EffectRemoveModule;
        if (!isLoad && !isRemove) return;

        // 选择对应的数据组件和最大效果数
        DeferredHolder<DataComponentType<?>, DataComponentType<EffectData>> dataComponent;
        int maxEffects;

        if (isLoad) {
            dataComponent = ALGItemData.EFFECT_LOAD_MODULE;
            maxEffects = ((EffectLoadModule) rawModule).maxEffects();
        } else {
            dataComponent = ALGItemData.EFFECT_REMOVE_MODULE;
            maxEffects = ((EffectRemoveModule) rawModule).maxEffects();
        }

        // 读取已存储的效果数据
        EffectData data = EffectData.EMPTY;
        if (dataComponent != null) {
            data = stack.getOrDefault(dataComponent.get(), EffectData.EMPTY);
        }

        var effects = data.effects();

        // 显示效果数量 / 最大容量
        int current = effects.size();
        tooltip.add(
            Component.translatable(
                "tooltip.advanced_little_garbage.effect_module.count", 
                Component.literal(current + " / " + maxEffects).copy().withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN)))
            )
                .withStyle(style -> style.withColor(0xAAAAAA))
        );

        // 显示每个效果的名称和等级
        // 格式：生命恢复 I
        // 不为空并且按下Shift才显示
        if (!effects.isEmpty() && Screen.hasShiftDown()) {
            for (var entry : effects.entrySet()) {
                Holder<MobEffect> effectHolder = entry.getKey();
                int amplifier = entry.getValue();
                Component name = effectHolder.value().getDisplayName();
                Component roman = Component.literal(TextHelper.toRoman(amplifier + 1));
                // 格式：生命恢复 I
                tooltip.add(
                    Component.translatable(
                        "tooltip.advanced_little_garbage.effect_module.effect", 
                        name.copy().withStyle(style -> style.withColor(0xFFAA00)), 
                        roman.copy().withStyle(style -> style.withColor(0xAAAAAA))
                    )
                );
            }
        }

        
    }
}
