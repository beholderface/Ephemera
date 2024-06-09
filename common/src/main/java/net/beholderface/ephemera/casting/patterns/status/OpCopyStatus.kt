package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.ephemera.api.getStatusEffect
import net.beholderface.ephemera.casting.mishaps.MishapMissingEffect
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffectInstance
import kotlin.math.pow



//this code was never implemented
//feel free to add your own pattern to use it, it *should* work just fine (though I haven't tested it)

class OpCopyStatus : SpellAction {
    override val argc = 3
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val origin = args.getLivingEntityButNotArmorStand(0, argc)
        val target = args.getLivingEntityButNotArmorStand(1, argc)
        val effect = args.getStatusEffect(2, argc, true)
        ctx.assertEntityInRange(origin)
        ctx.assertEntityInRange(target)
        val existingEffect =  origin.getStatusEffect(effect)
        if (existingEffect == null){
            throw MishapMissingEffect(origin, effect)
        }
        val effectDuration = existingEffect.duration.toDouble() / 20
        val effectStrenth = (existingEffect.amplifier + 1).toDouble()
        val costExponent = when(effect.category){
            StatusEffectCategory.BENEFICIAL -> 2.0
            StatusEffectCategory.NEUTRAL -> 1.5
            StatusEffectCategory.HARMFUL -> 2.0
            null -> 1.0
        }
        val cost = ((effectStrenth.pow(costExponent) * effectDuration) * MediaConstants.DUST_UNIT).toInt()
        return Triple(
            Spell(origin, target, existingEffect),
            cost,
            listOf(ParticleSpray.cloud(origin.pos, 2.0), ParticleSpray.cloud(target.pos, 2.0))
        )
    }

    private data class Spell(val source : LivingEntity, val target : LivingEntity, val effect: StatusEffectInstance) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            target.addStatusEffect(effect, ctx.caster)
        }
    }
}