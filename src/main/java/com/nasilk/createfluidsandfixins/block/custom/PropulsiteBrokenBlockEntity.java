package com.nasilk.createfluidsandfixins.block.custom;

import com.nasilk.createfluidsandfixins.block.ModBlockEntities;
import com.nasilk.createfluidsandfixins.util.FFLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
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
    private static final Vector3d position = new Vector3d(0.0D, 0.0D, 0.0D);
    private static final Vector3d force = new Vector3d(0.0D, 0.0D, 0.0D);
    private final Direction facing = getBlockState().getValue(PropulsiteBrokenBlock.FACING);

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

    public PropulsiteBrokenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROPULSITE_BROKEN.get(), pos, state);
    }

    public void tick() {
        if (level instanceof ServerLevel serverLevel) {
            boolean powered = getBlockState().getValue(PropulsiteBrokenBlock.POWERED);
            position.set(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);

            // Cooldown
            if (cooldown > 0) {
                if (!powered) cooldown--;
                return;
            }

            // Charging
            if (powered && !armed) {
                if (charge < MAX_CHARGE) {
                    charge++;

                    Vec3 thrustFace = Vec3.atCenterOf(worldPosition).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.6));
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
                    thrustStrength = (AMPLITUDE / (STANDARD_DEVIATION * Math.sqrt(2.0 * Math.PI))) * Math.exp(-0.5 * Math.pow((firingTick - MEAN) / STANDARD_DEVIATION, 2));
                    handle.applyImpulseAtPoint(position, force.set(facing.getStepX(), facing.getStepY(), facing.getStepZ()).mul(-thrustStrength));
                }

                firingTick++;
                if (firingTick >= BURST_DURATION) {
                    firing = false;
                    armed = false;
                    charge = 0;
                    thrustStrength = 0;
                    cooldown = MAX_COOLDOWN;
                }

                serverLevel.sendParticles(
                    ParticleTypes.SPORE_BLOSSOM_AIR,
                    position.x,
                    position.y,
                    position.z,
                    8,0.1,0.1,0.1,0.05
                );
            }

            // Force packet update (for tooltips)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        //FFLang.emptyLine(tooltip);
        FFLang.blockName(this.getBlockState()).text(":").forGoggles(tooltip);

        final MutableComponent thrustComponent = FFLang
                .pixelNewton(thrustStrength)
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.thrust", thrustComponent)
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
    public net.minecraft.nbt.CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        net.minecraft.nbt.CompoundTag tag = super.getUpdateTag(registries);
        tag.putDouble("ThrustStrength", this.thrustStrength);
        return tag;
    }

    // Wrap the tag into the standard vanilla packet
    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    // Handle receiving the packet on the Client side
    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt, net.minecraft.core.HolderLookup.Provider registries) {
        net.minecraft.nbt.CompoundTag tag = pkt.getTag();
        if (tag != null) this.thrustStrength = tag.getDouble("ThrustStrength");
    }
}
