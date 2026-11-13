package de.dertoaster.classicexploration.network.packet;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CPacketAirshipControls(int airshipId, boolean forward, boolean left, boolean right, boolean backward, boolean ascend, boolean descend) implements CustomPacketPayload {
    public static final Type<CPacketAirshipControls> TYPE = new Type<>(ClassicExplorationMod.prefix("airship_controls"));

    public static final StreamCodec<FriendlyByteBuf, CPacketAirshipControls> STREAM_CODEC = StreamCodec.ofMember(CPacketAirshipControls::write, CPacketAirshipControls::read);

    public static CPacketAirshipControls read(FriendlyByteBuf buf) {
        return new CPacketAirshipControls(
                buf.readInt(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.airshipId);
        buf.writeBoolean(this.forward);
        buf.writeBoolean(this.left);
        buf.writeBoolean(this.right);
        buf.writeBoolean(this.backward);
        buf.writeBoolean(this.ascend);
        buf.writeBoolean(this.descend);
    }

    public static void handle(CPacketAirshipControls packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sender) {
                Entity vehicle = sender.level().getEntity(packet.airshipId());
                if (vehicle instanceof EntityAirship airship && sender.getVehicle() == airship) {
                    airship.processControlInputs(new boolean[]{packet.forward(), packet.left(), packet.right(), packet.backward(), packet.ascend(), packet.descend()});
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
