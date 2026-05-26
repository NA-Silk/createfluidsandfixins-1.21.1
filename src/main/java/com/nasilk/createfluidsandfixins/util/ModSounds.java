package com.nasilk.createfluidsandfixins.util;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
        DeferredRegister.create(Registries.SOUND_EVENT, CreateFluidsAndFixins.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PEBBLE_PLACE = SOUND_EVENTS.register(
        "block.pebble_place",
        () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(
                CreateFluidsAndFixins.MOD_ID, "block.pebble_place"
            )
        )
    );

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
