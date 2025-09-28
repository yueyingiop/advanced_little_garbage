package com.core.ALG.util;

import net.minecraftforge.fml.ModList;

public class ModConditions {
    public static boolean isDraconicEvolutionLoaded() {
        return ModList.get().isLoaded("draconicevolution");
    }
}
