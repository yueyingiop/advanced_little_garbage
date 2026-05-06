package com.yueyingiop.ALG.item.DraconiCevolution.modules;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.data.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.lib.BaseModule;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;

import net.minecraft.world.item.Item;

public class CreateEnergyModule extends BaseModule<EnergyData> {
    public static final ModuleProperties<EnergyData> PROPERTIES = new ModuleProperties<>(
        TechLevel.CHAOTIC,
        2, 2,
        m -> new EnergyData(Integer.MAX_VALUE-1, (int)(Integer.MAX_VALUE-1)/10)
    );

    public CreateEnergyModule() {
        super(
            ModuleTypes.ENERGY_STORAGE, 
            PROPERTIES
        );
    }

    @Override
    public Item getItem() {
        return RegistryModuleItem.ITEM_CREATE_ENERGY.get();
    }

    @Override
    public int maxInstallable() {
        return 1;
    }
}
