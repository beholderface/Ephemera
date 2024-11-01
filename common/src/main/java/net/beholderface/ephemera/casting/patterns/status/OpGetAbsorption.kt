package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota

class OpGetAbsorption : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        ctx.assertEntityInRange(target)
        return listOf(DoubleIota(target.absorptionAmount.toDouble()))
    }
}