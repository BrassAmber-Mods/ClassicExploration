package de.dertoaster.classicexploration.network.packet;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CPacketFireAirshipArrow(int airshipId) implements CustomPacketPayload {
    public static final Type<CPacketFireAirshipArrow> TYPE = new Type<>(ClassicExplorationMod.prefix("fire_airship_arrow"));

    public static final StreamCodec<FriendlyByteBuf, CPacketFireAirshipArrow> STREAM_CODEC = StreamCodec.ofMember(CPacketFireAirshipArrow::write, CPacketFireAirshipArrow::read);

    public static CPacketFireAirshipArrow read(FriendlyByteBuf buf) {
        return new CPacketFireAirshipArrow(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.airshipId);
    }

    public static void handle(CPacketFireAirshipArrow packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sender) {
                Entity vehicle = sender.level().getEntity(packet.airshipId());
                if (vehicle instanceof EntityAirship airship && airship.getControllingPassenger() == sender) {
                    airship.shootArrow();
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
