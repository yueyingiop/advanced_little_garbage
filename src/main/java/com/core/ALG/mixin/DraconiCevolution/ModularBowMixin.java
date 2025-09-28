package com.core.ALG.mixin.DraconiCevolution;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.items.equipment.ModularBow;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ModularBow.class)
public class ModularBowMixin {
    // 修改能量消耗
    @Inject(method = "calculateShotEnergy", at = @At("RETURN"), cancellable = true, remap = false)
    private static void onCalculateShotEnergy(ItemStack stack, CallbackInfoReturnable<Long> cir) {
        
        if (isCreateEnergy(stack)) {
            cir.setReturnValue(0L);
        }
    }

    // 修改能量验证(注入点：捕获 energyRequired 变量的赋值)
    @ModifyVariable(
        method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V",
        at = @At(
            value = "STORE", // 捕获变量被赋值后的时刻
            ordinal = 0 // 确保匹配第一个 long 类型的 energyRequired 变量
        ),
        name = "energyRequired", // 目标变量名
        ordinal = 0
    )
    private long modifyEnergyRequired(long original, ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (isCreateEnergy(stack)) {
            return 0L;
        }
        // 非创造能量模式下使用原始计算值
        return original;
    }

    // 使用anyMatch方法来检查是否存在ITEM_CREATE_ENERGY模块
    private static Boolean isCreateEnergy(ItemStack stack) {
        ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        return host.getModules().anyMatch(m -> m.getItem() == RegistryModuleItem.ITEM_CREATE_ENERGY.get());
    }
}
