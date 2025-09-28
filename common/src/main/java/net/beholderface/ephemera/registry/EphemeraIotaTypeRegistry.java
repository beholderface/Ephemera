package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.HexRegistries;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.registry.registries.DeferredRegister;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.casting.iotatypes.HashIota;
import net.beholderface.ephemera.casting.iotatypes.PotionIota;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EphemeraIotaTypeRegistry {
    public static final DeferredRegister<IotaType<?>> IOTATYPES = DeferredRegister.create(Ephemera.MOD_ID, HexRegistries.IOTA_TYPE);

    public static final IotaType<PotionIota> POTION = type("potion", PotionIota.TYPE);
    public static final IotaType<HashIota> HASH = type("hash", HashIota.TYPE);

    private static boolean alreadyInit = false;
    public static void init() {
        if (!alreadyInit){
            alreadyInit = true;
            IOTATYPES.register();
        }
    }

    private static <U extends Iota, T extends IotaType<U>> T type(String name, T type) {
        Identifier id = Ephemera.id(name);
        IOTATYPES.register(id, ()->Registry.register(IXplatAbstractions.INSTANCE.getIotaTypeRegistry(), id, type));
        //Ephemera.LOGGER.info("Adding " + name + " to iota type map.");
        return type;
    }
}
