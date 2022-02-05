package de.dertoaster.classicexploration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dertoaster.classicexploration.init.CEItems;
import de.dertoaster.classicexploration.init.CEMessages;
import de.dertoaster.classicexploration.init.CESounds;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import software.bernie.geckolib3.GeckoLib;

@Mod("classicexploration")
public class ClassicExplorationMod {
	// Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MODID = "classicexploration";
    
    private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main-network-channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public ClassicExplorationMod() {
    	//Initialize Geckolib
    	GeckoLib.initialize();
    	
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		
		MinecraftForge.EVENT_BUS.register(this);
		CEItems.ITEMS.register(modbus);
		modbus.addGenericListener(SoundEvent.class, CESounds::registerSounds);
		
		CEMessages.registerMessages();
    }


	public static ResourceLocation prefix(String sound) {
		return new ResourceLocation(MODID, sound);
	}
}
