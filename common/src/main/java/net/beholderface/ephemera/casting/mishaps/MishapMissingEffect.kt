package net.beholderface.ephemera.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.nbt.NbtHelper
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import java.util.*

class MishapMissingEffect(val entity: LivingEntity, val effect: StatusEffect) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.BLUE)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(entity.pos, 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return error("ephemera:missingeffect", entity.name, Text.translatable(effect.translationKey))
    }

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        entity.addStatusEffect(StatusEffectInstance(StatusEffects.NAUSEA, 30 * 20))
    }

    companion object {
        @JvmStatic
        fun of(entity: LivingEntity, effect: StatusEffect): MishapMissingEffect {
            return MishapMissingEffect(entity, effect)
        }
    }

}