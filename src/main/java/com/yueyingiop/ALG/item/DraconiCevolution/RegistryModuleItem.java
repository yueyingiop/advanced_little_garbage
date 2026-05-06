package com.yueyingiop.ALG.item.DraconiCevolution;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleProvider;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.yueyingiop.ALG.ALG;
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
            ITEM_CREATE_PROJECTILE.get()
        );
    }
}
