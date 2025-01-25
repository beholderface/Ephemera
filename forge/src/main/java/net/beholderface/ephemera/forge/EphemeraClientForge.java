package net.beholderface.ephemera.forge;

import net.beholderface.ephemera.EphemeraClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client loading entrypoint.
 */
//@Mod.EventBusSubscriber
public class EphemeraClientForge {
    public static void init(FMLClientSetupEvent event) {
        EphemeraClient.init();
        //FMLJavaModLoadingContext.get().getModEventBus().register(EphemeraClientForge.class);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        //Ephemera.LOGGER.info("Registering ephemera renderers.");
    }
}
