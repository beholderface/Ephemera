package net.beholderface.ephemera.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.casting.MissingEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

public class EphemeraMiscRegistry {
    public static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Ephemera.MOD_ID, Registry.MOB_EFFECT_KEY);
    public static final RegistrySupplier<MissingEffect> MISSING = EFFECTS.register("missing", MissingEffect::new);

    public static void init(){
        EFFECTS.register();
    }

}
