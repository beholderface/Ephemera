package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import ram.talia.hexal.api.getBaseWisp
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.common.entities.BaseCastingWisp
import ram.talia.hexal.common.entities.TickingWisp
import ram.talia.hexal.common.entities.WanderingWisp

class OpRideWisp : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.SHARD_UNIT
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity ?: throw MishapBadCaster()
        val wisp = args.getBaseWisp(0, argc)
        env.assertEntityInRange(wisp)
        val wispRange : Double = if (wisp is BaseCastingWisp){
            wisp.maxSqrCastingDistance()
        } else {
            16.0
        }
        if ((wisp as Entity).pos.squaredDistanceTo(caster.pos) > wispRange){
            throw MishapBadLocation(env.castingEntity!!.pos)
        }
        if (wisp.owner() == caster.uuid || wisp is WanderingWisp) {
            caster.startRiding(wisp)
        } else {
            throw MishapImmuneEntity(wisp)
        }
        return listOf()
    }
}