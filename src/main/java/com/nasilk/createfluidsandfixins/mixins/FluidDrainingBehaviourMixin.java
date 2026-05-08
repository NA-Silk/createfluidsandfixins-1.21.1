package com.nasilk.createfluidsandfixins.mixins;

import com.nasilk.createfluidsandfixins.util.MixinSettings;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidDrainingBehaviour.class)
public class FluidDrainingBehaviourMixin {
    @Inject(method = "pullNext", at = @At("HEAD"), cancellable = true)
    private void pullNextMixin(BlockPos root, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        int yVoidSeaSlurry = new MixinSettings().yVoidSeaSlurry;
        Level world = ((BlockEntityBehaviour)(Object)this).getWorld();

        // Error check
        if (world == null) return;

        // Check if in END and y <= yVoidSeaSlurry
        if (world.dimension() == Level.END && root.getY() < yVoidSeaSlurry) {
            System.out.println("Void Sea Slurry extraction permitted at " + root.getY());

            // Pretend there is something to pull so HosePulleyFluidHandler proceeds
            cir.setReturnValue(true);
        }
    }
}
