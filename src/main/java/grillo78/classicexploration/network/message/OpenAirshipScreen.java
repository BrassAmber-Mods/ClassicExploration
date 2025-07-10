package grillo78.classicexploration.network.message;

import grillo78.classicexploration.entities.Airship;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenAirshipScreen implements Message<OpenAirshipScreen>{

    public OpenAirshipScreen() {
    }

    @Override
    public void encode(OpenAirshipScreen message, FriendlyByteBuf buffer) {
    }

    @Override
    public OpenAirshipScreen decode(FriendlyByteBuf buffer) {
        return new OpenAirshipScreen();
    }

    @Override
    public void handle(OpenAirshipScreen message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(()->{
            Player player = supplier.get().getSender();
            if(player.getVehicle() != null && player.getVehicle() instanceof Airship){
                ((Airship) player.getVehicle()).createMenu(player);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
