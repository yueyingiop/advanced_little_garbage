package com.core.ALG.mixin.DraconiCevolution;

import org.spongepowered.asm.mixin.Mixin;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.entities.EnergyEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;

@Mixin(EnergyEntity.class)
public class EnergyEntityMxin extends ModuleEntity<EnergyData> {

    public EnergyEntityMxin(Module<EnergyData> module) {
        super(module);
    }

    @Override
    public void tick(ModuleContext context) {
        super.tick(context);
        IOPStorage storage = context.getOpStorage();
        if (
            (
                module.getItem() == RegistryModuleItem.ITEM_CREATE_ENERGY.get() || 
                module.getItem() == RegistryModuleItem.ITEM_CREATE_SHIELD_CONTROL.get() ||
                module.getItem() == RegistryModuleItem.ITEM_CREATE_LARGE_SHIELD_CAPACITY.get()
            ) && storage != null && storage.getEnergyStored() != Integer.MAX_VALUE-1) {
            storage.modifyEnergyStored(Integer.MAX_VALUE-1);
        }
    }

}
