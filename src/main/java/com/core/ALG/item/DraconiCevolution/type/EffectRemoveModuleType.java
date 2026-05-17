package com.core.ALG.item.DraconiCevolution.type;

import java.util.Collection;
import java.util.Set;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.core.ALG.item.DraconiCevolution.data.EffectData;
import com.core.ALG.item.DraconiCevolution.entity.EffectRemoveEntity;

@SuppressWarnings("rawtypes")
public class EffectRemoveModuleType implements ModuleType<EffectData> {

    public static final EffectRemoveModuleType INSTANCE = new EffectRemoveModuleType();

    private EffectRemoveModuleType() {}

    @Override
    public int getDefaultWidth() {
        return 2;
    }

    @Override
    public int getDefaultHeight() {
        return 2;
    }

    @Override
    public String getName() {
        return "effect_load";
    }

    @Override
    public ModuleEntity createEntity(Module<EffectData> module) {
        return new EffectRemoveEntity(module);
    }

    @Override
    public Collection<ModuleCategory> getCategories() {
        return Set.of(ModuleCategory.CHESTPIECE);
    }

}

