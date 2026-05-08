package com.yueyingiop.ALG.item.DraconiCevolution;

import com.yueyingiop.ALG.ALG;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.util.ModConditions;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ALGItemData {
    public static final DeferredRegister<DataComponentType<?>> DATA = 
        ModConditions.isDraconicEvolutionLoaded() ? 
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ALG.MODID):
        null;

    public static void init(IEventBus modBus) {
        if (DATA != null) {
            DATA.register(modBus);
        }
    }

    // 药水模块存储的效果数据组件
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectData>> EFFECT_LOAD_MODULE;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectData>> EFFECT_REMOVE_MODULE;

    static {
        if (DATA != null) {
            EFFECT_LOAD_MODULE = DATA.register(
                "effect_load_module",
                () -> DataComponentType.<EffectData>builder()
                    .persistent(EffectData.CODEC)
                    .networkSynchronized(EffectData.STREAM_CODEC)
                    .cacheEncoding()
                    .build()
            );

            EFFECT_REMOVE_MODULE = DATA.register(
                "effect_remove_module",
                () -> DataComponentType.<EffectData>builder()
                    .persistent(EffectData.CODEC)
                    .networkSynchronized(EffectData.STREAM_CODEC)
                    .cacheEncoding()
                    .build()
            );
        } else {
            EFFECT_LOAD_MODULE = null;
            EFFECT_REMOVE_MODULE = null;
        }
    }
}
