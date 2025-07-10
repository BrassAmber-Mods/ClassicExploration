package grillo78.classicexploration.items;

import grillo78.classicexploration.ClassicExploration;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ClassicExploration.MOD_ID);

    public static final RegistryObject<ShipItem> AIRSHIP = register("airship", ()->new ShipItem(new Item.Properties()));
    public static final RegistryObject<Item> BALLOON = register("balloon", ()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENGINE = register("engine", ()->new Item(new Item.Properties()));

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
