package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants

class OpClearTransmitHistory : ConstMediaAction {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, environment: CastingEnvironment): List<Iota> {
        RevealHistoryManager.removeEntry(environment.castingEntity?.uuid)
        return listOf()
    }
}