package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import net.minecraft.util.Hand

class OpHandDurability(val other : Boolean) : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val hand = if (other){
            ctx.otherHand
        } else {
            ctx.castingHand
        }
        val stack = ctx.caster.getStackInHand(hand)
        return if (stack.isDamageable){
            listOf(DoubleIota((stack.maxDamage - stack.damage).toDouble()))
        } else {
            listOf(NullIota())
        }
    }
}