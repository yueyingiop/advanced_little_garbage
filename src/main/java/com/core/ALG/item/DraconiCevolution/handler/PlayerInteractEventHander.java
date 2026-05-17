package com.core.ALG.item.DraconiCevolution.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.items.ModuleItem;
import com.core.ALG.ALG;
import com.core.ALG.item.DraconiCevolution.RegistryModuleItem;
import com.core.ALG.item.DraconiCevolution.data.EffectData;
// import com.core.ALG.item.DraconiCevolution.data.EnchantedData;
import com.core.ALG.item.DraconiCevolution.moduleItem.EffectModuleItem;
// import com.core.ALG.item.DraconiCevolution.moduleItem.EnchantedModuleItem;
import com.core.ALG.item.DraconiCevolution.module.EffectLoadModule;
import com.core.ALG.item.DraconiCevolution.module.EffectRemoveModule;
// import com.core.ALG.item.DraconiCevolution.module.EnchantedLoadModule;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ALG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("null")
public class PlayerInteractEventHander {

    @SubscribeEvent
    public void moduleReturn(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack mainHand = event.getItemStack();           // 主手（媒介）
        ItemStack offHand = player.getOffhandItem();         // 副手（模块）

        if (offHand.isEmpty() || mainHand.isEmpty()) return;
        if (!player.isShiftKeyDown()) return;

        if (offHand.getItem() instanceof EffectModuleItem) {
            handleEffectReturn(player, mainHand, offHand, event);
        }

        // else if (offHand.getItem() instanceof EnchantedModuleItem) {
        //     handleEnchantReturn(player, mainHand, offHand, event);
        // }
    }

    private static void handleEffectReturn(ServerPlayer player, ItemStack mainHand, ItemStack offHand, PlayerInteractEvent.RightClickItem event) {
        if (!mainHand.is(Items.GLASS_BOTTLE)) return;

        Module<?> rawModule = ModuleItem.getModule(offHand);
        if (!(rawModule instanceof EffectLoadModule) && !(rawModule instanceof EffectRemoveModule)) return;
    
        if (rawModule instanceof EffectLoadModule loadMod && loadMod.getItem() == RegistryModuleItem.ITEM_CREATE_EFFECT_LOAD.get()) return;
        if (rawModule instanceof EffectRemoveModule removeMod && removeMod.getItem() == RegistryModuleItem.ITEM_CREATE_EFFECT_REMOVE.get()) return;
    
        // 读取模块数据
        EffectData data = EffectData.EMPTY;
        CompoundTag tag = offHand.getTag();
        if (tag != null && tag.contains("EffectData")) {
            data = EffectData.deserializeNBT(tag.getCompound("EffectData"));
        }
        if (data.effects().isEmpty()) return;

        // 移除最后一个效果
        List<Map.Entry<MobEffect, Integer>> entries = new ArrayList<>(data.effects().entrySet());
        Map.Entry<MobEffect, Integer> lastEntry = entries.get(entries.size() - 1);

        Map<MobEffect, Integer> newMap = new HashMap<>(data.effects());
        newMap.remove(lastEntry.getKey());
        EffectData newData = new EffectData(Collections.unmodifiableMap(newMap));

        // 更新模块物品
        CompoundTag outputTag = offHand.getOrCreateTag();
        outputTag.put("EffectData", newData.serializeNBT());

        // 消耗主手玻璃瓶
        mainHand.shrink(1);
        if (mainHand.isEmpty()) {
            player.setItemInHand(event.getHand(), ItemStack.EMPTY);
        }

        // 生成药水
        MobEffectInstance instance = new MobEffectInstance(lastEntry.getKey(), 400, lastEntry.getValue());
        ItemStack potion = new ItemStack(Items.POTION);
        PotionUtils.setCustomEffects(potion, Collections.singletonList(instance));

        if (!player.getInventory().add(potion)) {
            player.drop(potion, false);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    // private static void handleEnchantReturn(ServerPlayer player, ItemStack mainHand, ItemStack offHand, PlayerInteractEvent.RightClickItem event) {
    //     if (!mainHand.is(Items.BOOK)) return;

    //     Module<?> rawModule = ModuleItem.getModule(offHand);
    //     if (!(rawModule instanceof EnchantedLoadModule enchantModule)) return;

    //     if (enchantModule.getItem() == RegistryModuleItem.ITEM_CREATE_ENCHANTED_LOAD.get()) return;

    //     // 读取模块数据
    //     EnchantedData data = offHand.getOrDefault(ALGItemData.ENCHANTED_LOAD_MODULE.get(), EnchantedData.EMPTY);
    //     if (data.enchantments().isEmpty()) return;

    //     // 移除最后一个附魔
    //     List<Map.Entry<ResourceKey<Enchantment>, Integer>> entries = new ArrayList<>(data.enchantments().entrySet());
    //     Map.Entry<ResourceKey<Enchantment>, Integer> lastEntry = entries.get(entries.size() - 1);

    //     Map<ResourceKey<Enchantment>, Integer> newMap = new HashMap<>(data.enchantments());
    //     newMap.remove(lastEntry.getKey());
    //     EnchantedData newData = new EnchantedData(Collections.unmodifiableMap(newMap));

    //     // 更新模块物品
    //     offHand.set(ALGItemData.ENCHANTED_LOAD_MODULE.get(), newData);

    //     // 消耗主手书
    //     mainHand.shrink(1);
    //     if (mainHand.isEmpty()) {
    //         player.setItemInHand(event.getHand(), ItemStack.EMPTY);
    //     }

    //     // 生成附魔书
    //     ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
    //     ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
    //     enchantments.set(Objects.requireNonNull(player.serverLevel()
    //             .registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
    //             .getHolder(lastEntry.getKey()).orElse(null)), lastEntry.getValue());
    //     enchantedBook.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());

    //     if (!player.getInventory().add(enchantedBook)) {
    //         player.drop(enchantedBook, false);
    //     }

    //     event.setCancellationResult(InteractionResult.SUCCESS);
    //     event.setCanceled(true);
    // }
}
