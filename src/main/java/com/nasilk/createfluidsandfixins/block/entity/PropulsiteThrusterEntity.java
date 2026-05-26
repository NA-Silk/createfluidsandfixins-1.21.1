package com.nasilk.createfluidsandfixins.block.entity;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.block.ModBlockEntities;
import com.nasilk.createfluidsandfixins.block.ModBlocks;
import com.nasilk.createfluidsandfixins.block.custom.PropulsiteThrusterBlock;
import com.nasilk.createfluidsandfixins.particle.ModParticles;
import com.nasilk.createfluidsandfixins.util.FFLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import java.util.List;

@SuppressWarnings({"SpellCheckingInspection", "GrazieInspectionRunner"})
public class PropulsiteThrusterEntity extends BlockEntity implements IHaveGoggleInformation {
    // Tick variables
    private int charge = 0;
    private int cooldown = 0;
    private int firingTick = 0;
    private double amplitude = AMPLITUDE;
    private double thrust = 0.0d;
    private boolean armed = false;
    private boolean firing = false;

    private int tickCounter = 0;
    Direction facing = Direction.NORTH;

    // Tick constants
    private static final int MAX_CHARGE = 60; // How long it takes for the burst to be ready after receiving redstone power in ticks
    private static final int MAX_COOLDOWN = 100; // How long it takes for the block to be able to be charged again in ticks
    private static final int BURST_DURATION = 40; // How long it takes for the full burst to go though in ticks
    private static final double AMPLITUDE = 100.0d; // How much total thrust is output over the length of the burst
    private static final double STANDARD_DEVIATION = 5.0d; // Curve spread
    private static final double MEAN = 20.0d; // Curve middle

    // Cluster strength constants
    private static final int MAX_CLUSTER_SIZE = 16;
    private static final double CLUSTER_SCALE = 0.25d;

    // Particle constants
    private static final double MIN_PARTICLE_SPEED = 0.15;
    private static final double MAX_PARTICLE_SPEED = 0.5;
    private static final int MIN_PARTICLES = 2;
    private static final int MAX_PARTICLES = 10;
    private static final double PARTICLE_SPREAD = 0.15;

