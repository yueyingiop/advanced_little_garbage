package com.example.examplemod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// 这里的值应与 META-INF/neoforge.mods.toml 文件中的某个条目匹配
@Mod(ExampleMod.MODID)
public class ExampleMod {
    // 在一个通用位置定义 mod id，方便所有引用
    public static final String MODID = "examplemod";
    // 直接引用 slf4j 日志记录器
    public static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个延迟注册器用于保存所有将在 "examplemod" 命名空间下注册的方块
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // 创建一个延迟注册器用于保存所有将在 "examplemod" 命名空间下注册的物品
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // 创建一个延迟注册器用于保存所有将在 "examplemod" 命名空间下注册的创造模式标签
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 创建一个 id 为 "examplemod:example_block" 的新方块，结合命名空间和路径
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // 创建一个 id 为 "examplemod:example_block" 的新方块物品，结合命名空间和路径
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // 创建一个 id 为 "examplemod:example_item" 的新食物物品，营养值 1，饱和度 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 为示例物品创建一个 id 为 "examplemod:example_tab" 的创造标签，放在战斗标签之后
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) // 你的 CreativeModeTab 标题的语言键
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // 将示例物品添加到标签中。对于你自己的标签，这种方法比事件更优先。
            }).build());

    // mod 类的构造函数是加载 mod 时运行的第一段代码。
    // FML 会识别一些参数类型，比如 IEventBus 或 ModContainer，并自动传入。
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        // 注册 commonSetup 方法用于 mod 加载
        modEventBus.addListener(this::commonSetup);

        // 将延迟注册器注册到 mod 事件总线上，以便方块被注册
        BLOCKS.register(modEventBus);
        // 将延迟注册器注册到 mod 事件总线上，以便物品被注册
        ITEMS.register(modEventBus);
        // 将延迟注册器注册到 mod 事件总线上，以便标签被注册
        CREATIVE_MODE_TABS.register(modEventBus);

        // 注册我们自己以响应服务器和我们感兴趣的其他游戏事件。
        // 只有当我们希望 *this* 类（ExampleMod）直接响应事件时，这才是必要的。
        // 如果这个类中没有 @SubscribeEvent 注释的方法，比如下面的 onServerStarting()，则不要添加这一行。
        NeoForge.EVENT_BUS.register(this);

        // 将该物品注册到创造标签
        modEventBus.addListener(this::addCreative);

        // 注册我们 mod 的 ModConfigSpec，以便 FML 可以为我们创建和加载配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 一些通用的设置代码
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // 将示例方块物品添加到建筑方块标签
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // 你可以使用 SubscribeEvent 并让事件总线发现要调用的方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // 当服务器启动时执行一些操作
        LOGGER.info("HELLO from server starting");
    }
}
