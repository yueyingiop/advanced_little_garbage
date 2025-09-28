package com.core.ALG.item.DraconiCevolution;

import java.util.function.Function;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.AutoFeedData;
import com.brandon3055.draconicevolution.api.modules.data.DamageData;
import com.brandon3055.draconicevolution.api.modules.data.EnergyData;
import com.brandon3055.draconicevolution.api.modules.data.ProjectileData;
import com.brandon3055.draconicevolution.api.modules.data.ShieldControlData;
import com.brandon3055.draconicevolution.api.modules.data.ShieldData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleImpl;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.core.ALG.util.ModConditions;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CustomModule {
    public static final DeferredRegister<Module<?>> MODULES = ModConditions.isDraconicEvolutionLoaded() ? DEModules.MODULES : null;

    // 创造能量
    public static final RegistryObject<Module<?>> CREATE_ENERGY = MODULES != null ? MODULES.register(
        "create_energy", 
        () -> {
            ModuleImpl<?> module = new ModuleImpl<>(
                ModuleTypes.ENERGY_STORAGE,
                TechLevel.CHAOTIC,
                energyData(Integer.MAX_VALUE-1, (int)(Integer.MAX_VALUE-1)/10),
                2,2
            );
            module.setMaxInstall(1);
            module.setModuleItem(RegistryModuleItem.ITEM_CREATE_ENERGY.get());
            return module;
        }
    ): null;

    // 创造护盾控制器
    public static final RegistryObject<Module<?>> CREATE_SHIELD_CONTROL = MODULES != null ? MODULES.register(
        "create_shield_control", 
        () -> {
            ModuleImpl<?> module = new ModuleImpl<>(
                ModuleTypes.SHIELD_CONTROLLER,
                TechLevel.CHAOTIC,
                shieldControl(0.0)
            );
            module.setMaxInstall(1);
            module.setModuleItem(RegistryModuleItem.ITEM_CREATE_SHIELD_CONTROL.get());
            return module;
        }
    ): null;

    // 创造大型护盾增强
    public static final RegistryObject<Module<?>> CREATE_LARGE_SHIELD_CAPACITY = MODULES != null ? MODULES.register(
        "create_large_shield_capacity",
        () -> {
            ModuleImpl<?> module = new ModuleImpl<>(
                ModuleTypes.SHIELD_BOOST,
                TechLevel.CHAOTIC,
                shieldData(Integer.MAX_VALUE-1, 0.0),
                2,2
            );
            module.setMaxInstall(1);
            module.setModuleItem(RegistryModuleItem.ITEM_CREATE_LARGE_SHIELD_CAPACITY.get());
            return module;
        }
    ): null;

    // 混沌自动进食
    public static final RegistryObject<Module<?>> CHAOTIC_AUTO_FEED = MODULES != null ? MODULES.register(
        "chaotic_auto_feed",
        () -> {
            ModuleImpl<?> module = new ModuleImpl<>(
                ModuleTypes.AUTO_FEED,
                TechLevel.CHAOTIC,
                autoFeedData(1500)
            );
            module.setMaxInstall(1);
            module.setModuleItem(RegistryModuleItem.ITEM_CHAOTIC_AUTO_FEED.get());
            return module;
        }
    ): null;

    // 创造自动进食
    public static final RegistryObject<Module<?>> CREATE_AUTO_FEED = MODULES != null ? MODULES.register(
        "create_auto_feed",
        () -> {
            ModuleImpl<?> module = new ModuleImpl<>(
                ModuleTypes.AUTO_FEED,
                TechLevel.CHAOTIC,
                autoFeedData(Integer.MAX_VALUE-1),
                2,2
            );
            module.setMaxInstall(1);
            module.setModuleItem(RegistryModuleItem.ITEM_CREATE_AUTO_FEED.get());
            return module;
        }
    ): null;

    // 创造伤害
    public static final RegistryObject<Module<?>> CREATE_DAMAGE = MODULES != null ? MODULES.register(
        "create_damage",
        () -> {
            ModuleImpl<?> module = new ModuleImpl<>(
                ModuleTypes.DAMAGE,
                TechLevel.CHAOTIC,
                damageData(Integer.MAX_VALUE-1),
                2,2
            );
            module.setMaxInstall(1);
            module.setModuleItem(RegistryModuleItem.ITEM_CREATE_DAMAGE.get());
            return module;
        }
    ): null;

    // 创造弹射物
    public static final RegistryObject<Module<?>> CREATE_PROJECTILE = MODULES != null ? MODULES.register(
        "create_projectile",
        () -> {
            ModuleImpl<?> module = new ModuleImpl<>(
                ModuleTypes.PROJ_MODIFIER,
                TechLevel.CHAOTIC,
                projectileData(16,1,1,16,Integer.MAX_VALUE-1),
                4,4
            );
            module.setMaxInstall(1);
            module.setModuleItem(RegistryModuleItem.ITEM_CREATE_PROJECTILE.get());
            return module;
        }
    ): null;

    private static Function<Module<EnergyData>, EnergyData> energyData(long defCapacity, long defTransfer) {
        return e -> {
            long capacity = ModuleCfg.getModuleLong(e, "capacity", defCapacity);
            long transfer = ModuleCfg.getModuleLong(e, "transfer", defTransfer);
            return new EnergyData(capacity, transfer);
        };
    }

    private static Function<Module<ShieldControlData>, ShieldControlData> shieldControl(double defSeconds) {
        return e -> {
            int ticks = ModuleCfg.getModuleInt(e, "cool_down_ticks", (int) (defSeconds * 20D));
            return new ShieldControlData(ticks);
        };
    }

    private static Function<Module<ShieldData>, ShieldData> shieldData(int defCapacity, double defRechargePerSecond) {
        return e -> {
            int capacity = ModuleCfg.getModuleInt(e, "capacity", defCapacity);
            double recharge = ModuleCfg.getModuleDouble(e, "recharge", defRechargePerSecond / 20); //Convert to per-tick
            return new ShieldData(capacity, recharge);
        };
    }

    private static Function<Module<AutoFeedData>, AutoFeedData> autoFeedData(float defFoodStorage) {
        return e -> {
            float foodStorage = (float) ModuleCfg.getModuleDouble(e, "food_storage", defFoodStorage);
            return new AutoFeedData(foodStorage);
        };
    }

    private static Function<Module<DamageData>, DamageData> damageData(double defDamage) {
        return e -> new DamageData(ModuleCfg.getModuleDouble(e, "damage_boost", defDamage));
    }

    private static Function<Module<ProjectileData>, ProjectileData> projectileData(float defVelocityModifier, float defAccuracyModifier, float defAntiGravModifier, float defPenetrationModifier, float defDamageModifier) {
        return e -> {
            float velocityModifier = (float) ModuleCfg.getModuleDouble(e, "velocity_modifier", defVelocityModifier);
            float accuracyModifier = (float) ModuleCfg.getModuleDouble(e, "accuracy_modifier", defAccuracyModifier);
            float antiGravModifier = (float) ModuleCfg.getModuleDouble(e, "anti_grav_modifier", defAntiGravModifier);
            float penetrationModifier = (float) ModuleCfg.getModuleDouble(e, "penetration_modifier", defPenetrationModifier);
            float damageModifier = (float) ModuleCfg.getModuleDouble(e, "damage_modifier", defDamageModifier);
            return new ProjectileData(velocityModifier, accuracyModifier, antiGravModifier, penetrationModifier, damageModifier);
        };
    }
}
