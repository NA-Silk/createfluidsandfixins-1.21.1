package com.nasilk.createfluidsandfixins.item;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Moditems {
     public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateFluidsAndFixins.MOD_ID);


      public static final DeferredItem<Item> DENSITEBUCKET = ITEMS.register("densitebucket",
              () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventbus) {
          ITEMS.register(eventbus);
     }
}