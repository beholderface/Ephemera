package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.ephemera.api.getStatusEffect
import net.beholderface.ephemera.casting.mishaps.MishapMissingEffect

class OpGetStatusDetail(val type : Boolean) : ConstMediaAction {
    override val argc = 2
    override val mediaCost = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        val effect = args.getStatusEffect(1, argc, true)
        val existingEffect =  target.getStatusEffect(effect)
        if (existingEffect == null){
            throw MishapMissingEffect(target, effect)
        }
        val effectDuration = existingEffect.duration.toDouble() / 20
        val effectStrenth = (existingEffect.amplifier + 1).toDouble()
        return if (!type) {
            listOf(DoubleIota(effectDuration))
        } else {
            listOf(DoubleIota(effectStrenth))
        }
    }
}