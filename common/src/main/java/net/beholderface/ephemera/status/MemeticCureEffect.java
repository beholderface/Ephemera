package net.beholderface.ephemera.status;

import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.registry.EphemeraMiscRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MemeticCureEffect extends StatusEffect {
    public MemeticCureEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xaedafe);
    }
    public static final int SPREAD_DURATION = 20 * 60 * 10; //ten minutes
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return duration % 200 == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier){
        if (entity.hasStatusEffect(EphemeraMiscRegistry.BRAINROT.get())){
            entity.removeStatusEffect(EphemeraMiscRegistry.BRAINROT.get());
            MemeticDiseaseEffect.DISEASE_MAP.remove(entity);
        }
        World world = entity.world;
        Vec3d spreaderCenter = entity.getBoundingBox().getCenter();
        List<Entity> nearbyEntities = world.getOtherEntities(entity, Box.from(spreaderCenter).expand(8.0),
                (subject)->{
            if (subject instanceof LivingEntity livingEntity){
                return livingEntity.hasStatusEffect(EphemeraMiscRegistry.BRAINROT.get());
            }
            return false;});
        int chance = entity.world.random.nextBetween(1, 10);
        for (Entity e : nearbyEntities){
            if (e.getBoundingBox().getCenter().distanceTo(spreaderCenter) <= 8 && e instanceof LivingEntity livingEntity
                    && chance == 1){
                //Ephemera.LOGGER.info("Curing brainrot for " + livingEntity.getEntityName() + " at position " + livingEntity.getPos());
                livingEntity.addStatusEffect(new StatusEffectInstance(this, SPREAD_DURATION), entity);
                livingEntity.removeStatusEffect(EphemeraMiscRegistry.BRAINROT.get());
                MemeticDiseaseEffect.DISEASE_MAP.remove(e);
            }
            chance = entity.world.random.nextBetween(1, 10);
        }
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity.hasStatusEffect(EphemeraMiscRegistry.BRAINROT.get())){
            entity.removeStatusEffect(EphemeraMiscRegistry.BRAINROT.get());
            MemeticDiseaseEffect.DISEASE_MAP.remove(entity);
        }
    }
}
