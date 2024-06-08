package net.beholderface.ephemera.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.beholderface.ephemera.EphemeraAbstractions;

import java.nio.file.Path;

public class EphemeraAbstractionsImpl {
    /**
     * This is the actual implementation of {@link EphemeraAbstractions#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
	
    public static void initPlatformSpecific() {
        EphemeraConfigFabric.init();
    }
}
