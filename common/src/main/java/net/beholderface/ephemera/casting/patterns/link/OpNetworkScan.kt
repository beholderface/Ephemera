package net.beholderface.ephemera.casting.patterns.link

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.beholderface.ephemera.api.getConnected
import ram.talia.hexal.api.linkable.LinkableRegistry

class OpNetworkScan : ConstMediaAction{
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 4
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val initialTarget = LinkableRegistry.linkableFromIota(args[0], ctx.world) ?: throw MishapInvalidIota.ofType(args[0], 0, "linkable")
        val connections = initialTarget.getConnected(args.getInt(1, argc).coerceIn(0, 32))
        val iotas : MutableList<Iota> = mutableListOf()
        for (linkable in connections.iterator()){
            iotas.add(linkable.asActionResult[0])
        }
        return listOf(ListIota(iotas))
    }
}