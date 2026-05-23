package com.nasilk.createfluidsandfixins.util;

import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import net.minecraft.resources.ResourceLocation;

public class ModSpriteShifts {
    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(
            "createfluidsandfixins",
            path
        );
    }

    public static final CTSpriteShiftEntry PROPULSITE = CTSpriteShifter.getCT(
        AllCTTypes.OMNIDIRECTIONAL,
        rl("block/propulsite_block"),
        rl("block/propulsite_block_connected")

    );
    public static final CTSpriteShiftEntry DENSITE = CTSpriteShifter.getCT(
        AllCTTypes.OMNIDIRECTIONAL,
        rl("block/densite_block"),
        rl("block/densite_block_connected")
    );

    public static final CTSpriteShiftEntry DENSITE_1 = CTSpriteShifter.getCT(
        AllCTTypes.OMNIDIRECTIONAL,
        rl("block/densite_block"),
        rl("block/densite_connected_activated/densite_block_connected_1")
    );

    public static final CTSpriteShiftEntry DENSITE_2 = CTSpriteShifter.getCT(
        AllCTTypes.OMNIDIRECTIONAL,
        rl("block/densite_block"),
        rl("block/densite_connected_activated/densite_block_connected_2")
    );

    public static final CTSpriteShiftEntry DENSITE_3 = CTSpriteShifter.getCT(
        AllCTTypes.OMNIDIRECTIONAL,
        rl("block/densite_block"),
        rl("block/densite_connected_activated/densite_block_connected_3")
    );

    public static final CTSpriteShiftEntry DENSITE_4 = CTSpriteShifter.getCT(
        AllCTTypes.OMNIDIRECTIONAL,
        rl("block/densite_block"),
        rl("block/densite_connected_activated/densite_block_connected_4")
    );


    public static void init() {
        CreateFluidsAndFixins.LOGGER.info("Connected textures initialized");
    }
}
