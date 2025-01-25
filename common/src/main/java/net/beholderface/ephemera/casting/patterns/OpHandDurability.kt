package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster

class OpHandDurability(val other : Boolean) : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val hand = if (other){
            env.otherHand
        } else {
            env.castingHand
        }
        if (env.castingEntity == null){
            throw MishapBadCaster()
        }
        val stack = env.castingEntity!!.getStackInHand(hand)
        return if (stack.isDamageable){
            listOf(DoubleIota((stack.maxDamage - stack.damage).toDouble()))
        } else {
            listOf(NullIota())
        }
    }
}