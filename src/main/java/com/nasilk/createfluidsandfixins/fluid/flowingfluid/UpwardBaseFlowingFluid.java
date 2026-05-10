package com.nasilk.createfluidsandfixins.fluid.flowingfluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

// Very Gemini assisted ... sorry
public abstract class UpwardBaseFlowingFluid extends BaseFlowingFluid {
    protected UpwardBaseFlowingFluid(Properties properties) {
        super(properties);
    }

    /**
     * THE KILL SWITCH: Overriding tick without calling super.tick()
     * prevents the vanilla fluid engine from ever touching this fluid.
     */
    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        if (level.isClientSide) return;

        int amount = state.getAmount();

        // 1. Cleanup & Height Limit
        // If amount is too low, or we hit the sky, vanish.
        if (amount <= 1 || pos.getY() >= level.getMaxBuildHeight() - 1) {
            if (!state.isSource()) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        // 2. Tube Man Animation Logic
        // We use (Time - Y) so the wave "travels" up the column like a real tube man.
        long time = level.getGameTime();
        double wave = (time * 0.2) - (pos.getY() * 0.3);

        int offsetX = (Math.sin(wave) > 0.7) ? 1 : (Math.sin(wave) < -0.7 ? -1 : 0);
        int offsetZ = (Math.cos(wave) > 0.7) ? 1 : (Math.cos(wave) < -0.7 ? -1 : 0);

        BlockPos targetPos = pos.above().offset(offsetX, 0, offsetZ);

        // 3. Propagation
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.canBeReplaced()) {
            // Logic to reach ~15 blocks: drop 1 level every 2 Y-levels.
            int newAmount = (pos.getY() % 2 == 0) ? amount : amount - 1;

            if (newAmount > 0) {
                // Place the next piece of the tube man
                level.setBlock(targetPos, this.getFlowing(newAmount, false).createLegacyBlock(), 3);
                // Force the new block to tick soon
                level.scheduleTick(targetPos, this, 6);
            }
        }

        // 4. Persistence
        // Sources stay, flowing blocks vanish after they "pass" their energy upward.
        if (!state.isSource()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else {
            // Sources need to keep spawning the next block
            level.scheduleTick(pos, this, 12);
        }
    }

    // --- DISABLE ALL VANILLA FLOW MECHANICS ---

    @Override
    public Vec3 getFlow(BlockGetter level, BlockPos pos, FluidState state) {
        return Vec3.ZERO;
    }

    @Override
    protected boolean canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fromFluid) {
        return false; // Nuclear "No" to horizontal spreading
    }

    @Override
    protected void spread(Level level, BlockPos pos, FluidState state) {
        // Do nothing. We handled everything in tick().
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) { return 0; }

    @Override
    protected int getDropOff(LevelReader level) { return 1; }

    @Override
    protected boolean canConvertToSource(Level level) { return false; }

    @Override
    public FluidState getSource(boolean falling) {
        return super.getSource(false);
    }

    // Inner classes (Flowing/Source) remain identical to your previous working version
    public static class Flowing extends UpwardBaseFlowingFluid {
        public Flowing(Properties properties) { super(properties); }
        @Override public boolean isSource(FluidState state) { return false; }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
    }

    public static class Source extends UpwardBaseFlowingFluid {
        public Source(Properties properties) { super(properties); }
        @Override public boolean isSource(FluidState state) { return true; }
        @Override public int getAmount(FluidState state) { return 8; }
    }
}
