package net.beholderface.ephemera.api.xplat;

import at.petrak.hexcasting.api.spell.iota.Iota;
import net.beholderface.ephemera.Ephemera;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface EphemeraXPlat {
    EphemeraXPlat INSTANCE = find();
    //I did this at first because I wanted to make routing numbers for link relays, and forgot that I had already theorized a block that you just slap them
    Optional<Iota> getAttachedIota(String key);

    private static EphemeraXPlat find() {
        var providers = ServiceLoader.load(EphemeraXPlat.class).stream().toList();
        if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException(
                    "There should be exactly one EphemeraXPlat implementation on the classpath. Found: " + names);
        } else {
            var provider = providers.get(0);
            Ephemera.LOGGER.debug("Instantiating ephemera xplat impl: " + provider.type().getName());
            return provider.get();
        }
    }
}
