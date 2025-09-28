package com.core.ALG.item;

import javax.annotation.Nonnull;

import com.core.ALG.ALG;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegistryItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ALG.MODID);

    public static final RegistryObject<Item> CREATE_ENERGY_CORE = ITEMS.register(
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
