package com.yueyingiop.ALG.mixin.DraconiCevolution;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.yueyingiop.ALG.util.ColorHelper;

@Mixin(ModuleEntity.class)
public class ModuleEntityMixin<T extends ModuleData<T>> {

    @Inject(method = "getModuleColour", at = @At("HEAD"), cancellable = true, remap = false) 
    private void onGetModuleColour(Module<?> module, CallbackInfoReturnable<Integer> cir) {
        String path = module.getTexture().texture().getPath();
        Pattern pattern = Pattern.compile(".*create.*");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            int color = ColorHelper.colorListParse(List.of(234, 171, 255));
            cir.setReturnValue(color);
        }
    }
}
