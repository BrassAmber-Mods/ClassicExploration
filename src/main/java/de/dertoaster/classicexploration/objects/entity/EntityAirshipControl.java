package de.dertoaster.classicexploration.objects.entity;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.network.packet.CPacketAirshipControls;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityAirshipControl {
	
	@OnlyIn(Dist.CLIENT)
	static void updateClientControls(EntityAirship airship) {
		// Grab inputs, then create a packet and then send it
		Minecraft minecraft = Minecraft.getInstance();
		// IS the minecraft player actually our controlling passenger?
		if (airship.getControllingPassenger() == minecraft.player) {
			GameSettings settings = minecraft.options;

			boolean forward = settings.keyUp.isDown();
			boolean left = settings.keyLeft.isDown();
			boolean right = settings.keyRight.isDown();
			boolean backward = settings.keyDown.isDown();

			boolean ascend = settings.keyJump.isDown();
			boolean descend = settings.keySprint.isDown();

			// Now, construct a packet and send it to the server...
			// Then, in a different part of the entity, handle the packet and react accordingly
			CPacketAirshipControls controlPacket = new CPacketAirshipControls(forward, left, right, backward, ascend, descend);
			ClassicExplorationMod.NETWORK.sendToServer(controlPacket);
		}
	}

}
