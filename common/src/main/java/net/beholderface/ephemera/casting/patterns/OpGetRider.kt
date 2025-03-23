package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import net.minecraft.entity.Entity
import ram.talia.hexal.api.casting.eval.env.WispCastEnv

class OpGetRider : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val mount : Entity = args.getEntity(0, argc)
        env.assertEntityInRange(mount)
        val passengers = mount.passengerList
        val iotaList : MutableList<Iota> = mutableListOf()
        for (entity in passengers){
            iotaList.add(EntityIota(entity))
        }
        return listOf(ListIota(iotaList))
    }

}