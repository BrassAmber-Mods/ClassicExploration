package grillo78.classicexploration.menu;

import grillo78.classicexploration.ClassicExploration;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ClassicExploration.MOD_ID);

    public static final RegistryObject<MenuType<InventoryMenu>> INVENTORY_CONTAINER = register("inventory", new MenuType<>((windowId, playerInventory) -> new InventoryMenu(ModMenus.INVENTORY_CONTAINER.get(), windowId, playerInventory.player), FeatureFlags.DEFAULT_FLAGS));

    private static <T extends MenuType<?>> RegistryObject<T> register(String name, T containerType) {
        return CONTAINERS.register(name, () -> containerType);
    }
}
