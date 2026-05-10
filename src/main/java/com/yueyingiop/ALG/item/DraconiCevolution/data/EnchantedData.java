package com.yueyingiop.ALG.item.DraconiCevolution.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.brandon3055.draconicevolution.api.modules.data.ModuleData;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yueyingiop.ALG.util.TextHelper;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public record EnchantedData(Map<ResourceKey<Enchantment>, Integer> enchantments) implements ModuleData<EnchantedData> {
    public static final EnchantedData EMPTY = new EnchantedData(Collections.emptyMap());

    public static final EnchantedData TEST = new EnchantedData(
        Map.of(Enchantments.SHARPNESS, 0)
    );

    public static final Codec<EnchantedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(
            ResourceKey.codec(Registries.ENCHANTMENT),
            Codec.INT
        ).fieldOf("enchantments").forGetter(EnchantedData::enchantments)
    ).apply(instance, EnchantedData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantedData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(
            HashMap::new,
            ResourceKey.streamCodec(Registries.ENCHANTMENT),
            ByteBufCodecs.VAR_INT
        ),
        EnchantedData::enchantments,
        EnchantedData::new
    );

    @Override
    public EnchantedData combine(EnchantedData other) {
        if (other.enchantments.isEmpty()) return this;
        if (this.enchantments.isEmpty()) return other;

        Map<ResourceKey<Enchantment>, Integer> combined = new HashMap<>(this.enchantments);
        for (var entry : other.enchantments.entrySet()) {
            combined.merge(entry.getKey(), entry.getValue(), Math::max);
        }

        return new EnchantedData(Collections.unmodifiableMap(combined));
    }

    // @Override
    // @SuppressWarnings({ "unchecked", "rawtypes" })
    // public void addInformation(Map<Component, Component> map, ModuleContext context) {
    //     Registry<Enchantment> registry = (Registry<Enchantment>) BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) Registries.ENCHANTMENT);
    //     for (var entry : enchantments.entrySet()) {
    //         Enchantment enchantment = registry.get(entry.getKey());
    //         if (enchantment == null) continue;

    //         int level = entry.getValue();
    //         Component name = enchantment.description();
    //         Component value = Component.literal(TextHelper.toRoman(level));
    //         map.put(name, value);
    //     }
    // }

    @Override
    public void addInformation(Map<Component, Component> map, ModuleContext context) {
        Registry<Enchantment> registry = null;

        // 优先从 StackModuleContext 获取实体所在世界的注册表
        if (context instanceof StackModuleContext stackContext) {
            LivingEntity entity = stackContext.getEntity();
            if (entity != null) {
                registry = entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            }
        }

        // 若无法从实体获取（例如在物品栏界面），尝试用客户端当前世界
        if (registry == null) {
            var clientLevel = net.minecraft.client.Minecraft.getInstance().level;
            if (clientLevel != null) {
                registry = clientLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            }
        }

        if (registry == null) return; // 实在拿不到注册表就放弃显示

        for (var entry : enchantments.entrySet()) {
            registry.getHolder(entry.getKey()).ifPresent(holder -> {
                int level = entry.getValue();
                Component name = Enchantment.getFullname(holder, level);
                Component value = Component.literal(TextHelper.toRoman(level));
                map.put(name, value);
            });
        }
    }
}
