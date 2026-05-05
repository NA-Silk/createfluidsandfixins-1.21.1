package com.nasilk.createfluidsandfixins.fluid;

/* MCCourse imports
 * import net.kaupenjoe.mccourse.MCCourseMod;
 * import net.kaupenjoe.mccourse.block.ModBlocks;
 * import net.kaupenjoe.mccourse.item.ModItems;
 */
import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.block.ModBlocks;
import com.nasilk.createfluidsandfixins.item.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

import java.util.function.Supplier;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, CreateFluidsAndFixins.MOD_ID);

    public static final Supplier<FlowingFluid> SOURCE_DENSITE_SOLUTION_WATER = FLUIDS.register("source_densite_solution_water",
            () -> new BaseFlowingFluid.Source(ModFluids.DENSITE_SOLUTION_WATER_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_DENSITE_SOLUTION_WATER = FLUIDS.register("flowing_densite_solution_water",
            () -> new BaseFlowingFluid.Flowing(ModFluids.DENSITE_SOLUTION_WATER_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> DENSITE_SOLUTION_WATER_BLOCK = ModBlocks.BLOCKS.register("densite_solution_water_block",
            () -> new LiquidBlock(ModFluids.SOURCE_DENSITE_SOLUTION_WATER.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredItem<Item> DENSITE_SOLUTION_WATER_BUCKET = ModItems.ITEMS.registerItem("densitebucket",
            properties -> new BucketItem(ModFluids.SOURCE_DENSITE_SOLUTION_WATER.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final BaseFlowingFluid.Properties DENSITE_SOLUTION_WATER_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.DENSITE_SOLUTION_WATER_FLUID_TYPE, SOURCE_DENSITE_SOLUTION_WATER, FLOWING_DENSITE_SOLUTION_WATER)
            .slopeFindDistance(2).levelDecreasePerBlock(1)
            .block(ModFluids.DENSITE_SOLUTION_WATER_BLOCK).bucket(ModFluids.DENSITE_SOLUTION_WATER_BUCKET);


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
