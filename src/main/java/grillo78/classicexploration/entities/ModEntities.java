package grillo78.classicexploration.entities;

import grillo78.classicexploration.ClassicExploration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ClassicExploration.MOD_ID);

    public static final RegistryObject<EntityType<Airship>> AIRSHIP = register("airship", () -> EntityType.Builder
            .of(Airship::new, MobCategory.CREATURE).sized(3F, 5F)
            .build(ClassicExploration.MOD_ID + ":airship"));


    private static <T extends EntityType<?>> RegistryObject<T> register(String name, Supplier<T> containerType) {
        return ENTITIES.register(name, containerType);
    }
}
