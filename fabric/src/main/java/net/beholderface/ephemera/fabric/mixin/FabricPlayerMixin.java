package net.beholderface.ephemera.fabric.mixin;


import net.beholderface.ephemera.registry.EphemeraAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class FabricPlayerMixin extends LivingEntity {
    protected FabricPlayerMixin(EntityType<? extends LivingEntity> entityType, World level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "createPlayerAttributes")
    private static void hex$addAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        var out = cir.getReturnValue();
        out.add(EphemeraAttributes.BREAK_TIER_BOOST, 0.0);
    }
}
