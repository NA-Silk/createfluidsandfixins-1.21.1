package com.nasilk.createfluidsandfixins.block;

/* MCCourse imports
 * import net.kaupenjoe.mccourse.MCCourseMod;
 * import net.kaupenjoe.mccourse.block.custom.CrystallizerBlock;
 * import net.kaupenjoe.mccourse.block.custom.MagicBlock;
 * import net.kaupenjoe.mccourse.block.custom.PedestalBlock;
 * import net.kaupenjoe.mccourse.item.ModItems;
 * import net.kaupenjoe.mccourse.sound.ModSounds;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
 */
import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.item.ModItems;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
              DeferredRegister.createBlocks(CreateFluidsAndFixins.MOD_ID);

    public static final DeferredBlock<Block> DENSITEBLOCK = registerBlock("densiteblock",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(2.0f, 9.0f)
                    .jumpFactor(0.5f)
                    .friction(0.9f)
                    .lightLevel(state -> 2) //add seperate glow layer texture later _e
                    .hasPostProcess((state, pos, level) -> true)
                    .emissiveRendering((state, pos, level) -> true)
                    .sound(new SoundType(1.0f, 0.1f,
                            SoundEvents.ENDER_EYE_DEATH,
                            SoundEvents.NETHERITE_BLOCK_STEP,
                            SoundEvents.ENDER_EYE_DEATH, //might need another sound or pitch overide
                            SoundEvents.NETHERITE_BLOCK_HIT,
                            SoundEvents.NETHERITE_BLOCK_FALL
                    ))
            )
);
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
