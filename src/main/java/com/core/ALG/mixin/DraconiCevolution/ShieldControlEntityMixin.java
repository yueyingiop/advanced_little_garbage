package com.core.ALG.mixin.DraconiCevolution;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.ShieldControlData;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;

@Mixin(ShieldControlEntity.class)
public class ShieldControlEntityMixin extends ModuleEntity<ShieldControlData> {

    public ShieldControlEntityMixin(Module<ShieldControlData> module) {
        super(module);
    }

    @Shadow(remap = false)
    private double shieldPoints;

    @Shadow(remap = false)
    private int shieldCapacity;

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lcom/brandon3055/draconicevolution/api/modules/entities/ShieldControlEntity;shieldCapacity:I", ordinal = 0, shift = At.Shift.AFTER), remap = false)
    private void onTickAfterCapacitySet(ModuleContext moduleContext, CallbackInfo ci) {
        IOPStorage storage = moduleContext.getOpStorage();
        if (module.getItem() == RegistryModuleItem.ITEM_CREATE_SHIELD_CONTROL.get() && storage != null) {
            this.shieldPoints = this.shieldCapacity;
        }
    }
}
