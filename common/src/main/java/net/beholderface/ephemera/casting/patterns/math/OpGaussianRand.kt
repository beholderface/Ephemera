package net.beholderface.ephemera.casting.patterns.math

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota


class OpGaussianRand() : ConstMediaAction {
    //the math pattern package from the template is no longer lonely
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        return listOf(DoubleIota(env.world.random.nextGaussian()))
    }
}