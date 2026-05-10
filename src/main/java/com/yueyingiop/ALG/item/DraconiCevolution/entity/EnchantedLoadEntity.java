package com.yueyingiop.ALG.item.DraconiCevolution.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext;
import com.brandon3055.draconicevolution.init.DEModules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yueyingiop.ALG.ALGConfig;
import com.yueyingiop.ALG.item.DraconiCevolution.ALGItemData;
import com.yueyingiop.ALG.item.DraconiCevolution.RegistryModuleItem;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EnchantedData;
import com.yueyingiop.ALG.item.DraconiCevolution.modules.EnchantedLoadModule;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.fml.util.thread.EffectiveSide;

public class EnchantedLoadEntity extends ModuleEntity<EnchantedData> {

    private EnchantedData enchantments = EnchantedData.EMPTY;
    private int tickCounter = 0;

    public static final Codec<EnchantedLoadEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        DEModules.codec().fieldOf("module").forGetter(EnchantedLoadEntity::getModule),
        Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
        Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
        EnchantedData.CODEC.fieldOf("enchantments").forGetter(e -> e.enchantments)
    ).apply(builder, EnchantedLoadEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantedLoadEntity> STREAM_CODEC = StreamCodec.composite(
        DEModules.streamCodec(), ModuleEntity::getModule,
        ByteBufCodecs.INT, ModuleEntity::getGridX,
        ByteBufCodecs.INT, ModuleEntity::getGridY,
        EnchantedData.STREAM_CODEC, e -> e.enchantments,
        EnchantedLoadEntity::new
    );

    public EnchantedLoadEntity(Module<EnchantedData> module) {
        super(module);
    }

    @SuppressWarnings("unchecked")
    EnchantedLoadEntity(Module<?> module, int gridX, int gridY, EnchantedData enchantments) {
        super((Module<EnchantedData>) module, gridX, gridY);
        this.enchantments = enchantments;
    }

    //#region 核心处理函数
    @Override
    public void tick(ModuleContext context) {
        if (!(context instanceof StackModuleContext stackContext && EffectiveSide.get().isServer())) return;

        if (tickCounter++ % 100 != 0) return;
        clearCaches();
        markDirty();
        if (isCreateModule()) return;

        IOPStorage storage = context.getOpStorage();
        if (storage == null) return;

        int basePower = ALGConfig.ENCHANTED_LOAD_BASE_POWER.get();
        int enchantCount = enchantments.enchantments().size();
        if (enchantCount > 0) {
            int totalLevel = enchantments.enchantments().values().stream().mapToInt(Integer::intValue).sum();
            long energyCost = (long) totalLevel * enchantCount * basePower;

            if (storage.getOPStored() < energyCost) {
                removeEnchantments(stackContext);
                return;
            }
            storage.modifyEnergyStored(-energyCost);
        }
    }

    @Override
    public void onInstalled(ModuleContext context) {
        super.onInstalled(context);
        if (!(context instanceof StackModuleContext stackContext && EffectiveSide.get().isServer())) return;
        if (isCreateModule()) {
            updateCreativeEnchantments(stackContext);
            applyEnchantments(stackContext);
        } else {
            IOPStorage storage = context.getOpStorage();
            if (storage != null) {
                applyEnchantments(stackContext);
            }
        }
    }

    @Override
    public void onRemoved(ModuleContext context) {
        super.onRemoved(context);
        if (!(context instanceof StackModuleContext stackContext && EffectiveSide.get().isServer())) return;
        removeEnchantments(stackContext);
    }
    //#endregion

    @Override
    public Module<EnchantedData> getModule() {
        return super.getModule();
    }

    public EnchantedData getEnchantments() {
        return enchantments;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new EnchantedLoadEntity(module, getGridX(), getGridY(), enchantments);
    }

    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ALGItemData.ENCHANTED_LOAD_MODULE.get(), enchantments);
        markDirty();
    }

    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        if (isCreateModule()) {
            updateCreativeEnchantments(context);
        } else {
            this.enchantments = stack.getOrDefault(ALGItemData.ENCHANTED_LOAD_MODULE.get(), EnchantedData.EMPTY);
        }
    }

    //#region 辅助函数
    /**
     * 创造模块专用：将当前所有非诅咒附魔全部写入本模块，等级由配置决定。
     */
    private void updateCreativeEnchantments(ModuleContext context) {
        Registry<Enchantment> registry = null;

        // 尝试从实体所在世界获取动态注册表
        if (context instanceof StackModuleContext stackContext) {
            LivingEntity entity = stackContext.getEntity();
            if (entity != null) {
                registry = entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            }
        }

        // 客户端回退
        if (registry == null) {
            var clientLevel = net.minecraft.client.Minecraft.getInstance().level;
            if (clientLevel != null) {
                registry = clientLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            }
        }

        if (registry == null) return;

        int level = ALGConfig.CREATE_ENCHANTED_LOAD_LEVEL.get();
        List<? extends String> blacklist = ALGConfig.CREATE_ENCHANTED_LOAD_BLACKLIST.get();
        Map<ResourceKey<Enchantment>, Integer> map = new HashMap<>();

        for (Enchantment enchantment : registry) {
            Holder<Enchantment> holder = registry.wrapAsHolder(enchantment);
            // 跳过诅咒附魔（如绑定诅咒、消失诅咒）,跳过黑名单中的附魔
            if (holder.is(EnchantmentTags.CURSE)) continue;
            ResourceKey<Enchantment> key = holder.unwrapKey().orElse(null);
            if (key == null) continue;
            if (blacklist.contains(key.location().toString())) continue;
            map.put(key, level);
        }

        this.enchantments = new EnchantedData(Collections.unmodifiableMap(map));
    }

    /**
     * 将模块存储的附魔应用到物品上
     */
    private void applyEnchantments(StackModuleContext stackContext) {
        ItemStack stack = stackContext.getStack();
        Registry<Enchantment> registry = stackContext.getEntity().level()
                .registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            for (var entry : enchantments.enchantments().entrySet()) {
                int level = entry.getValue();
                if (level <= 0) continue;
                registry.getHolder(entry.getKey())
                        .ifPresent(holder -> mutable.upgrade(holder, level));
            }
        });
    }

    /**
     * 将物品上来自于模块的附魔删除
     */
    private void removeEnchantments(StackModuleContext stackContext) {
        ItemStack stack = stackContext.getStack();
        LivingEntity entity = stackContext.getEntity();
        if (entity == null) return;

        Registry<Enchantment> registry = entity.level()
                .registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        // 1. 获取物品所有附魔，临时存储
        ItemEnchantments currentEnchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        Map<Holder<Enchantment>, Integer> tempMap = new HashMap<>();
        currentEnchants.entrySet().forEach(entry -> tempMap.put(entry.getKey(), entry.getIntValue()));

        // 2. 移除物品上的所有附魔
        if (!tempMap.isEmpty()) {
            EnchantmentHelper.updateEnchantments(stack, mutable -> {
                for (Holder<Enchantment> holder : tempMap.keySet()) {
                    mutable.set(holder, 0);
                }
            });
        }

        // 3. 根据模块上的 enchantments 移除临时存储中的对应附魔效果
        for (var entry : enchantments.enchantments().entrySet()) {
            ResourceKey<Enchantment> key = entry.getKey();
            registry.getHolder(key).ifPresent(tempMap::remove);
        }

        // 4. 将临时存储中剩余的附魔重新给予物品
        if (!tempMap.isEmpty()) {
            EnchantmentHelper.updateEnchantments(stack, mutable -> {
                for (var entry : tempMap.entrySet()) {
                    if (entry.getValue() > 0) {
                        mutable.upgrade(entry.getKey(), entry.getValue());
                    }
                }
            });
        }
    }
    

    private boolean isCreateModule() {
        EnchantedLoadModule em = (EnchantedLoadModule) module;
        return em.getItem() == RegistryModuleItem.ITEM_CREATE_ENCHANTED_LOAD.get();
    }
    //#endregion
}
