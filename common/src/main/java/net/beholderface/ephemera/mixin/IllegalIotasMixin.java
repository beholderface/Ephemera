package net.beholderface.ephemera.mixin;

import at.petrak.hexcasting.api.spell.iota.GarbageIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.beholderface.ephemera.casting.iotatypes.PotionIota;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "ram.talia.hexal.api.spell.mishaps.MishapIllegalInterworldIota$Companion")
public class IllegalIotasMixin {
    @Inject(method = "replaceInNestedIota", at = @At(value = "HEAD", remap = false), remap = false, cancellable = true)
    public void illegal(Iota iota, CallbackInfoReturnable<Iota> cir){
        if (iota instanceof PotionIota){
            cir.setReturnValue(new GarbageIota());
        }
    }
}
