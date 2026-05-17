package com.core.ALG.item.DraconiCevolution.module;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.data.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.lib.BaseModule;
import com.core.ALG.item.DraconiCevolution.data.EffectData;
import com.core.ALG.item.DraconiCevolution.type.EffectLoadModuleType;

import net.minecraft.world.item.Item;

public class EffectLoadModule extends BaseModule<EffectData> {
    private final Item item;
    public final TechLevel techLevel;
    private final int maxEffects;

    public EffectLoadModule(Item item, TechLevel techLevel, int maxEffects) {
        
        super(
            EffectLoadModuleType.INSTANCE, 
            new ModuleProperties<>(
                techLevel,
                m -> EffectData.EMPTY
            )
        );
        this.item = item;
        this.techLevel = techLevel;
        this.maxEffects = maxEffects;
    }

    @Override
    public Item getItem() {
        return item;
    }

    public int maxEffects() {
        return maxEffects;
    }

    @Override
    public int maxInstallable() {
        return 2;
    }
}
