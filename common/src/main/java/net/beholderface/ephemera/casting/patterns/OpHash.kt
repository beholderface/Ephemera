package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.ephemera.casting.iotatypes.HashIota

class OpHash : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val iota = args[0]
        return listOf(HashIota.of(iota))
    }
}