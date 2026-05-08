package com.yueyingiop.ALG.item.DraconiCevolution;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.yueyingiop.ALG.ALG;
import com.yueyingiop.ALG.item.DraconiCevolution.moduleItem.EffectModuleItem;
import com.yueyingiop.ALG.util.ModConditions;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RegistryModuleItem {
    public static final DeferredRegister<Item> ITEMS = 
        ModConditions.isDraconicEvolutionLoaded() ? 
        DeferredRegister.createItems(ALG.MODID):
        null;

    //#region 注册模块物品
    // 创造能量模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_ENERGY = ITEMS != null? ITEMS.register(
        "item_create_energy", 
        () -> new ModuleItem<>(CustomModule.CREATE_ENERGY)
    ):null;

    // 创造护盾控制模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_SHIELD_CONTROL = ITEMS != null? ITEMS.register(
        "item_create_shield_control", 
        () -> new ModuleItem<>(CustomModule.CREATE_SHIELD_CONTROL)
    ):null;

    // 创造大型护盾增强模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_LARGE_SHIELD_CAPACITY = ITEMS != null? ITEMS.register(
        "item_create_large_shield_capacity", 
        () -> new ModuleItem<>(CustomModule.CREATE_LARGE_SHIELD_CAPACITY)
    ):null;

    // 混沌自动进食模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_AUTO_FEED = ITEMS != null? ITEMS.register(
        "item_chaotic_auto_feed", 
        () -> new ModuleItem<>(CustomModule.CHAOTIC_AUTO_FEED)
    ):null;

    // 创造自动进食模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_AUTO_FEED = ITEMS != null? ITEMS.register(
        "item_create_auto_feed", 
        () -> new ModuleItem<>(CustomModule.CREATE_AUTO_FEED)
    ):null;

    // 创造伤害模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_DAMAGE = ITEMS != null? ITEMS.register(
        "item_create_damage", 
        () -> new ModuleItem<>(CustomModule.CREATE_DAMAGE)
    ):null;

    // 创造弹射物模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_PROJECTILE = ITEMS != null? ITEMS.register(
        "item_create_projectile", 
        () -> new ModuleItem<>(CustomModule.CREATE_PROJECTILE)
    ):null;
    //#endregion

    //#region注册自定义模块物品
    // 药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_EFFECT_LOAD = ITEMS != null? ITEMS.register(
        "item_draconium_effect_load", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.DRACONIUM_EFFECT_LOAD.get())
    ):null;

    // 双足飞龙药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_EFFECT_LOAD = ITEMS != null? ITEMS.register(
        "item_wyvern_effect_load", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.WYVERN_EFFECT_LOAD.get())
    ):null;

    // 神龙药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_EFFECT_LOAD = ITEMS != null? ITEMS.register(
        "item_draconic_effect_load", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.DRACONIC_EFFECT_LOAD.get())
    ):null;

    // 混沌药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_EFFECT_LOAD = ITEMS != null? ITEMS.register(
        "item_chaotic_effect_load", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.CHAOTIC_EFFECT_LOAD.get())
    ):null;

    // 创造药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_EFFECT_LOAD = ITEMS != null? ITEMS.register(
        "item_create_effect_load", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.CREATE_EFFECT_LOAD.get())
    ):null;

    // 药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIUM_EFFECT_REMOVE = ITEMS != null? ITEMS.register(
        "item_draconium_effect_remove", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.DRACONIUM_EFFECT_REMOVE.get())
    ):null;

    // 双足飞龙药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_WYVERN_EFFECT_REMOVE = ITEMS != null? ITEMS.register(
        "item_wyvern_effect_remove", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.WYVERN_EFFECT_REMOVE.get())
    ):null;

    // 神龙药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_DRACONIC_EFFECT_REMOVE = ITEMS != null? ITEMS.register(
        "item_draconic_effect_remove", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.DRACONIC_EFFECT_REMOVE.get())
    ):null;

    // 混沌药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CHAOTIC_EFFECT_REMOVE = ITEMS != null? ITEMS.register(
        "item_chaotic_effect_remove", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.CHAOTIC_EFFECT_REMOVE.get())
    ):null;

    // 创造药水加载模块
    public static final DeferredHolder<Item, ModuleItem<?>> ITEM_CREATE_EFFECT_REMOVE = ITEMS != null? ITEMS.register(
        "item_create_effect_remove", 
        () -> new EffectModuleItem(new Item.Properties(), () -> CustomModule.CREATE_EFFECT_REMOVE.get())
    ):null;
    //#endregion

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (!ModConditions.isDraconicEvolutionLoaded()) return ; // 如果未加载Draconic Evolution则返回
        event.registerItem(
            DECapabilities.Module.ITEM,
            (ItemStack stack, Void context) -> {
                Item item = stack.getItem();
                if (item instanceof ModuleProvider<?> provider) {
                    return (ModuleProvider<?>) provider;
                }
                return null;
            },
            ITEM_CREATE_ENERGY.get(),
            ITEM_CREATE_SHIELD_CONTROL.get(),
            ITEM_CREATE_LARGE_SHIELD_CAPACITY.get(),
            ITEM_CHAOTIC_AUTO_FEED.get(),
            ITEM_CREATE_AUTO_FEED.get(),
            ITEM_CREATE_DAMAGE.get(),
            ITEM_CREATE_PROJECTILE.get(),
            
            ITEM_DRACONIUM_EFFECT_LOAD.get(),
            ITEM_WYVERN_EFFECT_LOAD.get(),
            ITEM_DRACONIC_EFFECT_LOAD.get(),
            ITEM_CHAOTIC_EFFECT_LOAD.get(),
            ITEM_CREATE_EFFECT_LOAD.get(),

            ITEM_DRACONIUM_EFFECT_REMOVE.get(),
            ITEM_WYVERN_EFFECT_REMOVE.get(),
            ITEM_DRACONIC_EFFECT_REMOVE.get(),
            ITEM_CHAOTIC_EFFECT_REMOVE.get(),
            ITEM_CREATE_EFFECT_REMOVE.get()
        );
    }
}
