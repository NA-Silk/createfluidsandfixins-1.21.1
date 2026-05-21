package com.nasilk.createfluidsandfixins.block.custom;

import com.nasilk.createfluidsandfixins.block.ModBlockEntities;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class PropulsiteBrokenBlockEntity extends BlockEntity {

    private static Vector3d position = new Vector3d(0.0D, 0.0D, 0.0D);
    private static Vector3d force = new Vector3d(0.0D, 0.0D, 0.0D);

    public PropulsiteBrokenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROPULSITE_BROKEN.get(), pos, state);
    }
    // FIRING SEQUENCE vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
    private int charge = 0;
    private int cooldown = 0;
    private static final int MAX_CHARGE = 60; //how long it takes for the burst to be ready after receiving redstone power in ticks
    private static final int MAX_COOLDOWN = 100; //how long it takes for the block to be able to be charged again in ticks

    private boolean armed = false;
    private boolean firing = false;
    private int firingTick = 0;

    private static final int BURST_DURATION = 16; //how long it takes for the full burst to go though in ticks

    public void tick() {
        if (level instanceof ServerLevel serverLevel) {

            Direction facing = getBlockState().getValue(PropulsiteBrokenBlock.FACING);

            position.set(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);

            boolean powered = getBlockState().getValue(PropulsiteBrokenBlock.POWERED);
            if (cooldown > 0) {

                if (!powered) cooldown--;

                return;
            }

            if (powered && !armed) {

                if (charge < MAX_CHARGE) {charge++;

                    Vec3 thrustFace = Vec3.atCenterOf(worldPosition).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.6));

                    double px = thrustFace.x + (serverLevel.random.nextDouble() - 0.5) * 0.15;
                    double py = thrustFace.y + (serverLevel.random.nextDouble() - 0.5) * 0.15;
                    double pz = thrustFace.z + (serverLevel.random.nextDouble() - 0.5) * 0.15;

                     serverLevel.sendParticles (ParticleTypes.WAX_ON,
                            px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);

                    if (charge >= MAX_CHARGE) {
                        armed = true;

                        serverLevel.playSound(
                                null,
                                worldPosition,
                                SoundEvents.BEACON_ACTIVATE,
                                SoundSource.BLOCKS,
                                1.0F,
                                1.2F

                        );
                    }
                }
            }

            if (!powered && !armed && charge > 0) {
                charge -= 2;

                if (charge < 0) charge = 0;

            }
            if (armed && !powered && !firing) {

                firing = true;
                firingTick = 0;
            }

            if (firing) {

                ServerSubLevel subLevel = (ServerSubLevel) Sable.HELPER.getContaining(level, worldPosition);

                if (subLevel != null) {
                    RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
                    if (!handle.isValid()) return;

                    double thrustStrength = 60 * Math.sin((double) firingTick / BURST_DURATION * Math.PI); //the curve that determines the total thrust of the burst

                    handle.applyImpulseAtPoint(position, force.set(facing.getStepX(), facing.getStepY(), facing.getStepZ()).mul(-thrustStrength));
                }
                serverLevel.sendParticles (ParticleTypes.SPORE_BLOSSOM_AIR,
                        position.x, position.y, position.z, 8, 0.1, 0.1, 0.1, 0.05);

                firingTick++;

                if (firingTick >= BURST_DURATION) {

                    firing = false;
                    armed = false;
                    charge = 0;
                    cooldown = MAX_COOLDOWN;
                    // FIRING SEQUENCE ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                }
            }
        }
    }
}