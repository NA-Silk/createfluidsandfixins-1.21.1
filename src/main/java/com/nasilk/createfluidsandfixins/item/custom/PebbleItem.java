package com.nasilk.createfluidsandfixins.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class PebbleItem extends BlockItem {
    public PebbleItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ItemStack armorStack = player.getItemBySlot(EquipmentSlot.HEAD);

        if (armorStack.isEmpty()) {
            player.setItemSlot(EquipmentSlot.HEAD, itemstack.copyWithCount(1));
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemstack);
    }
}
