package grillo78.classicexploration.network;

import grillo78.classicexploration.ClassicExploration;
import grillo78.classicexploration.network.message.Message;
import grillo78.classicexploration.network.message.OpenAirshipScreen;
import grillo78.classicexploration.network.message.ShootArrow;
import grillo78.classicexploration.network.message.UpdateAirshipInput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE;
    private static int nextId = 0;

    /**
     * create the network channel and register the packets
     */
    public static void init() {
        // Create the Network channel
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ClassicExploration.MOD_ID, "network"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();
        register(UpdateAirshipInput.class, new UpdateAirshipInput());
        register(OpenAirshipScreen.class, new OpenAirshipScreen());
        register(ShootArrow.class, new ShootArrow());
    }


    /**
     * Method to register a packet
     *
     * @param clazz   Class of the packet
     * @param message Message object
     * @param <T>
     */
    private static <T> void register(Class<T> clazz, Message<T> message) {
        INSTANCE.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }
}
