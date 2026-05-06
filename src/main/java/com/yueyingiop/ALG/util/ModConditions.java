package com.yueyingiop.ALG.util;

import net.neoforged.fml.ModList;

public class ModConditions {
    public static boolean isDraconicEvolutionLoaded() {
        return ModList.get().isLoaded("draconicevolution");
    }
}
