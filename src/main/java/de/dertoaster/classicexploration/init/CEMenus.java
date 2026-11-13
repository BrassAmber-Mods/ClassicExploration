package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.inventory.AirshipMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CEMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ClassicExplorationMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AirshipMenu>> AIRSHIP_MENU = MENUS.register(
            "airship",
            () -> IMenuTypeExtension.create(AirshipMenu::fromNetwork)
    );
}