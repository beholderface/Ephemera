package net.beholderface.ephemera.mixin;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.client.RegisterClientStuff;
import at.petrak.hexcasting.client.be.BlockEntitySlateRenderer;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.blocks.blockentity.ExtraConnectedSlateBlockEntity;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.TitleScreen;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegisterClientStuff.class)
public class EphemeraMixin {
    /*@Inject(at = @At(value = "HEAD", remap = false),
            method = "registerBlockEntityRenderers(Lat/petrak/hexcasting/client/RegisterClientStuff$BlockEntityRendererRegisterererer;)V", remap = false)
    private static void alsoSneakySlate(@NotNull RegisterClientStuff.BlockEntityRendererRegisterererer registerer, CallbackInfo ci) {
        Ephemera.LOGGER.info("Hopefully registering renderer");
        BlockEntityType<ExtraConnectedSlateBlockEntity> sneaky = EphemeraBlockRegistry.SNEAKY_SLATE_ENTITY.get();
        registerer.registerBlockEntityRenderer(sneaky, BlockEntitySlateRenderer::new);
    }*/
}