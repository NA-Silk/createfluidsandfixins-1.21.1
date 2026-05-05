package com.nasilk.createfluidsandfixins.item;

/*
 * import net.minecraft.world.item.Item;
 * import net.neoforged.neoforge.registries.DeferredItem;
 */

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateFluidsAndFixins.MOD_ID);

    public static void register(IEventBus eventbus) {
      ITEMS.register(eventbus);
    }
}
