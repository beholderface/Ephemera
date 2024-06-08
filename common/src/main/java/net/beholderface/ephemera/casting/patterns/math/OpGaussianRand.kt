package net.beholderface.ephemera.casting.patterns.math

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota

class OpGaussianRand() : ConstMediaAction {
    //the math pattern package from the template is no longer lonely
    override val argc = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        return listOf(DoubleIota(ctx.world.random.nextGaussian()))
    }
}