package com.nasilk.createfluidsandfixins;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;

import net.minecraft.resources.ResourceLocation;

public class ModSpriteShifts {

    public static final CTSpriteShiftEntry PROPULSITE =
            CTSpriteShifter.getCT(
                    AllCTTypes.OMNIDIRECTIONAL,
                    ResourceLocation.fromNamespaceAndPath(
                            "createfluidsandfixins",
                            "block/propulsite_block"
                    ),
                    ResourceLocation.fromNamespaceAndPath(
                            "createfluidsandfixins",
                            "block/propulsite_block_connected"
                    )
            );

    public static void init() {}

}