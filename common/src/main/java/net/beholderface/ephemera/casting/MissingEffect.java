package net.beholderface.ephemera.casting;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class MissingEffect extends StatusEffect {
    public MissingEffect() {
        super(StatusEffectCategory.NEUTRAL, 0);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier){
        entity.removeStatusEffect(this);
    }
}
