package net.beholderface.ephemera;

import com.mojang.datafixers.util.Either;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.beholderface.ephemera.items.ConjuredArmorItem;
import net.beholderface.ephemera.recipe.EphemeraRecipeSerializer;
import net.beholderface.ephemera.recipe.EphemeraRecipeTypes;
import net.beholderface.ephemera.registry.*;
import net.beholderface.ephemera.networking.EphemeraNetworking;
import net.beholderface.ephemera.status.MemeticDiseaseEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.beholderface.ephemera.items.ConjuredArmorItem.SHAME_MAP;

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
        EphemeraAbstractions.initPlatformSpecific();
        EphemeraBlockRegistry.init();
        EphemeraMiscRegistry.init();
        EphemeraItemRegistry.init();
        EphemeraIotaTypeRegistry.init();
        EphemeraPatternRegistry.init();
		EphemeraNetworking.init();

        EphemeraRecipeSerializer.registerSerializers(EphemeraRecipeTypes.Companion.bind(Registry.RECIPE_SERIALIZER));
        EphemeraRecipeTypes.registerTypes(EphemeraRecipeTypes.Companion.bind(Registry.RECIPE_TYPE));

        LOGGER.info(EphemeraAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());

        LifecycleEvent.SERVER_STARTED.register((startedserver)->{
            if (SHAME_MAP.isEmpty()){
                SHAME_MAP.put(EphemeraMiscRegistry.SHAME_CURSE.get(), 1);
            }
        });
        LifecycleEvent.SERVER_BEFORE_START.register((startedserver) ->{
            CACHED_SERVER = startedserver;
        });

        TickEvent.SERVER_POST.register((server)->{
            MemeticDiseaseEffect.processDiseaseRetention(Either.left(server));
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

    public static void boolLogger(String str, boolean enabled){
        if (enabled){
            LOGGER.info(str);
        }
    }
}
