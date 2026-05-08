package com.yueyingiop.ALG.item.DraconiCevolution.type;

import java.util.Collection;
import java.util.Set;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.mojang.serialization.Codec;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;
import com.yueyingiop.ALG.item.DraconiCevolution.entity.EffectLoadEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EffectLoadModuleType implements ModuleType<EffectData> {
    public static final EffectLoadModuleType INSTANCE = new EffectLoadModuleType();

    private EffectLoadModuleType() {}

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
        return "effect_load";
    }

    @Override
    public ModuleEntity<?> createEntity(Module<EffectData> module) {
        return new EffectLoadEntity(module);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Codec<ModuleEntity<?>> entityCodec() {
        return (Codec<ModuleEntity<?>>) (Codec<?>) EffectLoadEntity.CODEC;
    }

    @Override
    @SuppressWarnings("unchecked")
    public StreamCodec<RegistryFriendlyByteBuf, ModuleEntity<?>> entityStreamCodec() {
        return (StreamCodec<RegistryFriendlyByteBuf, ModuleEntity<?>>) (StreamCodec<?, ?>) EffectLoadEntity.STREAM_CODEC;
    }
}
