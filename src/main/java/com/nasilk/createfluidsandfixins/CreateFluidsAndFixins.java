package com.nasilk.createfluidsandfixins;

import com.nasilk.createfluidsandfixins.block.ModBlockEntities;
import com.nasilk.createfluidsandfixins.block.ModBlocks;
import com.nasilk.createfluidsandfixins.fluid.ModFluidTypes;
import com.nasilk.createfluidsandfixins.fluid.ModFluids;
import com.nasilk.createfluidsandfixins.item.ModItems;
import com.nasilk.createfluidsandfixins.behavior.ModDispenserBehaviors;
import com.nasilk.createfluidsandfixins.particle.DensiteParticles;
import com.nasilk.createfluidsandfixins.particle.ModParticles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateFluidsAndFixins.MOD_ID)
public class CreateFluidsAndFixins {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "createfluidsandfixins";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when the mod is loaded
    public CreateFluidsAndFixins(IEventBus modEventBus, ModContainer modContainer) {
        // Custom registrations
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModFluidTypes.register(modEventBus); // Fluid textures
        ModFluids.register(modEventBus); // Fluid behaviors
        ModParticles.register(modEventBus);

        // Register ourselves for server and other game events
        NeoForge.EVENT_BUS.register(this);

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        // Register the items to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register ModConfigSpec so that FML can create and load the config file
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModDispenserBehaviors.register();
        });

        LOGGER.info("Create: Fluids and Fixins loaded");
    }

    // Add block items to creative tabs
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModFluids.VOID_SEA_SLURRY_BUCKET);
            event.accept(ModFluids.DENSITE_EMULSION_BUCKET);
            event.accept(ModFluids.DRIFT_CONDENSATE_BUCKET);
            event.accept(ModFluids.PROPULSITE_FLURRY_BUCKET);
            event.accept(ModFluids.TEMP_BUCKET);
            event.accept(ModItems.CREATIVE_FLUID_ERASER);
        }

        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.DENSITE_BLOCK);
            event.accept(ModBlocks.PROPULSITE_BLOCK);
            event.accept(ModBlocks.PROPULSITE_BROKEN);
            event.accept(ModBlocks.TEMP_BLOCK);
        }
    }

    // @SubscribeEvent lets the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // @EventBusSubscriber automatically registers all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_VOID_SEA_SLURRY.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_VOID_SEA_SLURRY.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_DENSITE_EMULSION.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_DENSITE_EMULSION.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_DRIFT_CONDENSATE.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_DRIFT_CONDENSATE.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_PROPULSITE_FLURRY.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_PROPULSITE_FLURRY.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_TEMP.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_TEMP.get(), RenderType.translucent());
            });
        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.DENSITE_PARTICLES.get(), DensiteParticles.Provider::new);
        }
    }
}
