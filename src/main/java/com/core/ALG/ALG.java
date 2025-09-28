package com.core.ALG;

import com.core.ALG.item.RegistryItem;
import com.core.ALG.item.DraconiCevolution.CustomModule;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;
import com.core.ALG.util.ModConditions;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import org.slf4j.Logger;

@Mod(ALG.MODID)
public class ALG
{
    public static final String MODID = "advanced_little_garbage";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final RegistryObject<CreativeModeTab> ALG_TAB = CREATIVE_MODE_TABS.register(
        "alg_tab", 
        () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("itemGroup.alg_tab"))
            .icon(() -> RegistryItem.CREATE_ENERGY_CORE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(RegistryItem.CREATE_ENERGY_CORE.get());
                if (ModConditions.isDraconicEvolutionLoaded()) {
                    output.accept(RegistryModuleItem.ITEM_CREATE_ENERGY.get());
                    output.accept(RegistryModuleItem.ITEM_CREATE_SHIELD_CONTROL.get());
                    output.accept(RegistryModuleItem.ITEM_CREATE_LARGE_SHIELD_CAPACITY.get());
                    output.accept(RegistryModuleItem.ITEM_CHAOTIC_AUTO_FEED.get());
                    output.accept(RegistryModuleItem.ITEM_CREATE_AUTO_FEED.get());
                    output.accept(RegistryModuleItem.ITEM_CREATE_DAMAGE.get());
                    output.accept(RegistryModuleItem.ITEM_CREATE_PROJECTILE.get());
                }
                
            }).build()
    );

    public ALG(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        CREATIVE_MODE_TABS.register(modEventBus);
        if (ModConditions.isDraconicEvolutionLoaded()) {
            CustomModule.MODULES.register(modEventBus);
            RegistryModuleItem.ITEMS.register(modEventBus);
        }
        RegistryItem.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


}
