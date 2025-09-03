package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.beholderface.ephemera.api.getStatusEffect
import net.beholderface.ephemera.casting.mishaps.MishapMissingEffect
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import kotlin.math.floor
import kotlin.math.pow

class OpRemoveStatus : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        val effect = args.getStatusEffect(1, argc, true)
        var existingEffect = target.getStatusEffect(effect)
        if (existingEffect == null){
            existingEffect = StatusEffectInstance(StatusEffects.ABSORPTION, 60, floor(target.absorptionAmount / 4).toInt())
            if (!(effect == StatusEffects.ABSORPTION && target.absorptionAmount > 0)){
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
        if (costExponent.equals(1.0) && !(target.equals(env.caster))){
            costExponent = 2.0
        }
        var cost = ((effectStrenth.coerceAtMost(5.0).pow(costExponent) * effectDuration.coerceAtMost((20 * 60 * 10 /*ten minutes*/).toDouble())) * MediaConstants.DUST_UNIT).toLong()
        if (costExponent == 1.1){
            cost /= 10
        }
        var infinite = false
        if (existingEffect.isInfinite){
            cost = 0
            infinite = true
        }
        //ctx.caster.sendMessage(Text.of((cost.toDouble() / MediaConstants.DUST_UNIT).toString() + " dust"))
        return SpellAction.Result(
            Spell(target, effect, infinite),
            cost,
            listOf(ParticleSpray.cloud(env.mishapSprayPos(), 2.0))
        )
    }
    private data class Spell(val target : LivingEntity, val effect : StatusEffect, val infinite : Boolean) : RenderedSpell {
        override fun cast(env: CastingEnvironment){
            if (infinite){
                return
            }
            if (target.hasStatusEffect(effect)){
                target.removeStatusEffect(effect)
            }
            if (effect == StatusEffects.ABSORPTION){
                target.absorptionAmount = 0f
            }
        }
    }
}