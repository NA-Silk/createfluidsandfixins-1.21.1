package com.nasilk.createfluidsandfixins;


import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PropulsiteCTBehaviour extends ConnectedTextureBehaviour.Base {

    @Override
    @Nullable

    public CTSpriteShiftEntry getShift (BlockState state, Direction direction,

    @Nullable TextureAtlasSprite sprite) {return ModSpriteShifts.PROPULSITE;
    }
}