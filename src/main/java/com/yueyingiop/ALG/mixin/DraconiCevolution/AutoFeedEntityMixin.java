package com.yueyingiop.ALG.mixin.DraconiCevolution;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.entities.AutoFeedEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;

@Mixin(AutoFeedEntity.class)
public class AutoFeedEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"), remap = false)
    private void onTickEnd(ModuleContext moduleContext, CallbackInfo ci) {
        AutoFeedEntity self = (AutoFeedEntity) (Object) this;
        Module<AutoFeedData> module = self.getModule();
        AutoFeedData data = module.getData();
        if (module.getItem() == RegistryModuleItem.ITEM_CREATE_AUTO_FEED.get() && data != null) {
            self.setStoredFood((double)data.foodStorage());
        }
    }
}
