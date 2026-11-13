package de.dertoaster.classicexploration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dertoaster.classicexploration.init.CEEntities;
import de.dertoaster.classicexploration.init.CEItems;
import de.dertoaster.classicexploration.init.CEMenus;
import de.dertoaster.classicexploration.init.CEMessages;
import de.dertoaster.classicexploration.init.CESounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
@Mod(ClassicExplorationMod.MODID)
public class ClassicExplorationMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "classicexploration";

    public ClassicExplorationMod(IEventBus modEventBus) {
        CEItems.ITEMS.register(modEventBus);
        CESounds.SOUND_EVENTS.register(modEventBus);
        CEEntities.ENTITY_TYPES.register(modEventBus);
        CEMenus.MENUS.register(modEventBus);
        CEMessages.register(modEventBus);
    }

    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}
