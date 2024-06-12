package net.beholderface.ephemera.forge;

import net.beholderface.ephemera.EphemeraClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client loading entrypoint.
 */
public class EphemeraClientForge {
    public static void init(FMLClientSetupEvent event) {
        EphemeraClient.init();
    }
}
