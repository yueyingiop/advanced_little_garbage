package com.yueyingiop.ALG;

import java.util.List;

import com.yueyingiop.ALG.util.ModConditions;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ALGConfig {
    private static boolean DE_load = ModConditions.isDraconicEvolutionLoaded();

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    //#region 龙之研究
    public static final ModConfigSpec.IntValue CREATE_EFFECT_LOAD_LEVEL;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CREATE_EFFECT_LOAD_BLACKLIST;

    static {
        if (DE_load) {
            BUILDER.push("draconic_evolution");
            CREATE_EFFECT_LOAD_LEVEL = BUILDER
                    .comment("创造效果加载模块的效果等级")
                    .defineInRange("create_effect_load_level", 4, 0, 255);
            
            CREATE_EFFECT_LOAD_BLACKLIST = BUILDER
                    .comment("创造效果加载模块的效果黑名单，填写效果的资源位置，如minecraft:speed")
                    .defineListAllowEmpty(
                        "create_effect_load_blacklist", 
                        List.of("minecraft:slow_falling"), 
                        () -> "", 
                        s -> s instanceof String str && str.contains(":")
                    );
            
            BUILDER.pop();
        } else {
            CREATE_EFFECT_LOAD_LEVEL = null;
            CREATE_EFFECT_LOAD_BLACKLIST = null;
        }
        
    }
    //#endregion

    static final ModConfigSpec SPEC = BUILDER.build();
}
