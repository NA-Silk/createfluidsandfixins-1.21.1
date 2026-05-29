package com.nasilk.createfluidsandfixins.item.custom;

import com.nasilk.createfluidsandfixins.block.ModBlocks;
import com.nasilk.createfluidsandfixins.util.TransformItemUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import java.util.HashMap;

public class ChoraCasingItem extends BlockItem {
    public final HashMap<Block, Block> BLOCK_MAP = new HashMap<>();

    public ChoraCasingItem(Block block, Properties properties) {
        super(block, properties);
        BLOCK_MAP.put(ModBlocks.PROPULSITE_BLOCK.get(), ModBlocks.PROPULSITE_THRUSTER.get());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return TransformItemUtil.tryTransform(context, BLOCK_MAP).orElseGet(() -> super.useOn(context));
    }
}
