package grillo78.classicexploration;

import com.mojang.logging.LogUtils;
import grillo78.classicexploration.client.ModModelLayers;
import grillo78.classicexploration.client.entity.AirshipRenderer;
import grillo78.classicexploration.client.entity.model.AirshipModel;
import grillo78.classicexploration.client.screen.InventoryScreen;
import grillo78.classicexploration.client.util.InputUtil;
import grillo78.classicexploration.entities.Airship;
import grillo78.classicexploration.entities.ModEntities;
import grillo78.classicexploration.items.ModItems;
import grillo78.classicexploration.menu.ModMenus;
import grillo78.classicexploration.network.PacketHandler;
import grillo78.classicexploration.network.message.OpenAirshipScreen;
import grillo78.classicexploration.network.message.ShootArrow;
import grillo78.classicexploration.network.message.UpdateAirshipInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ClassicExploration.MOD_ID)
public class ClassicExploration {

    public static final String MOD_ID = "classicexploration";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ClassicExploration(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        PacketHandler.init();

        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenus.CONTAINERS.register(modEventBus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::doClientStuff);
            modEventBus.addListener(this::registerLayerDefinitions);
            modEventBus.addListener(this::registerRenderers);
            modEventBus.addListener(this::registerItemsInTab);
            MinecraftForge.EVENT_BUS.addListener(this::movementInputUpdate);
            MinecraftForge.EVENT_BUS.addListener(this::mouseInput);
            MinecraftForge.EVENT_BUS.addListener(this::openScreen);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void doClientStuff(FMLClientSetupEvent event) {
        MenuScreens.register(ModMenus.INVENTORY_CONTAINER.get(), InventoryScreen::new);
    }

    @OnlyIn(Dist.CLIENT)
    private void registerItemsInTab(BuildCreativeModeTabContentsEvent event) {
        if (BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(event.getTab()).get() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(ModItems.AIRSHIP.get());
        if (BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(event.getTab()).get() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BALLOON.get());
            event.accept(ModItems.ENGINE.get());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void mouseInput(final InputEvent.InteractionKeyMappingTriggered event) {
        if(event.isAttack() && Minecraft.getInstance().player.getVehicle() instanceof Airship){
            event.setSwingHand(false);
            PacketHandler.INSTANCE.sendToServer(new ShootArrow());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void movementInputUpdate(final MovementInputUpdateEvent event) {
        if (Minecraft.getInstance().player.getVehicle() != null && Minecraft.getInstance().player.getVehicle() instanceof Airship) {
            if (!InputUtil.checkAirshipInput((Airship) Minecraft.getInstance().player.getVehicle(), Minecraft.getInstance().player.input))
                PacketHandler.INSTANCE.sendToServer(new UpdateAirshipInput(Minecraft.getInstance().player.getVehicle().getId(), InputUtil.inputToArray(Minecraft.getInstance().player.input)));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.AIRSHIP, () -> AirshipModel.createBodyLayer());
    }

    @OnlyIn(Dist.CLIENT)
    public void openScreen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen && Minecraft.getInstance().player.getVehicle() != null && Minecraft.getInstance().player.getVehicle() instanceof Airship) {
            event.setCanceled(true);
            PacketHandler.INSTANCE.sendToServer(new OpenAirshipScreen());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.AIRSHIP.get(), AirshipRenderer::new);
    }
}
