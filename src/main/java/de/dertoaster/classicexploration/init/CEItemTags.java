package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CEItemTags {
    public static final TagKey<Item> CHESTS = TagKey.create(Registries.ITEM, ClassicExplorationMod.prefix("chests"));
}
