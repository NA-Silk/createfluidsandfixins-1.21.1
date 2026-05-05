package com.nasilk.createfluidsandfixins.fluid;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ModFluidTypes {
    /* DEFAULT
     * public static final ResourceLocation WATER_STILL_RL = ResourceLocation.parse("block/water_still");
     * public static final ResourceLocation WATER_FLOWING_RL = ResourceLocation.parse("block/water_flow");
     * public static final ResourceLocation WATER_OVERLAY_RL = ResourceLocation.parse("block/water_overlay");
     */

    public static final DeferredRegister<FluidType> FLUID_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, CreateFluidsAndFixins.MOD_ID);

    // DENSITE EMULSION
    public static final ResourceLocation DENSITE_STILL_RL =
        ResourceLocation.fromNamespaceAndPath(
            CreateFluidsAndFixins.MOD_ID,
            "block/source_densite_emulsion");
    public static final ResourceLocation DENSITE_FLOWING_RL =
        ResourceLocation.fromNamespaceAndPath(
            CreateFluidsAndFixins.MOD_ID,
            "block/flowing_densite_emulsion");
    public static final Supplier<FluidType> DENSITE_EMULSION_FLUID_TYPE =
        registerFluidType(
            "densite_emulsion_fluid_type",
            new BaseFluidType(
                DENSITE_STILL_RL,
                DENSITE_FLOWING_RL,
                null,
                new Vector3f(0.6f, 0.8f, 1.0f), // glow color
                FluidType.Properties.create()
                .lightLevel(15) // glow
                .viscosity(1500) // thicker than water
                .density(1500)
            )
        );

    // NEXT FLUID ...

    private static Supplier<FluidType> registerFluidType(String name, FluidType fluidType) {
        return FLUID_TYPES.register(name, () -> fluidType);
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
