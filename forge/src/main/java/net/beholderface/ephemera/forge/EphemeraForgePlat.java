package net.beholderface.ephemera.forge;

import at.petrak.hexcasting.api.spell.iota.Iota;
import net.beholderface.ephemera.api.xplat.EphemeraXPlat;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public class EphemeraForgePlat implements EphemeraXPlat {
    @Override
    public Optional<Iota> getAttachedIota(String key) {
        return Optional.empty();
    }

    @Override
    public MinecraftServer getCachedServer() {
        return null;
    }
}
