package com.core.ALG.mixin.DraconiCevolution;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;

@Mixin(ShieldControlEntity.class)
public class ShieldControlEntityMixin {

    @Shadow(remap = false)
    private double shieldPoints;

    @Shadow(remap = false)
    private int shieldCapacity;

    // 设置护盾容量
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void onTickAfterCapacitySet(ModuleContext moduleContext, CallbackInfo ci) {
        Module<?> module = ((ModuleEntity<?>) (Object) this).getModule();
        
        if (module.getItem() == RegistryModuleItem.ITEM_CREATE_SHIELD_CONTROL.get()) {
            this.shieldPoints = this.shieldCapacity;
            ci.cancel();
        }
    }
}
