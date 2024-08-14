package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPlayer
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.ephemera.casting.patterns.spells.RevealHistoryManager

class OpGetTransmitCost : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val target = args.getPlayer(0, argc)
        val cost = RevealHistoryManager.calculateCost(target.uuid, ctx.world.time)
        return listOf(DoubleIota(cost.toDouble() / MediaConstants.DUST_UNIT.toDouble()))
    }
}