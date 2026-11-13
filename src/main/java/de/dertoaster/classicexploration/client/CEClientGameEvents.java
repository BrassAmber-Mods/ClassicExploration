package de.dertoaster.classicexploration.client;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.network.packet.CPacketFireAirshipArrow;
import de.dertoaster.classicexploration.network.packet.CPacketOpenAirshipInventory;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ClassicExplorationMod.MODID, value = Dist.CLIENT)
public class CEClientGameEvents {

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!(mc.player.getVehicle() instanceof EntityAirship airship)) return;
        if (event.getNewScreen() instanceof InventoryScreen) {
            event.setCanceled(true);
            PacketDistributor.sendToServer(new CPacketOpenAirshipInventory(airship.getId()));
        }
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!event.isAttack()) return;
        if (!(mc.player.getVehicle() instanceof EntityAirship airship && airship.getControllingPassenger() == mc.player))
            return;
        event.setCanceled(true);
        PacketDistributor.sendToServer(
                new CPacketFireAirshipArrow(airship.getId())
        );

    }
}