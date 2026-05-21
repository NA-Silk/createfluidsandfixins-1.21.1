package com.nasilk.createfluidsandfixins.block.custom;

import com.nasilk.createfluidsandfixins.block.ModBlockEntities;
import com.nasilk.createfluidsandfixins.util.FFLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import java.util.List;

public class PropulsiteBrokenBlockEntity extends BlockEntity implements IHaveGoggleInformation {

    public PropulsiteBrokenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROPULSITE_BROKEN.get(), pos, state);
    }

    private int charge = 0;
    private int cooldown = 0;
    private static final int MAX_CHARGE = 60; //how long it takes for the burst to be ready after receiving redstone power in ticks
    private static final int MAX_COOLDOWN = 100; //how long it takes for the block to be able to be charged again in ticks

    private boolean armed = false;
    private boolean firing = false;
    private int firingTick = 0;

    private static final int BURST_DURATION = 16; //how long it takes for the full burst to go though in ticks

    public void tick() {




        Direction facing = getBlockState().getValue(PropulsiteBrokenBlock.FACING);

        Vector3d thrust = new Vector3d(
                facing.getStepX(),
                facing.getStepY(),
                facing.getStepZ()
        );

        Vector3d position = new Vector3d(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        );

        boolean powered = getBlockState().getValue(PropulsiteBrokenBlock.POWERED);
        if (cooldown > 0) {

            if (!powered) {
                cooldown--;
            }

            return;
        }

        if (powered && !armed) {

        if (charge < MAX_CHARGE) {
                charge++;

                 {
                 Vec3 forward = Vec3.atLowerCornerOf(facing.getNormal());
                 Vec3 center = Vec3.atCenterOf(worldPosition);
                 Vec3 thrustFace = center.add(forward.scale(0.6));

                 double px = thrustFace.x + (level.random.nextDouble() - 0.5) * 0.15;
                 double py = thrustFace.y + (level.random.nextDouble() - 0.5) * 0.15;
                 double pz = thrustFace.z + (level.random.nextDouble() - 0.5) * 0.15;

                 Vec3 particlePos = new Vec3(px, py, pz);

                 Vec3 motion = thrustFace.subtract(particlePos)
                         .normalize()
                         .scale(0.05);

                     ((ServerLevel) level).sendParticles
                             (ParticleTypes.WAX_ON,
                             px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
                }

                if (charge >= MAX_CHARGE) {
                    armed = true;

                    level.playSound(
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

                if (charge < 0) {
                    charge = 0;
                }
            }
            if (armed && !powered && !firing) {

                firing = true;
                firingTick = 0;
            }

            if (firing) {

                if (level == null || level.isClientSide) return;
                ServerSubLevel subLevel = (ServerSubLevel) Sable.HELPER.getContaining(level, worldPosition);
                if (subLevel == null) return;
                RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
                if (!handle.isValid()) return;

                double progress = (double) firingTick / BURST_DURATION;

                double curve = Math.sin(progress * Math.PI);

                double thrustStrength = 60 * curve; //the curve that determines the total thrust of the burst

                Vector3d firingThrust = new Vector3d(thrust);

                firingThrust.mul(-thrustStrength);

                handle.applyImpulseAtPoint(position, firingThrust);

                ((ServerLevel) level).sendParticles
                        (ParticleTypes.ELECTRIC_SPARK,
                        position.x, position.y, position.z, 8, 0.1, 0.1, 0.1, 0.05);

                firingTick++;

                if (firingTick >= BURST_DURATION) {

                    firing = false;
                    armed = false;

                    charge = 0;

                    cooldown = MAX_COOLDOWN;

                }
            }
    }

    @Override
    public boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        FFLang.emptyLine(tooltip);
        FFLang.blockName(this.getBlockState()).text(":").forGoggles(tooltip);

        final MutableComponent thrustComponent = FFLang
                .pixelNewton(Math.abs(20))
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.thrust", thrustComponent)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        final MutableComponent maximumThrust = FFLang
                .pixelNewton(Math.abs(200))
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.maximum_thrust", maximumThrust)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        return true;
    }
}
