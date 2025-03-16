package net.beholderface.ephemera.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.beholderface.ephemera.SharedMixinData;
import net.beholderface.ephemera.registry.EphemeraAttributes;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.OpBreakBlock$Spell")
public class BreakTierMixinCast {
    @Unique private static final ToolMaterial[] ephemera$vanillaMaterials = {
            ToolMaterials.WOOD, ToolMaterials.STONE, ToolMaterials.IRON, ToolMaterials.DIAMOND, ToolMaterials.NETHERITE};


    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At(value = "HEAD", remap = false), remap = false)
    public void setBreakBoost(CastingEnvironment env, CallbackInfo ci){
        if (env.getCastingEntity() instanceof ServerPlayerEntity player){
            SharedMixinData.breakBoost = player.getAttributeValue(EphemeraAttributes.BREAK_TIER_BOOST);
        }
    }

    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At(value = "TAIL", remap = false), remap = false)
    public void resetBreakBoost(CastingEnvironment env, CallbackInfo ci){
        SharedMixinData.breakBoost = 0.0;
    }
}
