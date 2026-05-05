package com.nasilk.createfluidsandfixins.fluid;

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
    // DEFAULT
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, CreateFluidsAndFixins.MOD_ID);

    // DENSITE EMULSION
    public static final Supplier<FlowingFluid> SOURCE_DENSITE_EMULSION = FLUIDS.register("source_densite_emulsion",
            () -> new BaseFlowingFluid.Source(ModFluids.DENSITE_EMULSION_PROPERTIES));

    public static final Supplier<FlowingFluid> FLOWING_DENSITE_EMULSION = FLUIDS.register("flowing_densite_emulsion",
            () -> new BaseFlowingFluid.Flowing(ModFluids.DENSITE_EMULSION_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> DENSITE_EMULSION_BLOCK = ModBlocks.BLOCKS.register("densite_emulsion_block",
            () -> new LiquidBlock(ModFluids.SOURCE_DENSITE_EMULSION.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final DeferredItem<Item> DENSITE_EMULSION_BUCKET = ModItems.ITEMS.registerItem("densite_emulsion_bucket",
            properties -> new BucketItem(ModFluids.SOURCE_DENSITE_EMULSION.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final BaseFlowingFluid.Properties DENSITE_EMULSION_PROPERTIES = new BaseFlowingFluid.Properties(
            ModFluidTypes.DENSITE_EMULSION_FLUID_TYPE, SOURCE_DENSITE_EMULSION, FLOWING_DENSITE_EMULSION)
            .slopeFindDistance(2).levelDecreasePerBlock(1)
            .block(ModFluids.DENSITE_EMULSION_BLOCK).bucket(ModFluids.DENSITE_EMULSION_BUCKET);

    // NEXT FLUID ...

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
