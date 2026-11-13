package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.item.ItemAirship;
import de.dertoaster.classicexploration.objects.item.ItemLore;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CEItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ClassicExplorationMod.MODID);

    public static final DeferredItem<Item> ITEM_ENGINE = ITEMS.register("engine", () -> new ItemLore(new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> ITEM_BALLOON = ITEMS.register("balloon", () -> new ItemLore(new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> ITEM_CANNON = ITEMS.register("cannon", () -> new ItemLore(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> ITEM_AIRSHIP = ITEMS.register("airship_item", () -> new ItemAirship(new Item.Properties().stacksTo(1)));
}
