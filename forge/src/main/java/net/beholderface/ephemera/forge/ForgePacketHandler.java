package net.beholderface.ephemera.forge;

import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.networking.ParticleBurstPacket;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

//dang yoinking code from hexal is useful
public class ForgePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            Ephemera.id("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static SimpleChannel getNetwork() {
        return NETWORK;
    }

    public static void init() {
        int messageIdx = 0;
        NETWORK.registerMessage(messageIdx++, ParticleBurstPacket.class, ParticleBurstPacket::serialize,
                ParticleBurstPacket::deserialise, makeClientBoundHandler(ParticleBurstPacket::handle));
    }

    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> makeClientBoundHandler(Consumer<T> consumer) {
        return (m, ctx) -> {
            consumer.accept(m);
            ctx.get().setPacketHandled(true);
        };
    }
}
