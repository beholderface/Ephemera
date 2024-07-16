package net.beholderface.ephemera.status;

import net.beholderface.ephemera.registry.EphemeraMiscRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MemeticPreventionEffect extends StatusEffect {
    public MemeticPreventionEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x78acd6);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return false;
    }
}
