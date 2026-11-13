package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import de.dertoaster.classicexploration.objects.entity.EntityAirshipPart;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CEEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ClassicExplorationMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EntityAirship>> AIRSHIP = ENTITY_TYPES.register(
            "airship",
            () -> EntityType.Builder.<EntityAirship>of(EntityAirship::new, MobCategory.MISC)
                    .sized(2.5F, 0.15F)
                    .clientTrackingRange(10)
                    .build(ClassicExplorationMod.prefix("airship").toString())
    );

    // Subentidad de colisión (globo), similar a las partes del Ender Dragon
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAirshipPart>> AIRSHIP_PART = ENTITY_TYPES.register(
            "airship_part",
            () -> EntityType.Builder.<EntityAirshipPart>of((type, level) -> {
                        throw new UnsupportedOperationException("EntityAirshipPart must be created by EntityAirship");
                    }, MobCategory.MISC)
                    .sized(3.0F, 3.0F)
                    .clientTrackingRange(10)
                    .noSave()
                    .noSummon()
                    .build(ClassicExplorationMod.prefix("airship_part").toString())
    );
}
