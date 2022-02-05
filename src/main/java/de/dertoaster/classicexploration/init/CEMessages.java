package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.network.packet.CPacketAirshipControls;

public class CEMessages {
	
	// Start the IDs at 1 so any unregistered messages (ID 0) throw a more obvious exception when received
	private static int messageId = 1;

	public static final void registerMessages() {
		ClassicExplorationMod.NETWORK.registerMessage(
				messageId++, 
				CPacketAirshipControls.class, 
				CPacketAirshipControls::write, 
				CPacketAirshipControls::read, 
				CPacketAirshipControls.Handler::handlePacket
		);
	}
	
}
