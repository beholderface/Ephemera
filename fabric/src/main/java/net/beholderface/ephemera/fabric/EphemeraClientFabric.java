package net.beholderface.ephemera.fabric;

import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.beholderface.ephemera.EphemeraClient;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

/**
 * Fabric client loading entrypoint.
 */
public class EphemeraClientFabric implements ClientModInitializer {

    private static int applyBlockRenderLayers(Block[] blocks, RenderLayer layer){
        int applied = 0;
        for (Block block : blocks){
            BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
            applied++;
        }
        return applied;
    }

    @Override
    public void onInitializeClient() {
        EphemeraClient.init();
        FabricPacketHandler.INSTANCE.initClientBound();

        /*Block[] cutoutBlocks = {EphemeraBlockRegistry.TP_DETECTOR.get()};
        Block[] translucentBlocks = {};
        Ephemera.LOGGER.info("Applied cutout layer to " + applyBlockRenderLayers(cutoutBlocks, RenderLayer.getCutout()) + " blocks");
        Ephemera.LOGGER.info("Applied translucent layer to " + applyBlockRenderLayers(translucentBlocks, RenderLayer.getTranslucent()) + " blocks");*/

    }
}
