package com.core.ALG.item.DraconiCevolution.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.core.ALG.util.TextHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

@SuppressWarnings({"null","deprecation"})
public record EffectData(Map<MobEffect, Integer> effects) implements ModuleData<EffectData> {
    public static final EffectData EMPTY = new EffectData(Collections.emptyMap());

    public static final EffectData TEST = new EffectData(
        Map.of(MobEffects.REGENERATION, 0)
    );

    @Override
    public EffectData combine(EffectData other) {
        if (other.effects.isEmpty()) return this;
        if (this.effects.isEmpty()) return other;

        Map<MobEffect, Integer> combined = new HashMap<>(this.effects);
        for (var entry : other.effects.entrySet()) {
            combined.merge(entry.getKey(), entry.getValue(), (v1, v2) -> Math.max(v1, v2));
        }

        return new EffectData(Collections.unmodifiableMap(combined));
    }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context) {
        for (var entry : effects.entrySet()) {
            MobEffect effect = entry.getKey();
            int amplifier = entry.getValue();
            Component name = effect.getDisplayName();
            Component value = Component.literal(TextHelper.toRoman(amplifier + 1));
            map.put(name, value);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (var entry : effects.entrySet()) {
            CompoundTag effectTag = new CompoundTag();
            ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(entry.getKey());
            effectTag.putString("Effect", key.toString());
            effectTag.putInt("Amplifier", entry.getValue());
            list.add(effectTag);
        }
        tag.put("Effects", list);
        return tag;
    }

    public static EffectData deserializeNBT(CompoundTag tag) {
        Map<MobEffect, Integer> map = new HashMap<>();
        if (tag == null || !tag.contains("Effects")) return EMPTY;
        ListTag list = tag.getList("Effects", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag effectTag = list.getCompound(i);
            ResourceLocation key = ResourceLocation.parse(effectTag.getString("Effect"));
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(key);
            if (effect != null) {
                map.put(effect, effectTag.getInt("Amplifier"));
            }
        }
        return new EffectData(Collections.unmodifiableMap(map));
    }
}
