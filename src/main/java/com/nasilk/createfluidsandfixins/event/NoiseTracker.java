package com.nasilk.createfluidsandfixins.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NoiseTracker {
    // Stores BlockPos with GameTime and frequency of noise
    public record NoiseEntry(long time, int frequency) {}
    private static final Map<BlockPos, NoiseEntry> RECENT_LOUD_NOISES = new ConcurrentHashMap<>();


    @SubscribeEvent
    public static void onVanillaGameEvent(VanillaGameEvent event) {
        // Get the frequency from the Holder<GameEvent>
        int frequency = VibrationSystem.getGameEventFrequency(event.getVanillaEvent());
        if (frequency >= 1) {
            BlockPos pos = BlockPos.containing(event.getEventPosition());
            // Store the position, time, and frequency
            RECENT_LOUD_NOISES.put(pos, new NoiseEntry(event.getLevel().getGameTime(), frequency));
        }
    }

    public static boolean wasLoudRecently(Level level, BlockPos pos, int minimumFrequency, int radius, int memoryTicks) {
        long currentTime = level.getGameTime();
        return RECENT_LOUD_NOISES.entrySet().stream()
            .anyMatch(entry -> {
                NoiseEntry noise = entry.getValue();
                return entry.getKey().closerThan(pos, radius) && (currentTime - noise.time()) <= memoryTicks && noise.frequency() >= minimumFrequency;
            });
    }


    // Run cleanup once every 100 ticks (5 seconds) to save CPU
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 100 == 0) {
            cleanup(event.getServer().overworld().getGameTime());
        }
    }

    // Clean entries older than 100 ticks (5 seconds)
    public static void cleanup(long currentTime) {
        RECENT_LOUD_NOISES.values().removeIf(noise -> currentTime - noise.time() > 100);
    }
}
