package net.beholderface.ephemera.forge;

import net.beholderface.ephemera.EphemeraAbstractions;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class EphemeraAbstractionsImpl {
    /**
     * This is the actual implementation of {@link EphemeraAbstractions#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
	
    public static void initPlatformSpecific() {
        EphemeraConfigForge.init();
    }
}
