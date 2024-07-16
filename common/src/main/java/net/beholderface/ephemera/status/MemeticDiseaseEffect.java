package net.beholderface.ephemera.status;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.registry.EphemeraMiscRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MemeticDiseaseEffect extends StatusEffect {
    public MemeticDiseaseEffect() {
        super(StatusEffectCategory.HARMFUL, 0x28005b);
    }
    public static final int SPREAD_DURATION = 20 * 60 * 60; //one hour
    public static final Map<LivingEntity, Pair<Long, Integer>> DISEASE_MAP = new HashMap<>();
    //private static final Pair<Long, Integer> DEFAULT_PAIR = new Pair<>(-1L, -1);
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return true;//duration % 200 == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier){
        World world = entity.world;
        StatusEffectInstance instance = entity.getStatusEffect(this);
        assert instance != null;
        int duration = instance.getDuration();
        if (duration > 5){
            if (DISEASE_MAP.containsKey(entity)){
                Pair<Long, Integer> preexisting = DISEASE_MAP.get(entity); // (pair.getFirst() + pair.getSecond()) - e.world.getTime();
                int properDuration = (int) ((preexisting.getFirst() + preexisting.getSecond()) - entity.world.getTime());
                if (duration < properDuration){
                    instance.upgrade(new StatusEffectInstance(this, properDuration));
                }
            } else {
                DISEASE_MAP.put(entity, new Pair<>(world.getTime(), duration));
            }
        } else {
            DISEASE_MAP.remove(entity);
        }
        if (world.getTime() % 200 == 0){
            Vec3d spreaderCenter = entity.getBoundingBox().getCenter();
            List<Entity> nearbyEntities = world.getOtherEntities(entity, Box.from(spreaderCenter).expand(8.0),
                    (subject)->{
                        if (subject instanceof LivingEntity livingEntity){
                            return !(livingEntity.hasStatusEffect(EphemeraMiscRegistry.BRAINROT_CURE.get())
                                    || livingEntity.hasStatusEffect(EphemeraMiscRegistry.BRAINROT_PREVENTION.get())
                                    || livingEntity.hasStatusEffect(this));
                        }
                        return false;});
            int chance = entity.world.random.nextBetween(1, 10);
            for (Entity e : nearbyEntities){
                if (e.getBoundingBox().getCenter().distanceTo(spreaderCenter) <= 8 && e instanceof LivingEntity livingEntity
                        && chance == 1){
                    //Ephemera.LOGGER.info("Spreading brainrot to " + livingEntity.getEntityName() + " at position " + livingEntity.getPos());
                    livingEntity.addStatusEffect(new StatusEffectInstance(this, SPREAD_DURATION), entity);
                }
                chance = entity.world.random.nextBetween(1, 10);
            }
        }
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity.hasStatusEffect(EphemeraMiscRegistry.BRAINROT_PREVENTION.get())){
            entity.removeStatusEffect(EphemeraMiscRegistry.BRAINROT.get());
            MemeticDiseaseEffect.DISEASE_MAP.remove(entity);
        }
    }

    //no milk for you :P
    public static void processDiseaseRetention(/*boolean client, */Either<MinecraftServer, MinecraftClient> either){
        //String side = client ? "client" : "server";
        if (either.right().isPresent()){
            if (either.right().get().world == null && !DISEASE_MAP.isEmpty()){
                DISEASE_MAP.clear();
            }
        }
        for (LivingEntity e : DISEASE_MAP.keySet()){
            StatusEffectInstance instance = e.getStatusEffect(EphemeraMiscRegistry.BRAINROT.get());
            Pair<Long, Integer> pair = DISEASE_MAP.get(e);
            long properDuration = (pair.getFirst() + pair.getSecond()) - e.world.getTime();
            if (instance != null){
                if (instance.getDuration() + 1 < properDuration){
                    instance.upgrade(new StatusEffectInstance(instance.getEffectType(), (int) (properDuration)));
                    //Ephemera.LOGGER.info("Extending brainrot " + side + "-side. Proper duration: " + properDuration + ", actual duration: " + instance.getDuration());
                }/* else {
                    Ephemera.LOGGER.info(side + " " + properDuration + " " + instance.getDuration());
                }*/
            } else {
                e.addStatusEffect(new StatusEffectInstance(EphemeraMiscRegistry.BRAINROT.get(), (int) (properDuration)));
                //Ephemera.LOGGER.info("Reapplying brainrot " + side + "-side");
            }
        }
    }
}
