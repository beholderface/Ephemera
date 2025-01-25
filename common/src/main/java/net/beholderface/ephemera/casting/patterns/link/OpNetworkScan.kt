package net.beholderface.ephemera.casting.patterns.link

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.beholderface.ephemera.api.getConnected
import ram.talia.hexal.api.linkable.LinkableRegistry

class OpNetworkScan : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 8
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val initialTarget = LinkableRegistry.linkableFromIota(args[0], env.world) ?: throw MishapInvalidIota.ofType(args[0], 0, "linkable")
        env.assertVecInRange(initialTarget.getPosition())
        val connections = initialTarget.getConnected(args.getPositiveInt(1, argc).coerceAtMost(32))
        val iotas : MutableList<Iota> = mutableListOf()
        for (linkable in connections.iterator()){
            iotas.add(linkable.asActionResult[0])
        }
        return listOf(ListIota(iotas))
    }
}