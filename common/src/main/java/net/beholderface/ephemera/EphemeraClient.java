package net.beholderface.ephemera;

import at.petrak.hexcasting.common.items.ItemSlate;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import com.mojang.datafixers.util.Either;
import dev.architectury.event.events.client.ClientTickEvent;
import net.beholderface.ephemera.registry.EphemeraItemRegistry;
import net.beholderface.ephemera.status.MemeticDiseaseEffect;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

/**
 * Common client loading entrypoint.
 */
public class EphemeraClient {

    public static void init() {
        ClientTickEvent.CLIENT_POST.register((client)-> MemeticDiseaseEffect.processDiseaseRetention(Either.right(client)));
        IClientXplatAbstractions.INSTANCE.registerItemProperty(EphemeraItemRegistry.SNEAKY_SLATE.get(), ItemSlate.WRITTEN_PRED,
                (stack, level, holder, holderID) -> ItemSlate.hasPattern(stack) ? 1f : 0f);
        /*
        public static void registerBlockEntityRenderers(@NotNull BlockEntityRendererRegisterererer registerer) {
            registerer.registerBlockEntityRenderer(HexBlockEntities.SLATE_TILE, BlockEntitySlateRenderer::new);
            registerer.registerBlockEntityRenderer(HexBlockEntities.AKASHIC_BOOKSHELF_TILE,
            BlockEntityAkashicBookshelfRenderer::new);
        }
        */
    }
}
