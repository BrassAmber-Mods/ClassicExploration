package de.dertoaster.classicexploration.client;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.client.render.RenderAirship;
import de.dertoaster.classicexploration.client.screen.AirshipScreen;
import de.dertoaster.classicexploration.init.CEEntities;
import de.dertoaster.classicexploration.init.CEMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ClassicExplorationMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CEClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CEEntities.AIRSHIP.get(), RenderAirship::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CEMenus.AIRSHIP_MENU.get(), AirshipScreen::new);
    }
}