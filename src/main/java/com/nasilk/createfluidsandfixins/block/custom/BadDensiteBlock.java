package com.nasilk.createfluidsandfixins.block.custom;

import com.nasilk.createfluidsandfixins.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Very inefficient attempt at cluster powering, do not use
 * Reference only, will be deleted
*/
public class BadDensiteBlock extends Block {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    private static final int MAX_CLUSTER_SIZE = 1024;

    public BadDensiteBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
    }

    // POWER LEVELS
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        // Make sure the block was not just updated (i.e. powered)
        if (!state.is(oldState.getBlock()) && !level.isClientSide) {
            updateClusterPower(level, pos);
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            updateClusterPower(level, pos);
        }
    }

    private void updateClusterPower(Level level, BlockPos pos) {
        Set<BlockPos> cluster = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        cluster.add(pos);
        queue.add(pos);

        // Perform breadth-first search (BFS) on cluster blocks for maximum power
        int maxPower = 0;
        while (!queue.isEmpty() && cluster.size() <= MAX_CLUSTER_SIZE) {
            BlockPos currentPos = queue.poll(); // Get top pos from queue
            maxPower = Math.max(maxPower, getInputPower(level, currentPos)); // Update maxPower

            // Search each direction for other Densite blocks
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);

                // Update cluster and queue if the neighbor block is a new densite
                if (!cluster.contains(neighborPos)) {
                    if (level.getBlockState(neighborPos).is(this)) {
                        cluster.add(neighborPos);
                        queue.add(neighborPos);
                    }
                }
            }
        }

        // Apply maximum power to cluster
        for (BlockPos currentPos : cluster) {
            BlockState currentState = level.getBlockState(currentPos);

            // Safety check: still Densite & power level is actually changing
            if (currentState.is(this) && currentState.getValue(POWER) != maxPower) {
                level.setBlock(currentPos, currentState.setValue(POWER, maxPower), 3);
            }
        }
    }

    private int getInputPower(Level level, BlockPos pos) {
        // Search each direction for maximum power
        int maxPower = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);

            // Update maxPower if the neighbor block is Densite
            if (!level.getBlockState(neighborPos).is(this)) {
                maxPower = Math.max(maxPower, level.getSignal(neighborPos, direction));
            }
        }
        return maxPower;
    }

    // PARTICLES
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !isMoving) {
            addParticle(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void addParticle(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                ModParticles.DENSITE_PARTICLES.get(),
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                8,0.3,0.2,0.3,0.05
            );
        }
    }
}
