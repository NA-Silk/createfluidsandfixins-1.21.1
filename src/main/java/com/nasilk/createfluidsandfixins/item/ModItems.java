package com.nasilk.createfluidsandfixins.item;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.block.ModBlocks;
import com.nasilk.createfluidsandfixins.item.custom.CreativeFluidEraserItem;
import com.nasilk.createfluidsandfixins.item.custom.PebbleItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateFluidsAndFixins.MOD_ID);

    public static final DeferredItem<CreativeFluidEraserItem> CREATIVE_FLUID_ERASER = ITEMS.registerItem(
        "creative_fluid_eraser",
        (properties) -> new CreativeFluidEraserItem(
            properties.stacksTo(1),
            32
        )
    );


    public static void register(IEventBus eventbus) {
      ITEMS.register(eventbus);
    }
}
