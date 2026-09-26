package net.beholderface.ephemera;

import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EphemeraMixinUtils {
    public static double breakBoost = 0.0;

    private static final Map<UUID, Long> timeFreezeMap = new HashMap<>();
    public static boolean checkCancelTick(Entity entity, boolean simulate){
        if (timeFreezeMap.containsKey(entity.getUuid())){
            long freezeDuration = timeFreezeMap.get(entity.getUuid());
            if (freezeDuration > 0){
                freezeDuration--;
                if (!simulate){
                    if (freezeDuration <= 0){
                        timeFreezeMap.remove(entity.getUuid());
                        //Ephemera.LOGGER.info("Time freeze on {} has expired at timestamp {}", entity.getName().getString(), entity.getWorld().getTime());
                    } else {
                        timeFreezeMap.put(entity.getUuid(), freezeDuration);
                    }
                }
                return true;
            } else if (!simulate || freezeDuration < 0) {
                timeFreezeMap.remove(entity.getUuid());
            }
        }
        return false;
    }
    public static boolean shouldCancelTick(Entity entity){
        return checkCancelTick(entity, true);
    }

    public static long getFreezeTicks(Entity entity){
        return timeFreezeMap.getOrDefault(entity.getUuid(), -1L);
    }

    public static void freeze(Entity entity, long duration){
        timeFreezeMap.put(entity.getUuid(), duration);
    }
    public static void unfreeze(Entity entity){
        timeFreezeMap.remove(entity.getUuid());
    }
}
