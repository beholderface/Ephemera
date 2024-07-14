package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.ephemera.casting.iotatypes.HashIota

class OpHash : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val iota = args[0]
        return listOf(HashIota.of(iota))
    }
}