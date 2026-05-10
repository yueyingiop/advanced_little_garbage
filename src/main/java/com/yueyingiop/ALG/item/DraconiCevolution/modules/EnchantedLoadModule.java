package com.yueyingiop.ALG.item.DraconiCevolution.modules;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.modules.data.ModuleProperties;
import com.brandon3055.draconicevolution.api.modules.lib.BaseModule;
import com.yueyingiop.ALG.item.DraconiCevolution.data.EnchantedData;
import com.yueyingiop.ALG.item.DraconiCevolution.type.EnchantedLoadModuleType;

import net.minecraft.world.item.Item;

public class EnchantedLoadModule extends BaseModule<EnchantedData> {
    private final Item item;
    public final TechLevel techLevel;
    private final int maxEnchantments;

    public EnchantedLoadModule(Item item, TechLevel techLevel, int maxEnchantments) {
        super(
            EnchantedLoadModuleType.INSTANCE, 
            new ModuleProperties<>(
                techLevel,
                m -> EnchantedData.EMPTY
            )
        );
        this.item = item;
        this.techLevel = techLevel;
        this.maxEnchantments = maxEnchantments;
    }

    @Override
    public Item getItem() {
        return item;
    }

    public int maxEnchantments() {
        return maxEnchantments;
    }

    public int maxInstallable() {
        return 1;
    }
}
