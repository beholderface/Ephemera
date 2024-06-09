package net.beholderface.ephemera;

import net.beholderface.ephemera.registry.EphemeraIotaTypeRegistry;
import net.beholderface.ephemera.registry.EphemeraItemRegistry;
import net.beholderface.ephemera.registry.EphemeraMiscRegistry;
import net.beholderface.ephemera.registry.EphemeraPatternRegistry;
import net.beholderface.ephemera.networking.EphemeraNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is effectively the loading entrypoint for most of your code, at least
 * if you are using Architectury as intended.
 */
public class Ephemera {
    public static final String MOD_ID = "ephemera";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


    public static void init() {
        LOGGER.info("bee");
        EphemeraMiscRegistry.init();
        EphemeraAbstractions.initPlatformSpecific();
        EphemeraItemRegistry.init();
        EphemeraIotaTypeRegistry.init();
        EphemeraPatternRegistry.init();
		EphemeraNetworking.init();

        LOGGER.info(EphemeraAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static Identifier id(String string) {
        return new Identifier(MOD_ID, string);
    }
}