    public PropulsiteThrusterEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PROPULSITE_THRUSTER.get(), pos, state);
    }


    // TICK BEHAVIOR
    private Vector3d thrustDirection = new Vector3d(0.0D, 0.0D, 0.0D);
    private Vector3d thrustFace =  new Vector3d(0.0D, 0.0D, 0.0D);
    public void tick() {
        if (level instanceof ServerLevel serverLevel) {
            tickCounter++;
            BlockState state = getBlockState();
            boolean powered = state.getValue(PropulsiteThrusterBlock.POWERED);

            facing = state.getValue(PropulsiteThrusterBlock.FACING);
            thrustDirection.set(JOMLConversion.toJOML(Vec3.atLowerCornerOf(facing.getNormal())));
            thrustFace.set(JOMLConversion.toJOML(Vec3.atCenterOf(worldPosition)).fma(0.6, thrustDirection));

            // Update amplitude
            if (tickCounter % 20 == 0) {
                updateAmplitude(serverLevel, worldPosition);
                tickCounter = 1;
            }

            // Cooldown
            if (cooldown > 0) {
                if (!powered) {
                    cooldown--;
                    this.setChanged();
                }
                return;
            }

            // Charging
            if (powered && !armed) {
                if (charge < MAX_CHARGE) {
                    charge++;
                    this.setChanged();

                    serverLevel.sendParticles (
                        ParticleTypes.WAX_ON,
                        thrustFace.x() + (serverLevel.random.nextDouble() - 0.5) * 0.15,
                        thrustFace.y() + (serverLevel.random.nextDouble() - 0.5) * 0.15,
                        thrustFace.z() + (serverLevel.random.nextDouble() - 0.5) * 0.15,
                        1,0.0,0.0,0.0,0.0
                    );

                    if (charge >= MAX_CHARGE) {
                        armed = true;
                        this.setChanged();

                        serverLevel.playSound(
                            null,
                            worldPosition,
                            SoundEvents.BEACON_ACTIVATE,
                            SoundSource.BLOCKS,
                            1.5F,1.2F
                        );
                    }
                }
            }

            // Discharging
            if (!powered && !armed && charge > 0) {
                charge = Math.max(charge - 2, 0);
                this.setChanged();
            }

            // Firing initialization
            if (armed && !powered && !firing) {
                firing = true;
                firingTick = 0;
                this.setChanged();

                serverLevel.playSound(
                    null,
                    worldPosition,
                    SoundEvents.ENDER_DRAGON_SHOOT,
                    SoundSource.BLOCKS,
                    1.5F,1.0F
                );
            }

            // Firing sequence
            if (firing) {
                ServerSubLevel serverSubLevel = null; // Assigned for external use
                if (Sable.HELPER.getContaining(serverLevel, worldPosition) instanceof ServerSubLevel subLevel) {
                    serverSubLevel = subLevel;

                    RigidBodyHandle handle = RigidBodyHandle.of(subLevel);
                    if (!handle.isValid()) return;

                    // The curve that determines the total thrust of the burst
                    thrust =
                        (amplitude / (STANDARD_DEVIATION * Math.sqrt(2.0 * Math.PI))) // Maximum
                        * Math.exp(-0.5 * Math.pow((firingTick - MEAN) / STANDARD_DEVIATION, 2)); // Curve computation

                    Vector3d position = new Vector3d(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
                    Vector3d force = new Vector3d(facing.getStepX(), facing.getStepY(), facing.getStepZ()).mul(-thrust);
                    handle.applyImpulseAtPoint(position, force);
                }

                firingTick++;
                if (firingTick >= BURST_DURATION) {
                    firing = false;
                    armed = false;
                    charge = 0;
                    thrust = 0;
                    cooldown = MAX_COOLDOWN;
                }
                this.setChanged();

                // Effects
                addExhaustParticles(serverLevel, serverSubLevel);
                pushEntities(serverLevel, serverSubLevel);

                // Force packet update (for tooltips)
                if (firingTick % 5 == 0) serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    private void pushEntities(ServerLevel serverLevel, ServerSubLevel subLevel) { // TODO: finish this
        if (thrust < 0.20d) return;

        double dirX = facing.getStepX();
        double dirY = facing.getStepY();
        double dirZ = facing.getStepZ();

        CreateFluidsAndFixins.LOGGER.info("Thrust: {}", thrust);
    }

    public void updateAmplitude(Level level, BlockPos pos) {
        // Using specialized set and queue for long efficiency
        LongOpenHashSet cluster = new LongOpenHashSet();
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();

        long startLong = pos.asLong();
        cluster.add(startLong);
        queue.enqueue(startLong);

        // Perform breadth-first search (BFS) on cluster blocks
        int propulsiteCount = 0;
        int thrusterCount = 1;
        while (!queue.isEmpty() && cluster.size() <= MAX_CLUSTER_SIZE) {
            BlockPos currentPos = BlockPos.of(queue.dequeueLong());

            // Search each direction around currentPos for propulsite
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.relative(direction);
                long neighborLong = neighborPos.asLong();

                // Update cluster and queue if the neighbor block is a new densite
                if (cluster.contains(neighborLong)) continue;
                if (level.getBlockState(neighborPos).is(ModBlocks.PROPULSITE_BLOCK)) propulsiteCount++;
                else if (level.getBlockState(neighborPos).is(ModBlocks.PROPULSITE_THRUSTER)) thrusterCount++;
                else continue;
                cluster.add(neighborLong);
                queue.enqueue(neighborLong);
            }
        }

        // Set updated amplitude
        amplitude = (1.0d + (CLUSTER_SCALE * propulsiteCount) / thrusterCount) * AMPLITUDE;

        // Tell the game this block needs to be saved to disk
        this.setChanged();

        // Tell the server to send the new amplitude to the client for the Goggle tooltips
        if (!level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void addExhaustParticles(ServerLevel serverLevel, ServerSubLevel subLevel) {
        double maxThrust = amplitude / (STANDARD_DEVIATION * Math.sqrt(2.0 * Math.PI));
        double thrustRatio = Math.max(0.0, thrust / maxThrust); // [0.0 to 1.0] multiplier based on current thrust strength

        double baseVelocity = MIN_PARTICLE_SPEED + ((MAX_PARTICLE_SPEED - MIN_PARTICLE_SPEED) * thrustRatio); // Faster jet at peak thrust
        int particleCount = MIN_PARTICLES + (int) ((MAX_PARTICLES - MIN_PARTICLES) * thrustRatio);

        // Compute each particle
        for (int i = 0; i < particleCount; i++) {
            // Apply slight random spread to the cone of the thrust
            double dirX = facing.getStepX() + (serverLevel.random.nextGaussian() * PARTICLE_SPREAD);
            double dirY = facing.getStepY() + (serverLevel.random.nextGaussian() * PARTICLE_SPREAD);
            double dirZ = facing.getStepZ() + (serverLevel.random.nextGaussian() * PARTICLE_SPREAD);

            // Get local/sublevel vectors
            Vector3d spawnPosition = new Vector3d(thrustFace);
            Vector3d spawnVelocity = new Vector3d(dirX, dirY, dirZ).normalize().mul(baseVelocity); // Normalize the direction and scale by baseVelocity

            // Convert sublevel vectors to global vectors
            if (subLevel != null) {
                Vector3d endPosition = new Vector3d(spawnPosition).add(spawnVelocity);

                Vector3d globalStartPosition = subLevel.logicalPose().transformPosition(spawnPosition);
                Vector3d globalEndPosition = subLevel.logicalPose().transformPosition(endPosition);

                spawnPosition.set(globalStartPosition);
                spawnVelocity.set(globalEndPosition.sub(globalStartPosition)); // NOTE: globalEndPosition is also mutated, I don't like pass-by-reference
            }

            // By setting count to 0, xOffset, yOffset, and zOffset act as xSpeed, ySpeed, and zSpeed
            serverLevel.sendParticles(
                ModParticles.PROPULSITE_THRUSTER_PARTICLES.get(), // Was ParticleTypes.GUST
                spawnPosition.x, spawnPosition.y, spawnPosition.z,
                0,
                spawnVelocity.x, spawnVelocity.y, spawnVelocity.z,
                1.0 // Use spawnVelocity values
            );
        }
    }


    // GOGGLE TOOLTIPS
    @Override
    public boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        FFLang.blockName(this.getBlockState()).text(":").forGoggles(tooltip);

        final MutableComponent currentThrust = FFLang
                .pixelNewton(thrust)
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.current_thrust", currentThrust)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        final MutableComponent maximumThrust = FFLang
                .pixelNewton(amplitude / (STANDARD_DEVIATION * Math.sqrt(2 * Math.PI)))
                .style(ChatFormatting.AQUA)
                .component();
        FFLang.translate("goggles.maximum_thrust", maximumThrust)
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip, 1);

        final MutableComponent totalThrust = FFLang
                .pixelNewton(amplitude)
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
        tag.putDouble("Thrust", this.thrust);
        tag.putDouble("Amplitude", this.amplitude);
        return tag;
    }

    // Handle receiving the packet on the Client side
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        this.thrust = tag.getDouble("Thrust");
        this.amplitude = tag.getDouble("Amplitude");
    }

    // Wrap the tag into the standard vanilla packet
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    // DATA PERSISTENCE
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Charge", this.charge);
        tag.putInt("Cooldown", this.cooldown);
        tag.putInt("FiringTick", this.firingTick);
        tag.putDouble("Amplitude", this.amplitude);
        tag.putDouble("Thrust", this.thrust);
        tag.putBoolean("Armed", this.armed);
        tag.putBoolean("Firing", this.firing);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.charge = tag.getInt("Charge");
        this.cooldown = tag.getInt("Cooldown");
        this.firingTick = tag.getInt("FiringTick");
        this.amplitude = tag.getDouble("Amplitude");
        this.thrust = tag.getDouble("Thrust");
        this.armed = tag.getBoolean("Armed");
        this.firing = tag.getBoolean("Firing");
    }


    // GETTERS & SETTERS (for compatibility)
    @SuppressWarnings("unused")
    public Direction getBlockDirection() {
        return this.facing;
    }

    @SuppressWarnings("unused")
    public double getAirflow() {
        return 0;
    }

    @SuppressWarnings("unused")
    public double getThrust() {
        return this.thrust;
    }

    @SuppressWarnings("unused")
    public boolean isActive() {
        return this.firing;
    }
}
