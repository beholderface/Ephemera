package net.beholderface.ephemera.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.EphemeraMixinUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(World.class)
public class EntityTickCancelMixin {
    @WrapMethod(method = "tickEntity")
    public void cancelTick(Consumer<Entity> tickConsumer, Entity entity, Operation<Void> original){
        if (EphemeraMixinUtils.getFreezeTicks(entity) < 0){
            original.call(tickConsumer, entity);
            return;
        }
        if (entity instanceof LivingEntity livingEntity && livingEntity.isDead()){
            EphemeraMixinUtils.unfreeze(entity);
            original.call(tickConsumer, entity);
        } else if (!EphemeraMixinUtils.checkCancelTick(entity, entity.getWorld().isClient)){
            original.call(tickConsumer, entity);
        }/* else {
            Ephemera.LOGGER.info("{}", entity.getWorld().isClient);
        }*/
    }
}
