package com.yueyingiop.ALG.item.DraconiCevolution.entity;

import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleContext;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.init.DEModules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yueyingiop.ALG.item.DraconiCevolution.ALGItemData;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EffectData;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class EffectRemoveEntity extends ModuleEntity<EffectData> {
    private EffectData effects = EffectData.EMPTY;
    
    public static final Codec<EffectRemoveEntity> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            DEModules.codec().fieldOf("module").forGetter(EffectRemoveEntity::getModule),
            Codec.INT.fieldOf("gridx").forGetter(ModuleEntity::getGridX),
            Codec.INT.fieldOf("gridy").forGetter(ModuleEntity::getGridY),
            EffectData.CODEC.optionalFieldOf("effects", EffectData.EMPTY).forGetter(e -> EffectData.EMPTY)
    ).apply(builder, EffectRemoveEntity::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectRemoveEntity> STREAM_CODEC = StreamCodec.composite(
            DEModules.streamCodec(), ModuleEntity::getModule,
            ByteBufCodecs.INT, ModuleEntity::getGridX,
            ByteBufCodecs.INT, ModuleEntity::getGridY,
            EffectData.STREAM_CODEC, e -> EffectData.EMPTY,
            EffectRemoveEntity::new
    );

    public EffectRemoveEntity(Module<EffectData> module) {
        super(module);
    }

    @SuppressWarnings("unchecked")
    EffectRemoveEntity(Module<?> module, int gridX, int gridY, EffectData dummy) {
        super((Module<EffectData>) module, gridX, gridY);
    }

    //#region 核心处理函数
    @Override
    public void tick(ModuleContext context) {
        super.tick(context);
    }

    @Override
    public void onInstalled(ModuleContext context) {
        // TODO Auto-generated method stub
        super.onInstalled(context);
    }

    @Override
    public void onRemoved(ModuleContext context) {
        // TODO Auto-generated method stub
        super.onRemoved(context);
    }

    //#endregion

    @Override
    public Module<EffectData> getModule() {
        return super.getModule();
    }

    public EffectData getEffects() {
        return effects;
    }

    @Override
    public ModuleEntity<?> copy() {
        return new EffectRemoveEntity(module, getGridX(), getGridY(), effects);
    }

    // 存储
    @Override
    public void saveEntityToStack(ItemStack stack, ModuleContext context) {
        stack.set(ALGItemData.EFFECT_REMOVE_MODULE.get(), effects);
        markDirty();
    }

    // 加载
    @Override
    public void loadEntityFromStack(ItemStack stack, ModuleContext context) {
        effects = stack.getOrDefault(ALGItemData.EFFECT_REMOVE_MODULE.get(), EffectData.EMPTY);
    }
}
