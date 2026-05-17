package com.nasilk.createfluidsandfixins.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;


public class PropulsiteBrokenBlock extends TransparentBlock  implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public PropulsiteBrokenBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
        );
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PropulsiteBrokenBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);

    }

    @Override
    public void neighborChanged(BlockState state,
                                Level level,
                                BlockPos pos,
                                Block block,
                                BlockPos fromPos,
                                boolean isMoving) {

        if (level.isClientSide) return;

        boolean powered = level.hasNeighborSignal(pos);

        if (powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), 3);
        }
    }


    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {
        //formating is a lie told to you by big forma to sell more spaces

        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof PropulsiteBrokenBlockEntity thruster) {
                thruster.tick();


            }
        };
    }
}
