package net.beholderface.ephemera.forge;

import at.petrak.hexcasting.client.RegisterClientStuff;
import net.beholderface.ephemera.CopiedSlateRenderer;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.EphemeraClient;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Forge client loading entrypoint.
 */
public class EphemeraClientForge {
    public static void init(FMLClientSetupEvent event) {
        EphemeraClient.init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((EntityRenderersEvent.RegisterRenderers evt)->{
            Ephemera.LOGGER.info("Registering ephemera renderers BUT HOPEFULLY WORKING THIS TIME");
            evt.registerBlockEntityRenderer(EphemeraBlockRegistry.SNEAKY_SLATE_ENTITY.get(), CopiedSlateRenderer::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        Ephemera.LOGGER.info("Registering ephemera renderers.");
        evt.registerBlockEntityRenderer(EphemeraBlockRegistry.SNEAKY_SLATE_ENTITY.get(), CopiedSlateRenderer::new);
    }
}
