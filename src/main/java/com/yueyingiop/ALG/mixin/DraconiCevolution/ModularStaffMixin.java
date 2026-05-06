package com.yueyingiop.ALG.mixin.DraconiCevolution;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import com.brandon3055.draconicevolution.items.equipment.DETier;
import com.brandon3055.draconicevolution.items.equipment.IModularMelee;
import com.brandon3055.draconicevolution.items.equipment.ModularStaff;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;

@Mixin(ModularStaff.class)
public class ModularStaffMixin extends DiggerItem implements IModularMelee {
    private final TechLevel techLevel;
    private final DETier itemTier;

    public ModularStaffMixin(DETier tier, TechProperties props) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, props);
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
        return EquipCfg.staffSwingSpeedMultiplier;
    }

    @Override
    public double getDamageMultiplier() {
        return EquipCfg.staffDamageMultiplier;
    }

    @Override
    public ModuleHostImpl instantiateHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.staffWidth(techLevel), ModuleCfg.staffHeight(techLevel), "staff", ModuleCfg.removeInvalidModules);
        host.addCategories(ModuleCategory.TOOL_AXE);
        host.addCategories(ModuleCategory.TOOL_HOE);
        host.addCategories(ModuleCategory.TOOL_SHOVEL);
        return host;
    }

    @Override
    public ModularOPStorage instantiateOPStorage(ItemStack stack, Supplier<ModuleHost> hostSupplier) {
        return new ModularOPStorage(hostSupplier, EquipCfg.getBaseStaffEnergy(techLevel), EquipCfg.getBaseStaffTransfer(techLevel));
    }
}
