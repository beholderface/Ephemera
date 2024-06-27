package net.beholderface.ephemera;

import dev.architectury.event.events.common.LifecycleEvent;
import net.beholderface.ephemera.recipe.EphemeraRecipeSerializer;
import net.beholderface.ephemera.recipe.EphemeraRecipeTypes;
import net.beholderface.ephemera.registry.*;
import net.beholderface.ephemera.networking.EphemeraNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is effectively the loading entrypoint for most of your code, at least
 * if you are using Architectury as intended.
 */
public class Ephemera {
    public static final String MOD_ID = "ephemera";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static MinecraftServer CACHED_SERVER = null;


    public static void init() {
        LOGGER.info("bee");
        EphemeraMiscRegistry.init();
        EphemeraAbstractions.initPlatformSpecific();
        EphemeraBlockRegistry.init();
        EphemeraItemRegistry.init();
        EphemeraIotaTypeRegistry.init();
        EphemeraPatternRegistry.init();
		EphemeraNetworking.init();

        EphemeraRecipeSerializer.registerSerializers(EphemeraRecipeTypes.Companion.bind(Registry.RECIPE_SERIALIZER));
        EphemeraRecipeTypes.registerTypes(EphemeraRecipeTypes.Companion.bind(Registry.RECIPE_TYPE));

        LOGGER.info(EphemeraAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());

        LifecycleEvent.SERVER_BEFORE_START.register((startedserver) ->{
            CACHED_SERVER = startedserver;
        });
    }

    public static MinecraftServer getCachedServer(){
        return CACHED_SERVER;
    }

    //for kotlin which doesn't seem to have a getBytes method
    public static byte[] getKTbytes(String s){
        return s.getBytes();
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static Identifier id(String string) {
        return new Identifier(MOD_ID, string);
    }
}
