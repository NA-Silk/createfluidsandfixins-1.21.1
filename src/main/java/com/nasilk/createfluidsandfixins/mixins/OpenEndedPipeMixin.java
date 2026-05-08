package com.nasilk.createfluidsandfixins.mixins;

import com.nasilk.createfluidsandfixins.fluid.ModFluids;
import com.nasilk.createfluidsandfixins.util.MixinSettings;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OpenEndedPipe.class)
public class OpenEndedPipeMixin {
    @Shadow private Level world;
    @Shadow private BlockPos outputPos;

    @Inject(method = "removeFluidFromSpace", at = @At("HEAD"), cancellable = true)
    private void removeFluidFromSpaceMixin(boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        int yVoidSeaSlurry = new MixinSettings().yVoidSeaSlurry;

        // Error check
        if (world == null) return;

        // Check if in END and y <= yVoidSeaSlurry
        if (world.dimension() == Level.END && outputPos.getY() < yVoidSeaSlurry) {
            System.out.println("Void Sea Slurry extraction triggered at " + outputPos);

            // Return void sea slurry as if it was extracted
            cir.setReturnValue(new FluidStack(ModFluids.SOURCE_VOID_SEA_SLURRY.get(), 500));
        }
    }
}
