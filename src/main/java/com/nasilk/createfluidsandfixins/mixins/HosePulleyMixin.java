package com.nasilk.createfluidsandfixins.mixins;

import com.nasilk.createfluidsandfixins.fluid.ModFluids;
import com.nasilk.createfluidsandfixins.util.MixinSettings;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidDrainingBehaviour.class)
public class HosePulleyMixin {
    @Inject(method = "getDrainableFluid", at = @At("HEAD"), cancellable = true)
    private void getDrainableFluidMixin(BlockPos rootPos, CallbackInfoReturnable<FluidStack> cir) {
        int yVoidSeaSlurry = new MixinSettings().yVoidSeaSlurry;
        Level world = ((BlockEntityBehaviour)(Object)this).getWorld();

        // Error check
        if (world == null) return;

        // Check if in END and y <= yVoidSeaSlurry
        if (world.dimension() == Level.END && rootPos.getY() < yVoidSeaSlurry) {
            System.out.println("Void Sea Slurry extraction triggered at " + rootPos);

            // Return void sea slurry as if it was extracted
            cir.setReturnValue(new FluidStack(ModFluids.SOURCE_VOID_SEA_SLURRY.get(), 500));
        }
    }
}
