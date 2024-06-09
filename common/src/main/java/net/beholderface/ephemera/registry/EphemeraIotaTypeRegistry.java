package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.beholderface.ephemera.Ephemera;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class EphemeraIotaTypeRegistry {
    public static Map<Identifier, IotaType<?>> TYPES = new HashMap<>();

    public static final IotaType<PotionIota> POTION = type("potion", PotionIota.TYPE);

    public static void init() {
        //Ephemera.LOGGER.info("Attempting to register iota types.");
        for (Map.Entry<Identifier, IotaType<?>> entry : TYPES.entrySet()) {
            Registry.register(HexIotaTypes.REGISTRY, entry.getKey(), entry.getValue());
        }
    }

    private static <U extends Iota, T extends IotaType<U>> T type(String name, T type) {
        IotaType<?> old = TYPES.put(Ephemera.id(name), type);
        //Ephemera.LOGGER.info("Adding " + name + " to iota type map.");
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + name);
        }
        return type;
    }
}
