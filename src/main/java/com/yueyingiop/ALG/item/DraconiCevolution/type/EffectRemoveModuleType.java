package com.yueyingiop.ALG.item.DraconiCevolution.type;

import java.util.Collection;
import java.util.Set;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.mojang.serialization.Codec;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.item.DraconiCevolution.entity.EffectRemoveEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EffectRemoveModuleType  implements ModuleType<EffectData> {
    public static final EffectRemoveModuleType INSTANCE = new EffectRemoveModuleType();

    private EffectRemoveModuleType() {}

    @Override
    public Collection<ModuleCategory> getCategories() {
        return Set.of(ModuleCategory.CHESTPIECE);
    }

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
        return "effect_remove";
    }

    @Override
    public ModuleEntity<?> createEntity(Module<EffectData> module) {
        return new EffectRemoveEntity(module);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Codec<ModuleEntity<?>> entityCodec() {
        return (Codec<ModuleEntity<?>>) (Codec<?>) EffectRemoveEntity.CODEC;
    }

    @Override
    @SuppressWarnings("unchecked")
    public StreamCodec<RegistryFriendlyByteBuf, ModuleEntity<?>> entityStreamCodec() {
        return (StreamCodec<RegistryFriendlyByteBuf, ModuleEntity<?>>) (StreamCodec<?, ?>) EffectRemoveEntity.STREAM_CODEC;
    }
}
