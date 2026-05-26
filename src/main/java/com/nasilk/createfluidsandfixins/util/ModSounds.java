package com.nasilk.createfluidsandfixins.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, "createfluidsandfixins");

    public static final DeferredHolder<SoundEvent, SoundEvent> PEBBLE_PLACE =
            SOUND_EVENTS.register("block.pebble_place",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath("createfluidsandfixins", "block.pebble_place")
                    ));
}