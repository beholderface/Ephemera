package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.ephemera.api.getStatusEffect
import net.beholderface.ephemera.casting.mishaps.MishapMissingEffect
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import kotlin.math.pow

class OpRemoveStatus : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        val effect = args.getStatusEffect(1, argc, true)
        var existingEffect = target.getStatusEffect(effect)
        if (existingEffect == null){
            existingEffect = StatusEffectInstance(StatusEffects.ABSORPTION, 60, 3)
            if (effect != StatusEffects.ABSORPTION){
                throw MishapMissingEffect(target, effect)
            }
        }
        val effectDuration = existingEffect.duration.toDouble() / 20
        val effectStrenth = (existingEffect.amplifier + 1).toDouble()
        var costExponent = when(effect.category){
            StatusEffectCategory.BENEFICIAL -> 1.1
            StatusEffectCategory.NEUTRAL -> 1.5
            StatusEffectCategory.HARMFUL -> 2.0
            null -> 1.0
        }
        if (costExponent.equals(1.0) && !(target.equals(ctx.caster))){
            costExponent = 2.0
        }
        var cost = ((effectStrenth.pow(costExponent.coerceAtMost(5.0)) * effectDuration.coerceAtMost((20 * 60 * 10 /*ten minutes*/).toDouble())) * MediaConstants.DUST_UNIT).toInt()
        if (costExponent == 1.1){
            cost /= 10
        }
        //ctx.caster.sendMessage(Text.of((cost.toDouble() / MediaConstants.DUST_UNIT).toString() + " dust"))
        return Triple(
            Spell(target, effect),
            cost,
            listOf(ParticleSpray.cloud(ctx.caster.pos, 2.0))
        )
    }
    private data class Spell(val target : LivingEntity, val effect : StatusEffect) : RenderedSpell {
        override fun cast(ctx: CastingContext){
            if (target.hasStatusEffect(effect)){
                target.removeStatusEffect(effect)
            }
            if (effect == StatusEffects.ABSORPTION){
                target.absorptionAmount = 0f
            }
        }
    }
}