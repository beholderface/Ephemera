package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import net.beholderface.ephemera.api.getHash
import net.beholderface.ephemera.api.getStatusEffect
import java.nio.ByteBuffer
import java.util.*

class OpHashBits : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val hashString = args.getHash(0, argc)
        val bytes = hashString.toByteArray()
        val ints : MutableList<DoubleIota> = mutableListOf()
        var index = 0
        for (byte in bytes){
            if (index % 4 == 0 && index < bytes.size - 3){
                ints.add(DoubleIota(ByteBuffer.wrap(byteArrayOf(bytes[index], bytes[index+1], bytes[index+2], bytes[index+3])).getInt().toDouble()))
            } else if (index == 60){
                ints.add(DoubleIota(ByteBuffer.wrap(byteArrayOf(0, 0, bytes[index], bytes[index+1])).getInt().toDouble()))
            }
            index++
        }
        return listOf(ListIota(ints.toList()))
    }
}