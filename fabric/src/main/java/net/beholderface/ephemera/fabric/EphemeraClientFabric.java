package net.beholderface.ephemera.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.beholderface.ephemera.EphemeraClient;

/**
 * Fabric client loading entrypoint.
 */
public class EphemeraClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EphemeraClient.init();
    }
}
