package net.beholderface.ephemera.mixin;

import net.beholderface.ephemera.items.ConjuredArmorItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArmorItem.class)
public class NoDispenserAllowedMixin {
    @Inject(method = "dispenseArmor(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Z",
            at = @At(value = "HEAD", remap = true), remap = true, cancellable = true)
    private static void noDispenserAllowed(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> cir){
        if (armor.getItem() instanceof ConjuredArmorItem){
            armor.setCount(0);
        }
        cir.setReturnValue(false);
    }
}
