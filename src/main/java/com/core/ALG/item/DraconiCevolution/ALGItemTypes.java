package com.core.ALG.item.DraconiCevolution;

import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.types.ModuleTypeImpl;
import com.core.ALG.item.DraconiCevolution.data.EffectData;
import com.core.ALG.item.DraconiCevolution.entity.EffectLoadEntity;
import com.core.ALG.item.DraconiCevolution.entity.EffectRemoveEntity;

public class ALGItemTypes {
    public static final ModuleType<EffectData> EFFECT_LOAD = new ModuleTypeImpl<>(
        "effect_load",     
        2, 2, 
        EffectLoadEntity::new,
        ModuleCategory.CHESTPIECE,
        ModuleCategory.ARMOR_CHEST
    ).setMaxInstallable(2);

    public static final ModuleType<EffectData> EFFECT_REMOVE = new ModuleTypeImpl<>(
        "effect_remove",     
        2, 2, 
        EffectRemoveEntity::new,
        ModuleCategory.CHESTPIECE,
        ModuleCategory.ARMOR_CHEST
    ).setMaxInstallable(2);
}
