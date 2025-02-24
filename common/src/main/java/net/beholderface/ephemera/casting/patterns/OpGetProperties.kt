package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import ram.talia.moreiotas.api.casting.iota.StringIota

class OpGetProperties : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getBlockPos(0, argc)
        val state = env.world.getBlockState(target)
        val propNames : MutableList<StringIota> = mutableListOf()
        for (property in state.properties){
            propNames.add(StringIota.make(property.name))
        }
        return listOf(ListIota(propNames.toList()))
    }

}