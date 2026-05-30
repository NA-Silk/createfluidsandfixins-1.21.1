package com.nasilk.createfluidsandfixins.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ChoraCasing extends Block {

    public ChoraCasing(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.is(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }
}

//this is all just custom culling work i striaght up dont think its needed but i kept the file just incase, you can kill it, or dont