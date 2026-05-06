package com.yueyingiop.ALG;

import com.yueyingiop.ALG.item.RegistryItem;
import com.yueyingiop.ALG.item.DraconiCevolution.CustomModule;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;
import com.yueyingiop.ALG.util.ModConditions;
import com.brandon3055.draconicevolution.init.DEModules;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.slf4j.Logger;

@Mod(ALG.MODID)
public class ALG
{
    public static final String MODID = "advanced_little_garbage";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ALG_TAB = CREATIVE_MODE_TABS.register(
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

    public ALG(IEventBus modEventBus, ModContainer modContainer)
    {

        CREATIVE_MODE_TABS.register(modEventBus);
        if (ModConditions.isDraconicEvolutionLoaded()) {
            CustomModule.MODULES.register(modEventBus);
            RegistryModuleItem.ITEMS.register(modEventBus);
            modEventBus.addListener(RegistryModuleItem::registerCapabilities);
            modEventBus.addListener(ALG::onConstruct);
        }
        RegistryItem.ITEMS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    private static void onConstruct(final FMLConstructModEvent event) {
        DEModules.MODULE_PROVIDING_MODS.add(MODID);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
