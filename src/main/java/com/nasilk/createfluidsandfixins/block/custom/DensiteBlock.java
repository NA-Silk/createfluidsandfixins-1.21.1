package com.nasilk.createfluidsandfixins.block.custom;

import com.nasilk.createfluidsandfixins.particle.ModParticles;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class DensiteBlock extends Block {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final int MAX_CLUSTER_SIZE = 512;

    public DensiteBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
    }

    // POWER LEVELS
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        // Make sure the block was not just updated (i.e. powered)
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        // Make sure the new neighbor is not Densite (the new one will check for updates)
        if (!level.isClientSide && neighborBlock != this) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        updateClusterPower(level, pos);
    }

    private void updateClusterPower(Level level, BlockPos pos) {
        // Using specialized set and queue for long efficiency
        LongOpenHashSet cluster = new LongOpenHashSet();
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();

        long startLong = pos.asLong();
        cluster.add(startLong);
        queue.enqueue(startLong);

        // Perform breadth-first search (BFS) on cluster blocks for maximum power
        int maxPower = 0;
        while (!queue.isEmpty() && cluster.size() <= MAX_CLUSTER_SIZE) {
            BlockPos currentPos = BlockPos.of(queue.dequeueLong());

            // Update maxPower if under 15
            if (maxPower < 15) maxPower = Math.max(maxPower, getExternalPower(level, currentPos));

            // Search each direction around currentPos for other Densite blocks
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.relative(direction);
                long neighborLong = neighborPos.asLong();

                // Update cluster and queue if the neighbor block is a new densite
                if (!cluster.contains(neighborLong) && level.getBlockState(neighborPos).is(this)) {
                    cluster.add(neighborLong);
                    queue.enqueue(neighborLong);
                }
            }
        }

        // Apply maximum power to the cluster
        for (long currentLong : cluster) {
            BlockPos currentPos = BlockPos.of(currentLong);
            BlockState currentState = level.getBlockState(currentPos);

            // Safety check: still Densite & power level is actually different
            if (currentState.is(this) && currentState.getValue(POWER) != maxPower) {
                level.setBlockAndUpdate(currentPos, currentState.setValue(POWER, maxPower));
            }
        }
    }

    private int getExternalPower(Level level, BlockPos pos) {
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
            addParticles(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void addParticles(Level level, BlockPos pos) {
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
