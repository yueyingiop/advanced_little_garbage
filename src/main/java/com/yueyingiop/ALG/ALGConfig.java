package com.yueyingiop.ALG;

import java.util.List;

import com.yueyingiop.ALG.util.ModConditions;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ALGConfig {
    private static boolean DE_load = ModConditions.isDraconicEvolutionLoaded();

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    //#region 龙之研究
    public static final ModConfigSpec.IntValue EFFECT_LOAD_BASE_POWER;
    public static final ModConfigSpec.IntValue EFFECT_REMOVE_BASE_POWER;
    public static final ModConfigSpec.IntValue ENCHANTED_LOAD_BASE_POWER;
    public static final ModConfigSpec.IntValue CREATE_EFFECT_LOAD_LEVEL;
    public static final ModConfigSpec.IntValue CREATE_ENCHANTED_LOAD_LEVEL;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CREATE_EFFECT_LOAD_BLACKLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CREATE_EFFECT_REMOVE_BLACKLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CREATE_ENCHANTED_LOAD_BLACKLIST;

    static {
        if (DE_load) {
            BUILDER.push("draconic_evolution");
            EFFECT_LOAD_BASE_POWER = BUILDER
                    .comment("效果加载模块的基础能量消耗")
                    .defineInRange("effect_load_base_power", 100, 0, Integer.MAX_VALUE);

            EFFECT_REMOVE_BASE_POWER = BUILDER
                    .comment("效果移除模块的基础能量消耗")
                    .defineInRange("effect_remove_base_power", 200, 0, Integer.MAX_VALUE);

            ENCHANTED_LOAD_BASE_POWER = BUILDER
                    .comment("附魔加载模块的基础能量消耗")
                    .defineInRange("enchanted_load_base_power", 50, 0, Integer.MAX_VALUE);

            CREATE_EFFECT_LOAD_LEVEL = BUILDER
                    .comment("创造效果加载模块的效果等级")
                    .defineInRange("create_effect_load_level", 4, 0, 255);

            CREATE_ENCHANTED_LOAD_LEVEL = BUILDER
                    .comment("创造附魔加载模块的附魔等级")
                    .defineInRange("create_enchanted_load_level", 10, 0, 255);

            CREATE_EFFECT_LOAD_BLACKLIST = BUILDER
                    .comment("创造效果加载模块的效果黑名单，填写效果的资源位置，如minecraft:speed")
                    .defineListAllowEmpty(
                        "create_effect_load_blacklist", 
                        List.of("minecraft:slow_falling"), 
                        () -> "", 
                        s -> s instanceof String str && str.contains(":")
                    );

            CREATE_EFFECT_REMOVE_BLACKLIST = BUILDER
                    .comment("创造效果移除模块的效果黑名单，填写效果的资源位置，如minecraft:speed")
                    .defineListAllowEmpty(
                        "create_effect_remove_blacklist", 
                        List.of("minecraft:glowing"), 
                        () -> "", 
                        s -> s instanceof String str && str.contains(":")
                    );
        
            CREATE_ENCHANTED_LOAD_BLACKLIST = BUILDER
                    .comment("创造附魔加载模块的附魔黑名单，填写附魔的资源位置，如minecraft:sharpness")
                    .defineListAllowEmpty(
                        "create_enchanted_load_blacklist", 
                        List.of("minecraft:silk_touch"), 
                        () -> "", 
                        s -> s instanceof String str && str.contains(":")
                    );
            
            BUILDER.pop();
        } else {
            EFFECT_LOAD_BASE_POWER = null;
            EFFECT_REMOVE_BASE_POWER = null;
            ENCHANTED_LOAD_BASE_POWER = null;
            CREATE_EFFECT_LOAD_LEVEL = null;
            CREATE_ENCHANTED_LOAD_LEVEL = null;
            CREATE_EFFECT_LOAD_BLACKLIST = null;
            CREATE_EFFECT_REMOVE_BLACKLIST = null;
            CREATE_ENCHANTED_LOAD_BLACKLIST = null;
        }
        
    }
    //#endregion

    static final ModConfigSpec SPEC = BUILDER.build();
}
