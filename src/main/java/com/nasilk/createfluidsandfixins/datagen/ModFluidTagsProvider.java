package com.nasilk.createfluidsandfixins.datagen;

/* MCCourse imports
 * import net.kaupenjoe.mccourse.MCCourseMod;
 * import net.kaupenjoe.mccourse.fluid.ModFluids;
 */
import com.nasilk.createfluidsandfixins.CreateFluidsAndFixins;
import com.nasilk.createfluidsandfixins.fluid.ModFluids;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, CreateFluidsAndFixins.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(FluidTags.WATER)
                .add(ModFluids.SOURCE_DENSITE_SOLUTION_WATER.get())
                .add(ModFluids.FLOWING_DENSITE_SOLUTION_WATER.get());
    }
}
