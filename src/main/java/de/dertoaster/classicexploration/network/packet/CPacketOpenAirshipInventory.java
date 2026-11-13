package de.dertoaster.classicexploration.network.packet;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CPacketOpenAirshipInventory(int airshipId) implements CustomPacketPayload {
    public static final Type<CPacketOpenAirshipInventory> TYPE = new Type<>(ClassicExplorationMod.prefix("open_airship_inventory"));

    public static final StreamCodec<FriendlyByteBuf, CPacketOpenAirshipInventory> STREAM_CODEC = StreamCodec.ofMember(CPacketOpenAirshipInventory::write, CPacketOpenAirshipInventory::read);

    public static CPacketOpenAirshipInventory read(FriendlyByteBuf buf) {
        return new CPacketOpenAirshipInventory(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.airshipId);
    }

    public static void handle(CPacketOpenAirshipInventory packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sender) {
                Entity entity = sender.level().getEntity(packet.airshipId());
                if (entity instanceof EntityAirship airship && airship.isAlive() && sender.distanceToSqr(airship) <= 64.0D) {
                    // Allow if riding the airship or nearby interacting context
                    if (sender.getVehicle() == airship || sender.distanceToSqr(airship) <= 64.0D) {
                        airship.openInventoryFor(sender);
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}