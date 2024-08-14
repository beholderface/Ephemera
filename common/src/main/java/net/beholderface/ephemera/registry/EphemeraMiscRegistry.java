package net.beholderface.ephemera.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.items.ShameEnchantment;
import net.beholderface.ephemera.status.MemeticCureEffect;
import net.beholderface.ephemera.status.MemeticDiseaseEffect;
import net.beholderface.ephemera.status.MemeticPreventionEffect;
import net.beholderface.ephemera.status.MissingEffect;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;

public class EphemeraMiscRegistry {
    public static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Ephemera.MOD_ID, Registry.MOB_EFFECT_KEY);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Ephemera.MOD_ID, Registry.ENCHANTMENT_KEY);

    public static final RegistrySupplier<MissingEffect> MISSING = EFFECTS.register("missing", MissingEffect::new);
    public static final RegistrySupplier<MemeticDiseaseEffect> BRAINROT = EFFECTS.register("brainrot", MemeticDiseaseEffect::new);
    public static final RegistrySupplier<MemeticCureEffect> BRAINROT_CURE = EFFECTS.register("brainrot_cure", MemeticCureEffect::new);
    public static final RegistrySupplier<MemeticPreventionEffect> BRAINROT_PREVENTION = EFFECTS.register("brainrot_prevention", MemeticPreventionEffect::new);

    public static final RegistrySupplier<ShameEnchantment> SHAME_CURSE = ENCHANTMENTS.register("shame", ShameEnchantment::new);


    public static void init(){
        EFFECTS.register();
        ENCHANTMENTS.register();
    }

}
