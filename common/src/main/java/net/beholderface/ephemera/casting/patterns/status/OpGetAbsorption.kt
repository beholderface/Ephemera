package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota


class OpGetAbsorption : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        env.assertEntityInRange(target)
        return listOf(DoubleIota(target.absorptionAmount.toDouble()))
    }
}