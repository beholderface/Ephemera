package net.beholderface.ephemera.fabric.mixin;

import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.fabric.xplat.FabricXplatImpl;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.ephemera.SharedMixinData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FabricXplatImpl.class)
public class BreakTierMixinCheck {
    @WrapOperation(method = "isCorrectTierForDrops(Lnet/minecraft/item/ToolMaterial;Lnet/minecraft/block/BlockState;)Z",
    at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/mod/HexConfig$ServerConfigAccess;opBreakHarvestLevelBecauseForgeThoughtItWasAGoodIdeaToImplementHarvestTiersUsingAnHonestToGodTopoSort()I",
    remap = false), remap = true)
    public int possiblyUpgradeTier(HexConfig.ServerConfigAccess instance, Operation<Integer> original){
        return (int)Math.min(Math.max(original.call(instance) + (int)Math.floor(SharedMixinData.breakBoost), 0), 4);
    }

}
