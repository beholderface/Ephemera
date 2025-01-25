package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.beholderface.ephemera.casting.patterns.spells.RevealHistoryManager

class OpGetTransmitCost : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val target = args.getPlayer(0, argc)
        val cost = RevealHistoryManager.calculateCost(target.uuid, ctx.world.time)
        return listOf(DoubleIota(cost.toDouble() / MediaConstants.DUST_UNIT.toDouble()))
    }
}