package de.dertoaster.classicexploration.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;

public class CESounds {

	public static final SoundEvent ENTITY_AIRSHIP_PROPELLOR_LOOP = createEvent("entity.airship.propellor");
	public static final SoundEvent ENTITY_AIRSHIP_CANNON = createEvent("entity.airship.cannon.shoot");

	private static SoundEvent createEvent(String sound) {
		ResourceLocation name = ClassicExplorationMod.prefix(sound);
		return new SoundEvent(name).setRegistryName(name);

	}

	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		Field[] allFields = CESounds.class.getDeclaredFields();

		for (Field f : allFields) {
			if (!Modifier.isPrivate(f.getModifiers())) {
				try {
					if (f.get(null) instanceof SoundEvent) {
						event.getRegistry().register((SoundEvent) f.get(null));
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
