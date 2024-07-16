package net.beholderface.ephemera;

import com.mojang.datafixers.util.Either;
import dev.architectury.event.events.client.ClientTickEvent;
import net.beholderface.ephemera.status.MemeticDiseaseEffect;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;

/**
 * Common client loading entrypoint.
 */
public class EphemeraClient {

    public static void init() {
        ClientTickEvent.CLIENT_POST.register((client)-> MemeticDiseaseEffect.processDiseaseRetention(Either.right(client)));
    }
}
