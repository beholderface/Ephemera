package net.beholderface.ephemera.casting.patterns.link

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway
import net.beholderface.ephemera.api.getConnected
import net.beholderface.ephemera.blocks.RelayTPDetectorBlock
import net.beholderface.ephemera.registry.EphemeraBlockRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.api.linkable.LinkableRegistry
import ram.talia.hexal.api.linkable.LinkableTypes
import ram.talia.hexal.api.spell.casting.IMixinCastingContext
import ram.talia.hexal.common.entities.BaseCastingWisp
import ram.talia.hexal.common.entities.BaseWisp
import java.util.*

class OpNetworkTeleport : SpellAction {
    override val argc = 3

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val target = args.getEntity(0, argc)
        ctx.assertEntityInRange(target)
        val inputNode = LinkableRegistry.linkableFromIota(args[1], ctx.world)
            ?: throw MishapInvalidIota.ofType(args[0], 0, "linkable")
        ctx.assertVecInRange(inputNode.getPosition())
        if (target.pos.distanceTo(inputNode.getPosition()) >= 8){
            throw MishapLocationTooFarAway(target.pos, "ephemera:inputrelaytprange")
        }
        if (!target.canUsePortals() || target.type.isIn(HexTags.Entities.CANNOT_TELEPORT))
            throw MishapImmuneEntity(target)
        val destination = args.getVec3(2, argc)
        val connectedNodes = inputNode.getConnected(32)
        var foundOutputNode = Optional.empty<ILinkable>()
        var proximity = 64.0
        for (node in connectedNodes){
            val toDestination = node.getPosition().distanceTo(destination)
            if (toDestination <= 4 && toDestination < proximity){
                foundOutputNode = Optional.of(node)
                proximity = toDestination
            }
        }
        if (!foundOutputNode.isPresent){
            throw MishapLocationTooFarAway(destination, "ephemera:outputrelaytprange")
        }
        val mCast = ctx as? IMixinCastingContext
        val isWisp = !(mCast == null || !mCast.hasWisp())
        val castingEntity = if (isWisp){
            mCast!!.wisp
        } else {
            ctx.caster
        }
        val cost = calculateCost(target, castingEntity!!, ctx)
        return Triple(
            Spell(target, destination, foundOutputNode.get(), inputNode), cost, listOf(
            ParticleSpray.burst(target.pos, 2.0, 16), ParticleSpray.burst(destination, 2.0, 16)
        ))
    }
    private data class Spell(val target : Entity, val destination : Vec3d, val destNode : ILinkable, val sourceNode : ILinkable) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            target.teleport(destination.x, destination.y, destination.z)
            if (destNode.getLinkableType() == LinkableTypes.RELAY_TYPE){
                val state = ctx.world.getBlockState(BlockPos(destNode.getPosition()))
                /*val facing = state.get(Properties.FACING).vector
                val posToCheck = BlockPos(destNode.getPosition().add(facing.x.toDouble(), facing.y.toDouble(), facing.z.toDouble()))
                val state2 = ctx.world.getBlockState(posToCheck)
                if (state2.block == EphemeraBlockRegistry.TP_DETECTOR.get()){
                    (state2.block as RelayTPDetectorBlock).notifyTeleport(state, ctx.world, posToCheck)
                }*/
                for (dir in Direction.values()){
                    val checkedPos = destNode.getPosition().add(dir.vector.x.toDouble(), dir.vector.y.toDouble(), dir.vector.z.toDouble())
                    val state2 = ctx.world.getBlockState(BlockPos(checkedPos))
                    val block = state2.block
                    if (block == EphemeraBlockRegistry.TP_DETECTOR.get()){
                        (block as RelayTPDetectorBlock).notifyTeleport(state2, ctx.world, BlockPos(checkedPos))
                    }
                }
                for (dir in Direction.values()){
                    val checkedPos = sourceNode.getPosition().add(dir.vector.x.toDouble(), dir.vector.y.toDouble(), dir.vector.z.toDouble())
                    val state2 = ctx.world.getBlockState(BlockPos(checkedPos))
                    val block = state2.block
                    if (block == EphemeraBlockRegistry.TP_DETECTOR.get()){
                        (block as RelayTPDetectorBlock).notifyTeleport(state2, ctx.world, BlockPos(checkedPos))
                    }
                }
            }
        }

    }

    private fun calculateCost(target: Entity, castingEntity: Entity, ctx: CastingContext) : Int{
        if (target == castingEntity){
            return if (target is BaseCastingWisp){
                //wisps can send themselves through networks for cheap
                MediaConstants.DUST_UNIT / 8
            } else {
                MediaConstants.SHARD_UNIT
            }
        }
        if (target is BaseWisp){
            val fightsBack = if (target is BaseCastingWisp){
                if (castingEntity is ServerPlayerEntity){
                    target.caster == castingEntity
                } else if (castingEntity is BaseCastingWisp) {
                    target.caster == castingEntity.caster
                } else {
                    false
                }
            } else {
                false
            }
            return if (fightsBack){
                MediaConstants.SHARD_UNIT * 3
            } else {
                MediaConstants.DUST_UNIT
            }
        }
        if (target is ItemEntity){
            return MediaConstants.DUST_UNIT
        }
        return MediaConstants.SHARD_UNIT * 3
    }
}