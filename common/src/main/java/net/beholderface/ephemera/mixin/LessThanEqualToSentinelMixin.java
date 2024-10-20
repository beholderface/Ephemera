package net.beholderface.ephemera.mixin;

import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import com.llamalad7.mixinextras.sugar.Local;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CastingContext.class)
public class LessThanEqualToSentinelMixin {
    @Unique
    CastingContext ctx = (CastingContext) (Object) this;
    @Inject(method = "isVecInRange(Lnet/minecraft/util/math/Vec3d;)Z", at = @At(value = "INVOKE_ASSIGN",
            target = "Lat/petrak/hexcasting/xplat/IXplatAbstractions;getSentinel(Lnet/minecraft/entity/player/PlayerEntity;)Lat/petrak/hexcasting/api/player/Sentinel;"
            , ordinal = 0, remap = true), remap = true, cancellable = true)
    public void lessthanequalto(Vec3d vec, CallbackInfoReturnable<Boolean> cir, @Local Sentinel sentinel){
        if (sentinel.hasSentinel() && sentinel.extendsRange() && Intrinsics.areEqual(ctx.getWorld().getRegistryKey(),
                sentinel.dimension()) && vec.squaredDistanceTo(sentinel.position()) <= 256.0) {
            cir.setReturnValue(true);
        }
    }
}
