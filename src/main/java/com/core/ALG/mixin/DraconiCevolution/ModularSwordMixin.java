package com.core.ALG.mixin.DraconiCevolution;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import com.brandon3055.draconicevolution.items.equipment.DETier;
import com.brandon3055.draconicevolution.items.equipment.IModularMelee;
import com.brandon3055.draconicevolution.items.equipment.ModularSword;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

@Mixin(ModularSword.class)
public class ModularSwordMixin extends SwordItem implements IModularMelee {

    private final TechLevel techLevel;
    private final DETier itemTier;

    public ModularSwordMixin(DETier tier, TechProperties props) {
        super(tier, 0, 0, props);
        this.techLevel = props.getTechLevel();
        this.itemTier = (DETier) getTier();
    }

    @Override
    public double getAttackDamage(ModuleHost host, ItemStack stack) {
        boolean isTrue = host.getModules().anyMatch(m -> m.getItem() == RegistryModuleItem.ITEM_CREATE_DAMAGE.get());
        double res = 0D;
        if (isTrue) {
            res = (double)Integer.MAX_VALUE - 1;
        } else {
            double damage = host.getModuleData(ModuleTypes.DAMAGE, new DamageData(0)).damagePoints();
            if (getEnergyStored(stack) < EquipCfg.energyAttack * damage) {
                damage = 0;
            }
            res = damage + ((getItemTier().getAttackDamageBonus() * getDamageMultiplier()) - 1);
        }
        return res;
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public DETier getItemTier() {
        return itemTier;
    }

    @Override
    public double getSwingSpeedMultiplier() {
        return EquipCfg.swordSwingSpeedMultiplier;
    }

    @Override
    public double getDamageMultiplier() {
        return EquipCfg.swordDamageMultiplier;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.toolWidth(techLevel), ModuleCfg.toolHeight(techLevel), "sword", ModuleCfg.removeInvalidModules);
        return host;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseToolEnergy(techLevel), EquipCfg.getBaseToolTransfer(techLevel));
    }
}
