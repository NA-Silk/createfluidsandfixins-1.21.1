package com.nasilk.createfluidsandfixins.block;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.block.entity.PropulsiteBrokenBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateFluidsAndFixins.MOD_ID);

    public static final Supplier<BlockEntityType<PropulsiteBrokenBlockEntity>> PROPULSITE_BROKEN = BLOCK_ENTITIES.register(
        "propulsite_broken",
        () -> BlockEntityType.Builder.of(
            PropulsiteBrokenBlockEntity::new,
            ModBlocks.PROPULSITE_BROKEN.get()
        )
            .build(null)
    );

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
