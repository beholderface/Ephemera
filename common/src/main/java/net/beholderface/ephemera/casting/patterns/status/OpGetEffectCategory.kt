package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import net.beholderface.ephemera.api.getStatusEffect
import net.minecraft.entity.effect.StatusEffectCategory

class OpGetEffectCategory : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val effect = args.getStatusEffect(0, argc, true)
        val output = when(effect.category){
            StatusEffectCategory.BENEFICIAL -> DoubleIota(1.0)
            StatusEffectCategory.HARMFUL -> DoubleIota(-1.0)
            StatusEffectCategory.NEUTRAL -> DoubleIota(0.0)
            null -> NullIota()
        }
        return listOf(output)
    }
}