package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.ephemera.api.getStatusEffect
import net.beholderface.ephemera.casting.mishaps.MishapMissingEffect
import net.minecraft.entity.effect.StatusEffects
import kotlin.math.floor

class OpGetStatusDetail(val type : Boolean) : ConstMediaAction {
    override val argc = 2
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        val effect = args.getStatusEffect(1, argc, true)
        val existingEffect = target.getStatusEffect(effect)
        if (existingEffect == null){
            if (effect == StatusEffects.ABSORPTION){
                if (!(target.absorptionAmount > 0)){
                    throw MishapMissingEffect(target, effect)
                } else {
                    val effectDuration = -1.0
                    val effectStrenth = floor(target.absorptionAmount / 4)
                    return if (!type) {
                        listOf(DoubleIota(effectDuration))
                    } else {
                        listOf(DoubleIota(effectStrenth.toDouble()))
                    }
                }
            } else {
                throw MishapMissingEffect(target, effect)
            }
        }
        val effectDuration = existingEffect.duration.toDouble() / 20
        val effectStrenth = (existingEffect.amplifier + 1).toDouble()
        return if (!type) {
            listOf(DoubleIota(effectDuration))
        } else {
            listOf(DoubleIota(effectStrenth))
        }
    }
}