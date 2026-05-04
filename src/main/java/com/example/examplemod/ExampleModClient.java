package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// 这个类不会在专用服务器上加载。从这里访问客户端代码是安全的。
@Mod(value = ExampleMod.MODID, dist = Dist.CLIENT)
// 你可以使用 EventBusSubscriber 自动注册类中所有用 @SubscribeEvent 注释的静态方法
@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT)
public class ExampleModClient {
    public ExampleModClient(ModContainer container) {
        // 允许 NeoForge 为此模组的配置创建一个配置界面。
        // 配置界面可通过 Mods 界面 > 点击你的模组 > 点击配置 来访问。
        // 不要忘记在 en_us.json 文件中为你的配置选项添加翻译。
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // 一些客户端初始化代码
        ExampleMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        ExampleMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
