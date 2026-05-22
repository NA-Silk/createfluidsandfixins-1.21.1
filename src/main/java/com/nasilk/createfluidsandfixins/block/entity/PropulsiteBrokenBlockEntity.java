package com.nasilk.createfluidsandfixins.block.entity;

import com.nasilk.createfluidsandfixins.block.ModBlockEntities;
import com.nasilk.createfluidsandfixins.block.custom.PropulsiteBrokenBlock;
import com.nasilk.createfluidsandfixins.util.FFLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import java.util.List;

@SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner"})
public class PropulsiteBrokenBlockEntity extends BlockEntity implements IHaveGoggleInformation {
    private int charge = 0;
    private int cooldown = 0;
    private int firingTick = 0;
    private double thrustStrength = 0;

    private boolean armed = false;
    private boolean firing = false;

    private static final int MAX_CHARGE = 60; // How long it takes for the burst to be ready after receiving redstone power in ticks
    private static final int MAX_COOLDOWN = 100; // How long it takes for the block to be able to be charged again in ticks
    private static final int BURST_DURATION = 40; // How long it takes for the full burst to go though in ticks
    private static final double AMPLITUDE = 100.0d; // How much total thrust is output over the length of the burst
    private static final double STANDARD_DEVIATION = 5.0d; // Curve spread
    private static final double MEAN = 20.0d; // Curve middle
    private static final double TERMINAL_VELOCITY = 50.0d; // Maximum falling speed to prevent tunneling, 50 seems to work

    public PropulsiteBrokenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROPULSITE_BROKEN.get(), pos, state);
    }

    public void tick() {
        if (level instanceof ServerLevel serverLevel) {
            BlockState state = getBlockState();
            Direction facing = state.getValue(PropulsiteBrokenBlock.FACING);
            boolean powered = state.getValue(PropulsiteBrokenBlock.POWERED);
            Vec3 thrustFace = Vec3.atCenterOf(worldPosition).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.6));

            // Cooldown
            if (cooldown > 0) {
                if (!powered) cooldown--;
                return;
            }

            // Charging
            if (powered && !armed) {
                if (charge < MAX_CHARGE) {
                    charge++;

                    serverLevel.sendParticles (
                        ParticleTypes.WAX_ON,
                        thrustFace.x + (serverLevel.random.nextDouble() - 0.5) * 0.15,
                        thrustFace.y + (serverLevel.random.nextDouble() - 0.5) * 0.15,
                        thrustFace.z + (serverLevel.random.nextDouble() - 0.5) * 0.15,
                        1,0.0,0.0,0.0,0.0
                    );

                    if (charge >= MAX_CHARGE) {
                        armed = true;

                        serverLevel.playSound(
                            null,
                            worldPosition,
                            SoundEvents.BEACON_ACTIVATE,
                            SoundSource.BLOCKS,
                            1.0F,1.2F
                        );
                    }
                }
            }

            // Discharging
            if (!powered && !armed && charge > 0) charge = Math.max(charge - 2, 0);

            // Firing initialization
            if (armed && !powered && !firing) {
                firing = true;
                firingTick = 0;
            }

            // Firing sequence
            if (firing) {
                if (Sable.HELPER.getContaining(level, worldPosition) instanceof ServerSubLevel subLevel) {
                    RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
                    if (!handle.isValid()) return;

                    // The curve that determines the total thrust of the burst
                    thrustStrength =
                        (AMPLITUDE / (STANDARD_DEVIATION * Math.sqrt(2.0 * Math.PI))) // Maximum
                        * Math.exp(-0.5 * Math.pow((firingTick - MEAN) / STANDARD_DEVIATION, 2)); // Curve computation

                    Vector3d position = new Vector3d(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
                    Vector3d force = new Vector3d(facing.getStepX(), facing.getStepY(), facing.getStepZ()).mul(-thrustStrength);
                    handle.applyImpulseAtPoint(position, force);
                }

                firingTick++;
                if (firingTick >= BURST_DURATION) {
                    firing = false;
                    armed = false;
                    charge = 0;
                    thrustStrength = 0;
                    cooldown = MAX_COOLDOWN;
                }

                addThrusterParticles(serverLevel, facing, thrustFace);
            }

            // Handle terminal velocity
            if (Sable.HELPER.getContaining(level, worldPosition) instanceof ServerSubLevel subLevel) {
                RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
                if (!handle.isValid()) return;
                Vector3d currentVelocity = handle.getLinearVelocity(new Vector3d());

                // Apply opposite velocity if falling faster than 3.0 blocks per tick (~60 blocks/s)
                if (currentVelocity.y < -TERMINAL_VELOCITY) {
                    handle.addLinearAndAngularVelocity(
                        new Vector3d(0.0, -TERMINAL_VELOCITY - currentVelocity.y, 0.0),
                        new Vector3d(0.0, 0.0, 0.0)
                    );
                }
            }

            // Force packet update (for tooltips)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void addThrusterParticles(ServerLevel serverLevel, Direction facing, Vec3 thrustFace) {
        double maxThrust = AMPLITUDE / (STANDARD_DEVIATION * Math.sqrt(2.0 * Math.PI));
        double thrustRatio = Math.max(0.0, thrustStrength / maxThrust); // [0.0 to 1.0] multiplier based on current thrust strength
        double baseVelocity = 0.3 + (0.7 * thrustRatio); // Faster jet at peak thrust
        int particleCount = (int) (15 * thrustRatio) + 2; // Always spawn at least 2

        for (int i = 0; i < particleCount; i++) {
            // Apply slight random spread to the cone of the thrust
            double dirX = facing.getStepX() + (serverLevel.random.nextGaussian() * 0.15);
            double dirY = facing.getStepY() + (serverLevel.random.nextGaussian() * 0.15);
            double dirZ = facing.getStepZ() + (serverLevel.random.nextGaussian() * 0.15);

            // Normalize the randomized direction and scale by our calculated velocity
            Vec3 particleVel = new Vec3(dirX, dirY, dirZ).normalize().scale(baseVelocity);

            // By setting count to 0, xOffset, yOffset, and zOffset act as xSpeed, ySpeed, and zSpeed
            serverLevel.sendParticles(
                ParticleTypes.FLAME, // Replace later
                thrustFace.x,
                thrustFace.y,
                thrustFace.z,
                0, // Count = 0 forces the particle to use the offsets as velocity
                particleVel.x,
                particleVel.y,
                particleVel.z,
                1.0 // Speed multiplier (1.0 means it uses the exact x/y/z vector values above)
            );
        }
    }

    @Override
    public boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        //FFLang.emptyLine(tooltip);
        FFLang.blockName(this.getBlockState()).text(":").forGoggles(tooltip);

        final MutableComponent currentThrust = FFLang
                .pixelNewton(thrustStrength)
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.current_thrust", currentThrust)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        final MutableComponent maximumThrust = FFLang
                .pixelNewton(AMPLITUDE / (STANDARD_DEVIATION * Math.sqrt(2 * Math.PI)))
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.maximum_thrust", maximumThrust)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        final MutableComponent totalThrust = FFLang
                .pixelNewton(AMPLITUDE)
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.total_thrust", totalThrust)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        return true;
    }

    // Save data to the network sync packet
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putDouble("ThrustStrength", this.thrustStrength);
        return tag;
    }

    // Wrap the tag into the standard vanilla packet
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Handle receiving the packet on the Client side
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        this.thrustStrength = tag.getDouble("ThrustStrength");
    }
}
