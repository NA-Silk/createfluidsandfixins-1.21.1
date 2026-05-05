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
            ResourceLocation.fromNamespaceAndPath(CreateFluidsAndFixins.MOD_ID,
                    "fluid/source_densite_emulsion");
    public static final ResourceLocation DENSITE_FLOWING_RL =
            ResourceLocation.fromNamespaceAndPath(CreateFluidsAndFixins.MOD_ID,
                    "fluid/flowing_densite_emulsion");
    public static final Supplier<FluidType> DENSITE_EMULSION_FLUID_TYPE =
            registerFluidType("densite_emulsion_fluid_type",
                    new BaseFluidType(
                            DENSITE_STILL_RL,
                            DENSITE_FLOWING_RL,
                            null,
                            0xA1343E69,
                            new Vector3f(108f / 255f, 168f / 255f, 212f / 255f),
                            FluidType.Properties.create()
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
