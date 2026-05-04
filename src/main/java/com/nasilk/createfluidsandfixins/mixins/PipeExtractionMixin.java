package com.nasilk.createfluidsandfixins.mixins;

import com.simibubi.create.content.fluids.OpenEndedPipe;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;

import net.neoforged.neoforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OpenEndedPipe.class)
public class PipeExtractionMixin {

    @Shadow private Level world;
    @Shadow private BlockPos outputPos;

    @Inject(method = "removeFluidFromSpace", at = @At("HEAD"), cancellable = true)
    private void removeFluidFromSpaceMixin(boolean simulate, CallbackInfoReturnable<FluidStack> cir) {

        // Error check
        if (world == null)
            return;

        // Check if in END and y <= 5
        if (world.dimension() == Level.END && outputPos.getY() < 5) {

            // Debug message
            System.out.println("Custom End fluid extraction triggered at " + outputPos);

            // Return lava as if it was extracted
            cir.setReturnValue(new FluidStack(Fluids.LAVA, 1000));
        }
    }
}
