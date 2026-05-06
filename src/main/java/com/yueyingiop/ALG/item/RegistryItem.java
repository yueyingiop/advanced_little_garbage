package com.yueyingiop.ALG.item;

import javax.annotation.Nonnull;

import com.yueyingiop.ALG.ALG;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class RegistryItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(ALG.MODID);

    public static final DeferredHolder<Item, ? extends Item> CREATE_ENERGY_CORE = ITEMS.register(
        "create_energy_core", 
        () -> new Item(
            new Item.Properties()
        ){
            @Override
            public boolean isFoil(@Nonnull ItemStack p_41453_) {
                return true;
            };
        }
    );

}
