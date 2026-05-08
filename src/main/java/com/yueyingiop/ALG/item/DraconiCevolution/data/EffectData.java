package com.yueyingiop.ALG.item.DraconiCevolution.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yueyingiop.ALG.util.TextHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public record EffectData(Map<Holder<MobEffect>, Integer> effects) implements ModuleData<EffectData> {
    public static final EffectData EMPTY = new EffectData(Collections.emptyMap());

    public static final EffectData TEST = new EffectData(
        Map.of(MobEffects.REGENERATION, 0)
    );

    public static final Codec<EffectData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    BuiltInRegistries.MOB_EFFECT.holderByNameCodec(),
                    Codec.INT
            ).fieldOf("effects").forGetter(EffectData::effects)
    ).apply(instance, EffectData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),
                    ByteBufCodecs.VAR_INT
            ),
            EffectData::effects,
            EffectData::new
    );

    @Override
    public EffectData combine(EffectData other) {
        if (other.effects.isEmpty()) return this;
        if (this.effects.isEmpty()) return other;

        Map<Holder<MobEffect>, Integer> combined = new HashMap<>(this.effects);
        for (var entry : other.effects.entrySet()) {
            combined.merge(entry.getKey(), entry.getValue(), Math::max);
        }

        return new EffectData(
            Collections.unmodifiableMap(combined)
        );
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context) {
        for (var entry : effects.entrySet()) {
            Holder<MobEffect> effectHolder = entry.getKey();
            int amplifier = entry.getValue();
            Component name = effectHolder.value().getDisplayName();
            Component value = Component.literal(TextHelper.toRoman(amplifier + 1));
            map.put(name, value);
        }
    }
}
