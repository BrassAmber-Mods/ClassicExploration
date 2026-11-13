package de.dertoaster.classicexploration.init;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CESounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, ClassicExplorationMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_AIRSHIP_PROPELLOR_LOOP = SOUND_EVENTS.register(
            "entity.airship.propellor", () -> SoundEvent.createVariableRangeEvent(ClassicExplorationMod.prefix("entity.airship.propellor"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_AIRSHIP_CANNON = SOUND_EVENTS.register(
            "entity.airship.cannon.shoot", () -> SoundEvent.createVariableRangeEvent(ClassicExplorationMod.prefix("entity.airship.cannon.shoot"))
    );
}
