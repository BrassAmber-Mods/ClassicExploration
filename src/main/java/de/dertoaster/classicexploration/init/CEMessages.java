package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.network.packet.CPacketAirshipControls;
import de.dertoaster.classicexploration.network.packet.CPacketFireAirshipArrow;
import de.dertoaster.classicexploration.network.packet.CPacketOpenAirshipInventory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CEMessages {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CEMessages::onRegisterPayloadHandlers);
    }

    private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ClassicExplorationMod.MODID).versioned("1");
        registrar.playToServer(CPacketAirshipControls.TYPE, CPacketAirshipControls.STREAM_CODEC, CPacketAirshipControls::handle);
        registrar.playToServer(CPacketOpenAirshipInventory.TYPE, CPacketOpenAirshipInventory.STREAM_CODEC, CPacketOpenAirshipInventory::handle);
        registrar.playToServer(CPacketFireAirshipArrow.TYPE, CPacketFireAirshipArrow.STREAM_CODEC, CPacketFireAirshipArrow::handle);
    }
}
