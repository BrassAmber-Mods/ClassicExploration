package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.item.ItemLore;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CEItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ClassicExplorationMod.MODID);
	
	public static final RegistryObject<Item> ITEM_ENGINE = ITEMS.register("engine", () -> new ItemLore(new Properties().tab(ItemGroup.TAB_MISC).stacksTo(64)));
	public static final RegistryObject<Item> ITEM_BALLOON = ITEMS.register("balloon", () -> new ItemLore(new Properties().tab(ItemGroup.TAB_MISC).stacksTo(64)));
	public static final RegistryObject<Item> ITEM_CANNON = ITEMS.register("cannon", () -> new ItemLore(new Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)));
	//public static final RegistryObject<Item> ITEM_AIRSHIP = ITEMS.register("airship_engine", () -> new Item(new Properties().tab(ItemGroup.TAB_MISC).stacksTo(64)));
}
