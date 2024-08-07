package net.beholderface.ephemera.casting.patterns.spells;

import at.petrak.hexcasting.api.misc.MediaConstants;
import net.beholderface.ephemera.Ephemera;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RevealHistoryManager {
    private static final Map<UUID, Long> HISTORY_MAP = new HashMap<>();
    public static final int minuteInTicks = 20 * 60;

    public static int calculateCost(UUID uuid, long currentTime){
        int defaultCost = MediaConstants.DUST_UNIT / 10;
        if (!HISTORY_MAP.containsKey(uuid) || (HISTORY_MAP.containsKey(uuid) && (HISTORY_MAP.get(uuid) < currentTime - minuteInTicks || HISTORY_MAP.get(uuid) > currentTime))){
            return defaultCost;
        } else {
            long storedTime = HISTORY_MAP.get(uuid);
            long timeDifference = currentTime - storedTime;
            assert timeDifference >= 0 && timeDifference <= minuteInTicks;
            //janky transcription of a function I toyed around with in Desmos for a while
            //yes I know it's terrible
            double rawOutput = (10.0 - (((Math.pow(Math.pow(minuteInTicks / 10.0, 2) - Math.pow((timeDifference / 10.0)
                    - (minuteInTicks / 10.0), 2), 0.5) * ((5.0 / 3.0 / 20.0) * -1)) + ((minuteInTicks/10.0) * ((5.0 / 3.0 / 20.0) * -1))) / -2)) * 2;
            //Ephemera.LOGGER.info(rawOutput);
            double coercedOutput = Math.max(rawOutput, 0.1);
            return (int)(coercedOutput * MediaConstants.DUST_UNIT);
        }
    }

    public static void notifyReveal(UUID target, ServerWorld world){
        HISTORY_MAP.put(target, world.getServer().getOverworld().getTime());
    }

    public static void removeEntry(UUID target){
        HISTORY_MAP.remove(target);
    }

    public static long getTimestamp(UUID target){
        return HISTORY_MAP.getOrDefault(target, -1L);
    }
}
