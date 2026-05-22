package com.nasilk.createfluidsandfixins.block;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.behavior.PropulsiteCTBehavior;
import com.nasilk.createfluidsandfixins.block.custom.DensiteBlock;
import com.nasilk.createfluidsandfixins.block.custom.PropulsiteBlock;
import com.nasilk.createfluidsandfixins.block.custom.PropulsiteBrokenBlock;
import com.nasilk.createfluidsandfixins.item.ModItems;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import com.tterrag.registrate.util.entry.BlockEntry;

@SuppressWarnings({"deprecation", "SameParameterValue"})
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(CreateFluidsAndFixins.MOD_ID);

    public static final DeferredBlock<Block> DENSITE_BLOCK = registerBlock(
        "densite_block",
        () -> new DensiteBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .isRedstoneConductor((state, level, pos) -> true)
            .strength(2.0f, 9.0f)
            .jumpFactor(0.5f)
            .friction(0.8f)
            .lightLevel(state -> state.getValue(DensiteBlock.POWER))
            .hasPostProcess((state, pos, level) -> true)
            .emissiveRendering((state, pos, level) -> true)
            .sound(
                new SoundType(1.0f, 0.1f,
                    SoundEvents.ENDER_EYE_DEATH,
                    SoundEvents.NETHERITE_BLOCK_STEP,
                    SoundEvents.ENDER_EYE_DEATH, // Might need another sound or pitch override
                    SoundEvents.NETHERITE_BLOCK_HIT,
                    SoundEvents.NETHERITE_BLOCK_FALL
                )
            )
        )
    );

    public static final BlockEntry<Block> PROPULSITE_BLOCK = registerCTBlock(
        "propulsite_block",
        (properties) -> new PropulsiteBlock(BlockBehaviour.Properties.of() // I don't know what this does, but if I delete it, it crashes the game
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .friction(1.01f)
            .sound(SoundType.GLASS)
            .lightLevel(state -> 6)
            .isValidSpawn((state, level, pos, value) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false)
        ),
        PropulsiteCTBehavior::new
    );

    public static final DeferredBlock<Block> PROPULSITE_BROKEN = registerBlock(
        "propulsite_broken",
        () -> new PropulsiteBrokenBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .friction(1.01f)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .lightLevel(state ->6)
            .isValidSpawn((state, level, pos, value) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false)
            .sound(
                new SoundType(1.0f, 1.0f,
                    SoundEvents.GLASS_BREAK,
                    SoundEvents.GLASS_STEP,
                    SoundEvents.GLASS_PLACE,
                    SoundEvents.GLASS_HIT,
                    SoundEvents.GLASS_FALL
                )
            )
        )
    );

    public static final DeferredBlock<Block> TEMP_BLOCK = registerBlock(
        "temp_block",
        () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GRAY)
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.3F)
            .friction(1.05f)
            .sound(SoundType.GLASS)
            .lightLevel(state -> 6)
            .noOcclusion()
            .isValidSpawn((state, level, pos, value) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false)
            .sound(
                new SoundType(1.0f, 1.0f,
                    SoundEvents.GLASS_BREAK,
                    SoundEvents.GLASS_STEP,
                    SoundEvents.GLASS_PLACE,
                    SoundEvents.GLASS_HIT,
                    SoundEvents.GLASS_FALL
                )
            )
        )
    );

    private static <T extends Block> BlockEntry<T> registerCTBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> factory, Supplier<ConnectedTextureBehaviour> behavior) {
        BlockEntry<T> toReturn = CreateFluidsAndFixins.REGISTRATE.block(name, factory)
            .onRegister(CreateRegistrate.connectedTextures(behavior))
            .register();
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, Supplier<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
