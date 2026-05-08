package com.yueyingiop.ALG.item.DraconiCevolution.modules;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.data.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.lib.BaseModule;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.item.DraconiCevolution.type.EffectRemoveModuleType;

import net.minecraft.world.item.Item;

public class EffectRemoveModule extends BaseModule<EffectData> {
    private final Item item;
    public final TechLevel techLevel;
    private final int maxEffects;

    public EffectRemoveModule(Item item, TechLevel techLevel, int maxEffects) {
        super(EffectRemoveModuleType.INSTANCE, new ModuleProperties<>(techLevel, m -> EffectData.EMPTY));
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
