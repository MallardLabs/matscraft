package com.mallardlabs.matscraft.sound;

import com.mallardlabs.matscraft.MatsCraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent CHAINSAW_CUT = registerSoundEvent("chainsaw_cut");
    public static final SoundEvent CHAINSAW_PULL = registerSoundEvent("chainsaw_pull");

    private static SoundEvent registerSoundEvent(String name) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(MatsCraft.MOD_ID, name),
                SoundEvent.of(Identifier.of(MatsCraft.MOD_ID, name)));
    }

    public static void registerSounds() {
        MatsCraft.LOGGER.info("Registering Mod Sounds for " + MatsCraft.MOD_ID);
    }
}
