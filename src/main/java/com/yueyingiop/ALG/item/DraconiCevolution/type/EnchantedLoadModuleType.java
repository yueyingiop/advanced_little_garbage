package com.yueyingiop.ALG.item.DraconiCevolution.type;

import java.util.Collection;
import java.util.Set;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleType;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.mojang.serialization.Codec;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EnchantedData;
import com.yueyingiop.ALG.item.DraconiCevolution.entity.EnchantedLoadEntity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class EnchantedLoadModuleType implements ModuleType<EnchantedData> {
    public static final EnchantedLoadModuleType INSTANCE = new EnchantedLoadModuleType();

    private EnchantedLoadModuleType() {}

    @Override
    public Collection<ModuleCategory> getCategories() {
        return Set.of(ModuleCategory.ALL);
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
        return "enchanted_load";
    }

    @Override
    public ModuleEntity<?> createEntity(Module<EnchantedData> module) {
        return new EnchantedLoadEntity(module);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Codec<ModuleEntity<?>> entityCodec() {
        return (Codec<ModuleEntity<?>>) (Object) EnchantedLoadEntity.CODEC;
    }

    @Override
    @SuppressWarnings("unchecked")
    public StreamCodec<RegistryFriendlyByteBuf, ModuleEntity<?>> entityStreamCodec() {
        return (StreamCodec<RegistryFriendlyByteBuf, ModuleEntity<?>>) (Object) EnchantedLoadEntity.STREAM_CODEC;
    }

}
