package com.nasilk.createfluidsandfixins.fluid;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.block.ModBlocks;
import com.nasilk.createfluidsandfixins.util.FluidTransformationSettings;
import com.nasilk.createfluidsandfixins.fluid.flowingfluid.TransformBaseFlowingFluid;
import com.nasilk.createfluidsandfixins.fluid.flowingfluid.UpwardBaseFlowingFluid;
import com.nasilk.createfluidsandfixins.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
        DeferredRegister.create(BuiltInRegistries.FLUID, CreateFluidsAndFixins.MOD_ID);


    // VOID SEA SLURRY
    public static final Supplier<FlowingFluid> SOURCE_VOID_SEA_SLURRY = FLUIDS.register(
        "source_void_sea_slurry",
        () -> new BaseFlowingFluid.Source(ModFluids.VOID_SEA_SLURRY_PROPERTIES)
    );
    public static final Supplier<FlowingFluid> FLOWING_VOID_SEA_SLURRY = FLUIDS.register(
        "flowing_void_sea_slurry",
        () -> new BaseFlowingFluid.Flowing(ModFluids.VOID_SEA_SLURRY_PROPERTIES)
    );

    public static final DeferredBlock<LiquidBlock> VOID_SEA_SLURRY_BLOCK = ModBlocks.BLOCKS.register(
        "void_sea_slurry_block",
        () -> new LiquidBlock(ModFluids.SOURCE_VOID_SEA_SLURRY.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable())
    );
    public static final DeferredItem<Item> VOID_SEA_SLURRY_BUCKET = ModItems.ITEMS.registerItem(
        "void_sea_slurry_bucket",
        properties -> new BucketItem(ModFluids.SOURCE_VOID_SEA_SLURRY.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final BaseFlowingFluid.Properties VOID_SEA_SLURRY_PROPERTIES = new BaseFlowingFluid.Properties(
        ModFluidTypes.VOID_SEA_SLURRY_FLUID_TYPE,
        SOURCE_VOID_SEA_SLURRY,
        FLOWING_VOID_SEA_SLURRY
    )
        .slopeFindDistance(1) // Horizontal searching rate (flow speed)
        .levelDecreasePerBlock(1) // Spread distance
        .tickRate(15) // Spread rate (water ~5, inverted scale)
        .block(ModFluids.VOID_SEA_SLURRY_BLOCK)
        .bucket(ModFluids.VOID_SEA_SLURRY_BUCKET);


    // DENSITE EMULSION
    public static final FluidTransformationSettings DENSITE_EMULSION_SETTINGS = new FluidTransformationSettings(
        0.5f, // Transform rate [0.0f, 1,0f]
        15, // Max skylight
        new FluidTransformationSettings.YRange(-64, 319), // y level range
        false, // Require cold biome
        false, // Require rain
        false, // Require thunder
        false, // Require night
        Set.of( // Require adjacent blocks
            () -> Blocks.ICE, // Vanilla blocks must be clearly supplied
            () -> Blocks.PACKED_ICE,
            () -> Blocks.BLUE_ICE,
            () -> Blocks.FROSTED_ICE,
            ModBlocks.DENSITE_BLOCK
        ),
        new FluidTransformationSettings.LightningSettings(false, null), // Lightning requirements
        new FluidTransformationSettings.VibrationSettings(false, null, null), // Vibration requirements
        false, // Transform flowing fluids
        false, // Vaporize in ultrawarm dimensions
        Set.of(Level.OVERWORLD, Level.NETHER, Level.END), // Allowed dimensions
        Optional.of(() -> SoundEvents.ENDER_EYE_DEATH) // Sound effect
    );
    public static final Supplier<FlowingFluid> SOURCE_DENSITE_EMULSION = FLUIDS.register(
        "source_densite_emulsion",
        () -> new TransformBaseFlowingFluid.Source(
            ModFluids.DENSITE_EMULSION_PROPERTIES,
            ModBlocks.DENSITE_BLOCK,
            DENSITE_EMULSION_SETTINGS
        )
    );
    public static final Supplier<FlowingFluid> FLOWING_DENSITE_EMULSION = FLUIDS.register(
        "flowing_densite_emulsion",
        () -> new TransformBaseFlowingFluid.Flowing(
            ModFluids.DENSITE_EMULSION_PROPERTIES,
            ModBlocks.DENSITE_BLOCK,
            DENSITE_EMULSION_SETTINGS
        )
    );

    public static final DeferredBlock<LiquidBlock> DENSITE_EMULSION_BLOCK = ModBlocks.BLOCKS.register(
        "densite_emulsion_block",
        () -> new LiquidBlock(ModFluids.SOURCE_DENSITE_EMULSION.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).randomTicks().noLootTable())
    );
    public static final DeferredItem<Item> DENSITE_EMULSION_BUCKET = ModItems.ITEMS.registerItem(
        "densite_emulsion_bucket",
        properties -> new BucketItem(ModFluids.SOURCE_DENSITE_EMULSION.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final TransformBaseFlowingFluid.Properties DENSITE_EMULSION_PROPERTIES = new TransformBaseFlowingFluid.Properties(
        ModFluidTypes.DENSITE_EMULSION_FLUID_TYPE,
        SOURCE_DENSITE_EMULSION,
        FLOWING_DENSITE_EMULSION
    )
        .slopeFindDistance(1) // Horizontal searching rate
        .levelDecreasePerBlock(1) // Spread distance
        .tickRate(25) // Spread rate (water ~5, inverted scale)
        .block(ModFluids.DENSITE_EMULSION_BLOCK)
        .bucket(ModFluids.DENSITE_EMULSION_BUCKET);


    // DRIFT CONDENSATE
    public static final Supplier<FlowingFluid> SOURCE_DRIFT_CONDENSATE = FLUIDS.register(
        "source_drift_condensate",
        () -> new UpwardBaseFlowingFluid.Source(ModFluids.DRIFT_CONDENSATE_PROPERTIES)
            .setFlowAnimationOptions(10,3,0.2,0.4,0.5,1.0f)
    );
    public static final Supplier<FlowingFluid> FLOWING_DRIFT_CONDENSATE = FLUIDS.register(
        "flowing_drift_condensate",
        () -> new UpwardBaseFlowingFluid.Flowing(ModFluids.DRIFT_CONDENSATE_PROPERTIES)
            .setFlowAnimationOptions(10,3,0.2,0.4,0.8,1.0f)
    );

    public static final DeferredBlock<LiquidBlock> DRIFT_CONDENSATE_BLOCK = ModBlocks.BLOCKS.register(
        "drift_condensate_block",
        () -> new LiquidBlock(ModFluids.SOURCE_DRIFT_CONDENSATE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable())
    );
    public static final DeferredItem<Item> DRIFT_CONDENSATE_BUCKET = ModItems.ITEMS.registerItem(
        "drift_condensate_bucket",
        properties -> new BucketItem(ModFluids.SOURCE_DRIFT_CONDENSATE.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final UpwardBaseFlowingFluid.Properties DRIFT_CONDENSATE_PROPERTIES = new UpwardBaseFlowingFluid.Properties(
        ModFluidTypes.DRIFT_CONDENSATE_FLUID_TYPE,
        SOURCE_DRIFT_CONDENSATE,
        FLOWING_DRIFT_CONDENSATE
    )
        //.slopeFindDistance(0) // Horizontal searching rate (flow speed)
        //.levelDecreasePerBlock(1) // Spread distance
        //.tickRate(5) // Spread rate (water ~5, inverted scale)
        .block(ModFluids.DRIFT_CONDENSATE_BLOCK)
        .bucket(ModFluids.DRIFT_CONDENSATE_BUCKET);


    // PROPULSITE FLURRY
    public static final FluidTransformationSettings PROPULSITE_FLURRY_SETTINGS = new FluidTransformationSettings(
        1.0f, // Transform rate [0.0f, 1,0f]
        15, // Max skylight
        new FluidTransformationSettings.YRange(-64, 319), // y level range
        false, // Require cold biome
        false, // Require rain
        false, // Require thunder
        false, // Require night
        Set.of(), // Require adjacent blocks
        new FluidTransformationSettings.LightningSettings(true, 6), // Lightning requirements
        new FluidTransformationSettings.VibrationSettings(false, null, null), // Vibration requirements
        false, // Transform flowing fluids
        false, // Vaporize in ultrawarm dimensions
        Set.of(Level.OVERWORLD, Level.NETHER, Level.END), // Allowed dimensions
        Optional.of(() -> SoundEvents.GLASS_PLACE) // Sound effect
    );
    public static final Supplier<FlowingFluid> SOURCE_PROPULSITE_FLURRY = FLUIDS.register(
        "source_propulsite_flurry",
        () -> new TransformBaseFlowingFluid.Source(
            ModFluids.PROPULSITE_FLURRY_PROPERTIES,
            ModBlocks.PROPULSITE_BLOCK, //had to slightly change this because propulsite is no longer a deferred block -pebb // Now it is lol -na
            PROPULSITE_FLURRY_SETTINGS
        )
    );
    public static final Supplier<FlowingFluid> FLOWING_PROPULSITE_FLURRY = FLUIDS.register(
        "flowing_propulsite_flurry",
        () -> new TransformBaseFlowingFluid.Flowing(
            ModFluids.PROPULSITE_FLURRY_PROPERTIES,
            ModBlocks.PROPULSITE_BLOCK, //had to slightly change this because propulsite is no longer a deferred block -pebb // Now it is lol -na
            PROPULSITE_FLURRY_SETTINGS
        )
    );

    public static final DeferredBlock<LiquidBlock> PROPULSITE_FLURRY_BLOCK = ModBlocks.BLOCKS.register(
        "propulsite_flurry_block",
        () -> new LiquidBlock(ModFluids.SOURCE_PROPULSITE_FLURRY.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).randomTicks().noLootTable())
    );
    public static final DeferredItem<Item> PROPULSITE_FLURRY_BUCKET = ModItems.ITEMS.registerItem(
        "propulsite_flurry_bucket",
        properties -> new BucketItem(ModFluids.SOURCE_PROPULSITE_FLURRY.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final TransformBaseFlowingFluid.Properties PROPULSITE_FLURRY_PROPERTIES = new TransformBaseFlowingFluid.Properties(
        ModFluidTypes.PROPULSITE_FLURRY_FLUID_TYPE,
        SOURCE_PROPULSITE_FLURRY,
        FLOWING_PROPULSITE_FLURRY
    )
        .slopeFindDistance(8) // Horizontal searching rate
        .levelDecreasePerBlock(1) // Spread distance
        .tickRate(2) // Spread rate (water ~5, inverted scale)
        .block(ModFluids.PROPULSITE_FLURRY_BLOCK)
        .bucket(ModFluids.PROPULSITE_FLURRY_BUCKET);


    // TEMP (VibrationSettings test)
    public static final FluidTransformationSettings TEMP_SETTINGS = new FluidTransformationSettings(
            0.01f, // Transform rate [0.0f, 1,0f]
            15, // Max skylight
            new FluidTransformationSettings.YRange(-64, 319), // y level range
            false, // Require cold biome
            false, // Require rain
            false, // Require thunder
            false, // Require night
            Set.of(), // Require adjacent blocks
            new FluidTransformationSettings.LightningSettings(false, null), // Lightning requirements
            new FluidTransformationSettings.VibrationSettings(true, 6, 10), // Vibration requirements
            false, // Transform flowing fluids
            false, // Vaporize in ultrawarm dimensions
            Set.of(Level.OVERWORLD, Level.NETHER, Level.END), // Allowed dimensions
            Optional.of(() -> SoundEvents.GLASS_PLACE) // Sound effect
    );
    public static final Supplier<FlowingFluid> SOURCE_TEMP = FLUIDS.register(
            "source_temp",
            () -> new TransformBaseFlowingFluid.Source(
                    ModFluids.TEMP_PROPERTIES,
                    ModBlocks.TEMP_BLOCK,
                    TEMP_SETTINGS
            )
    );
    public static final Supplier<FlowingFluid> FLOWING_TEMP = FLUIDS.register(
            "flowing_temp",
            () -> new TransformBaseFlowingFluid.Flowing(
                    ModFluids.TEMP_PROPERTIES,
                    ModBlocks.TEMP_BLOCK,
                    TEMP_SETTINGS
            )
    );

    public static final DeferredBlock<LiquidBlock> TEMP_LIQUID_BLOCK = ModBlocks.BLOCKS.register(
            "temp_liquid_block",
            () -> new LiquidBlock(ModFluids.SOURCE_TEMP.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).randomTicks().noLootTable())
    );
    public static final DeferredItem<Item> TEMP_BUCKET = ModItems.ITEMS.registerItem(
            "temp_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_TEMP.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1))
    );

    public static final TransformBaseFlowingFluid.Properties TEMP_PROPERTIES = new TransformBaseFlowingFluid.Properties(
            ModFluidTypes.TEMP_FLUID_TYPE,
            SOURCE_TEMP,
            FLOWING_TEMP
    )
            .slopeFindDistance(8) // Horizontal searching rate
            .levelDecreasePerBlock(1) // Spread distance
            .tickRate(2) // Spread rate (water ~5, inverted scale)
            .block(ModFluids.TEMP_LIQUID_BLOCK)
            .bucket(ModFluids.TEMP_BUCKET);


    // NEXT FLUID ...


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
