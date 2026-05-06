package com.yueyingiop.ALG.mixin.DraconiCevolution;

import java.lang.reflect.Field;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;

import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;

import net.neoforged.fml.util.thread.EffectiveSide;


@Mixin(ShieldControlEntity.class)
public class ShieldControlEntityMixin{
    @Inject(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lcom/brandon3055/draconicevolution/api/modules/entities/ShieldControlEntity$ShieldSaveData;shieldCapacity:I",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        ),
        remap = false
    )
    private void onTickAfterCapacitySet(ModuleContext moduleContext, CallbackInfo ci) {
        if (!EffectiveSide.get().isServer()) return;
        IOPStorage storage = moduleContext.getOpStorage();
        if (((ShieldControlEntity)(Object)this).getModule().getItem() != RegistryModuleItem.ITEM_CREATE_SHIELD_CONTROL.get()) return;
        if (storage == null) return;

        try {
            Field dataField = ShieldControlEntity.class.getDeclaredField("data");
            dataField.setAccessible(true);
            Object data = dataField.get(this);
            // 获取 ShieldSaveData 的 shieldCapacity 和 shieldPoints 字段
            Field capacityField = data.getClass().getField("shieldCapacity");
            Field pointsField  = data.getClass().getField("shieldPoints");

            int capacity = capacityField.getInt(data);          // 读取刚设置好的容量
            pointsField.setDouble(data, capacity);              // 护盾点数设为容量值
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
