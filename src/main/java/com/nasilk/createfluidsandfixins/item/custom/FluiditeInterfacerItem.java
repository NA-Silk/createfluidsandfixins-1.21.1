package com.nasilk.createfluidsandfixins.item.custom;

import com.nasilk.createfluidsandfixins.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import java.util.HashMap;

public class FluiditeInterfacerItem extends Item {
    public final HashMap<Block, Block> BLOCK_MAP = new HashMap<>();

    public FluiditeInterfacerItem(Properties properties) {
        super(properties);
        BLOCK_MAP.put(ModBlocks.PROPULSITE_BLOCK.get(), ModBlocks.PROPULSITE_THRUSTER.get());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        // Only use on BLOCK action
        if (blockhitresult.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(itemStack);

        // Get BlockState at targetPos
        BlockPos targetPos = blockhitresult.getBlockPos();
        BlockState state = level.getBlockState(targetPos);
        if (!level.isClientSide() && !state.isEmpty()) {
            // Find matching block
            Block transformBlock = BLOCK_MAP.get(state.getBlock());

            // Replace logic
            if (transformBlock == null) return InteractionResultHolder.pass(itemStack);
            Direction direction = blockhitresult.getDirection();
            level.setBlockAndUpdate(targetPos, transformBlock.defaultBlockState().setValue(BlockStateProperties.FACING, direction));

            return InteractionResultHolder.success(itemStack);
        }
        return InteractionResultHolder.pass(itemStack);
    }
}
