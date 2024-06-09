package net.beholderface.ephemera.casting.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapMissingEffect(val entity: LivingEntity, val effect: StatusEffect) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.BLUE)

    override fun particleSpray(ctx: CastingContext) =
        ParticleSpray.burst(entity.pos, 1.0)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text =
        error("ephemera:missingeffect", entity.name, Text.translatable(effect.translationKey))

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.caster.addStatusEffect(StatusEffectInstance(StatusEffects.NAUSEA, 30 * 20))
    }

    companion object {
        @JvmStatic
        fun of(entity: LivingEntity, effect: StatusEffect): MishapMissingEffect {
            return MishapMissingEffect(entity, effect)
        }
    }

}