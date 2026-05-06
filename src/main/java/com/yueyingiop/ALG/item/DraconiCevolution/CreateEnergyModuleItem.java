package com.yueyingiop.ALG.item.DraconiCevolution;

import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;

import net.minecraft.world.item.Item;

public class CreateEnergyModuleItem extends ModuleItem<EnergyData> {

    public CreateEnergyModuleItem() {
        super(new Item.Properties(), ()->CustomModule.CREATE_ENERGY.get());
        //TODO Auto-generated constructor stub
    }
}
