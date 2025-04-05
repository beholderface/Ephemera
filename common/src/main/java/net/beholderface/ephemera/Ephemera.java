package net.beholderface.ephemera;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.player.FlightAbility;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Either;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.beholderface.ephemera.casting.ChunkLoadingManager;
import net.beholderface.ephemera.recipe.EphemeraRecipeSerializer;
import net.beholderface.ephemera.recipe.EphemeraRecipeTypes;
import net.beholderface.ephemera.registry.*;
import net.beholderface.ephemera.networking.EphemeraNetworking;
import net.beholderface.ephemera.status.MemeticDiseaseEffect;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ram.talia.hexal.common.entities.BaseWisp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.beholderface.ephemera.items.ConjuredArmorItem.SHAME_MAP;

/**
 * This is effectively the loading entrypoint for most of your code, at least
 * if you are using Architectury as intended.
 */
public class Ephemera {
    public static final String MOD_ID = "ephemera";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static MinecraftServer CACHED_SERVER = null;


    private static DamageSource ACCELERATION_DAMAGE = null;
    public static void init() {
        LOGGER.info("bee");
        EphemeraAbstractions.initPlatformSpecific();
        EphemeraBlockRegistry.init();
        EphemeraMiscRegistry.init();
        EphemeraItemRegistry.init();
        EphemeraIotaTypeRegistry.init();
        EphemeraPatternRegistry.init();
		EphemeraNetworking.init();

        EphemeraRecipeSerializer.registerSerializers(EphemeraRecipeTypes.Companion.bind(Registries.RECIPE_SERIALIZER));
        EphemeraRecipeTypes.registerTypes(EphemeraRecipeTypes.Companion.bind(Registries.RECIPE_TYPE));

        LOGGER.info(EphemeraAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());

        LifecycleEvent.SERVER_STARTED.register((startedserver)->{
            if (SHAME_MAP.isEmpty()){
                SHAME_MAP.put(EphemeraMiscRegistry.SHAME_CURSE.get(), 1);
            }
            ChunkLoadingManager loadingManager = ChunkLoadingManager.getServerState(startedserver);
            RegistryKey<DamageType> accelDamageKey = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("acceleration"));
            Registry<DamageType> damageRegistry = startedserver.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE);
            ACCELERATION_DAMAGE = new DamageSource(damageRegistry.getEntry(damageRegistry.get(accelDamageKey)));
        });
        LifecycleEvent.SERVER_BEFORE_START.register((startedserver) ->{
            CACHED_SERVER = startedserver;
        });

        TickEvent.SERVER_PRE.register((server)->{
            //ChunkLoadingManager.tick();
        });
        TickEvent.SERVER_POST.register((server)->{
            MemeticDiseaseEffect.processDiseaseRetention(Either.left(server));
            processWispAccelDamage(server);
            processFlightReset(server);
        });
    }

    public static MinecraftServer getCachedServer(){
        return CACHED_SERVER;
    }
    public static DamageSource getAccelerationDamage(){
        return ACCELERATION_DAMAGE;
    }

    //for kotlin which doesn't seem to have a getBytes method
    public static byte[] getKTbytes(String s){
        return s.getBytes();
    }

    private static final Map<ServerPlayerEntity, Vec3d> lastVelocityMap = new HashMap<>();
    private static void processWispAccelDamage(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            Vec3d currentVel = HexAPI.instance().getEntityVelocitySpecial(player);
            if (player.getVehicle() instanceof BaseWisp && !player.isInvulnerableTo(player.getDamageSources().flyIntoWall())){
                Vec3d previousVel = lastVelocityMap.get(player);
                Vec3d differenceVel = previousVel.subtract(currentVel);
                float differenceMagnitude = (float) differenceVel.length();
                float threshold = (9.8f * 10.0f) / 20.0f; //the magnitude of the vector if you accelerate by ten Gs in a single tick
                if (differenceMagnitude > threshold){
                    float amountOverThreshold = differenceMagnitude - threshold;
                    float damagePerHalfSecond = amountOverThreshold * 5;
                    player.damage(ACCELERATION_DAMAGE, damagePerHalfSecond);
                    //LOGGER.info(differenceVel + ", " + differenceMagnitude);
                }
            }
            lastVelocityMap.put(player, currentVel);
        }
    }

    private static final Set<ServerPlayerEntity> flightResetSet = new HashSet<>();
    private static void processFlightReset(MinecraftServer server){
        for (ServerPlayerEntity player : flightResetSet){
            if (IXplatAbstractions.INSTANCE.getFlight(player) == null){
                IXplatAbstractions.INSTANCE.setFlight(player, new FlightAbility(0, server.getOverworld().getRegistryKey(), Vec3d.ZERO, 0.0));
            }
        }
        flightResetSet.clear();
    }
    public static boolean notifyFlightReset(ServerPlayerEntity player){
        return flightResetSet.add(player);
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
