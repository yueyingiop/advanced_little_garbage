package com.yueyingiop.ALG.mixin.DraconiCevolution;

import java.lang.reflect.Constructor;

import org.spongepowered.asm.mixin.Mixin;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.entities.EnergyEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;

@Mixin(EnergyEntity.class)
public class EnergyEntityMxin extends ModuleEntity<EnergyData> {

    private long energy = 0;

    public EnergyEntityMxin(Module<EnergyData> module) {
        super(module);
    }

    @Override
    public void tick(ModuleContext context) {
        super.tick(context);
        IOPStorage storage = context.getOpStorage();
        if (module.getItem() == RegistryModuleItem.ITEM_CREATE_ENERGY.get() && storage != null && storage.getEnergyStored() != Integer.MAX_VALUE-1) {
            storage.modifyEnergyStored(Integer.MAX_VALUE-1);
        }
    }

    @Override
    public ModuleEntity<?> copy() {
        try {
            // 使用反射创建EnergyEntity实例
            Constructor<EnergyEntity> constructor = EnergyEntity.class.getDeclaredConstructor(Module.class, int.class, int.class, long.class);
            constructor.setAccessible(true);
            return constructor.newInstance(module, getGridX(), getGridY(), energy);
        } catch (Exception e) {
            // 如果反射失败，返回新的当前类型实例
            EnergyEntityMxin newInstance = new EnergyEntityMxin(this.module);
            newInstance.energy = this.energy;
            return newInstance;
        }
    }

}
