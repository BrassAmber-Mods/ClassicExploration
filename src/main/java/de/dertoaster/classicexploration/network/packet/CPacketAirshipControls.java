package de.dertoaster.classicexploration.network.packet;

import java.util.function.Supplier;

import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CPacketAirshipControls {
	
	private int airshipId;
	private boolean controlValues[] = new boolean[6];

	public CPacketAirshipControls() {
		
	}
	
	public CPacketAirshipControls(boolean forward, boolean left, boolean right, boolean backward, boolean ascend, boolean descend) {
		this.controlValues[0] = forward;
		this.controlValues[1] = left;
		this.controlValues[2] = right;
		this.controlValues[3] = backward;
		this.controlValues[4] = ascend;
		this.controlValues[5] = descend;
	}
	
	public static CPacketAirshipControls read(PacketBuffer buffer) {
		CPacketAirshipControls packet = new CPacketAirshipControls();
		
		packet.airshipId = buffer.readInt();
		for(int i = 0; i < packet.controlValues.length; i++) {
			packet.controlValues[i] = buffer.readBoolean();
		}
		
		return packet;
	}
	
	public static void write(CPacketAirshipControls packet, PacketBuffer buffer) {
		buffer.writeInt(packet.airshipId);
		for(int i = 0; i < packet.controlValues.length; i++) {
			buffer.writeBoolean(packet.controlValues[i]);
		}
	}
	
	public static class Handler {
		
		public Handler() {
			
		}
		
		public static void handlePacket(CPacketAirshipControls packet, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				ServerPlayerEntity sender = context.get().getSender();
				if(sender != null ) {
					World level = sender.level;
					if(level != null) {
						Entity vehicle = level.getEntity(packet.getAirshipID());
						if(vehicle instanceof EntityAirship && vehicle != null && vehicle.getControllingPassenger() == (Entity)sender) {
							((EntityAirship)vehicle).processControlInputs(packet.getControlValues());
						}
					}
				}
			});
			context.get().setPacketHandled(true);
		}
		
	}

	public int getAirshipID() {
		return this.airshipId;
	}

	public boolean[] getControlValues() {
		return this.controlValues;
	}
}
