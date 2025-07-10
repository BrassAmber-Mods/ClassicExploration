package grillo78.classicexploration.network.message;

import grillo78.classicexploration.entities.Airship;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateAirshipInput implements Message<UpdateAirshipInput>{
    private int id;
    private boolean[] input;

    public UpdateAirshipInput() {
    }

    public UpdateAirshipInput(int id, boolean[] input) {
        this.id = id;
        this.input = input;
    }

    @Override
    public void encode(UpdateAirshipInput message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.id);
        buffer.writeBoolean(message.input[0]);
        buffer.writeBoolean(message.input[1]);
        buffer.writeBoolean(message.input[2]);
        buffer.writeBoolean(message.input[3]);
        buffer.writeBoolean(message.input[4]);
        buffer.writeBoolean(message.input[5]);
    }

    @Override
    public UpdateAirshipInput decode(FriendlyByteBuf buffer) {
        return new UpdateAirshipInput(buffer.readInt(), new boolean[]{buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean()});
    }

    @Override
    public void handle(UpdateAirshipInput message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(()->{
            Airship pilder = (Airship) supplier.get().getSender().level().getEntity(message.id);
            pilder.setInput(message.input);
        });
        supplier.get().setPacketHandled(true);
    }
}
