package net.beholderface.ephemera.casting.patterns.link

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapLocationInWrongDimension
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import net.beholderface.ephemera.api.getConnected
import net.beholderface.ephemera.api.toVec3i
import net.beholderface.ephemera.blocks.RelayTPDetectorBlock
import net.beholderface.ephemera.registry.EphemeraBlockRegistry
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import ram.talia.hexal.api.casting.eval.env.WispCastEnv
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.api.linkable.LinkableRegistry
import ram.talia.hexal.api.linkable.LinkableTypes
import ram.talia.hexal.common.entities.BaseCastingWisp
import ram.talia.hexal.common.entities.BaseWisp
import java.util.*

class OpNetworkTeleport : SpellAction {
    override val argc = 3

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getEntity(0, argc)
        env.assertEntityInRange(target)
        val inputNode = LinkableRegistry.linkableFromIota(args[1], env.world)
            ?: throw MishapInvalidIota.ofType(args[0], 0, "linkable")
        env.assertVecInRange(inputNode.getPosition())
        if (target.pos.distanceTo(inputNode.getPosition()) >= 8){
            throw MishapBadLocation(target.pos, "ephemera:inputrelaytprange")
        }
        if (!target.canUsePortals() || target.type.isIn(HexTags.Entities.CANNOT_TELEPORT))
            throw MishapImmuneEntity(target)
        val worldKey = env.world.registryKey
        if (target is LivingEntity /*teleporting nonliving stuff is probably fine*/ && !HexConfig.server().canTeleportInThisDimension(worldKey)){
            throw MishapLocationInWrongDimension(worldKey.value)
        }
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
            throw MishapBadLocation(destination, "ephemera:outputrelaytprange")
        }
        val mCast = env as? WispCastEnv
        val isWisp = mCast != null
        val castingEntity = if (isWisp){
            mCast!!.wisp
        } else {
            env.castingEntity
        }
        val cost = calculateCost(target, castingEntity!!, env)
        return SpellAction.Result(
            Spell(target, destination, foundOutputNode.get(), inputNode), cost, listOf(
            ParticleSpray.burst(target.pos, 2.0, 16), ParticleSpray.burst(destination, 2.0, 16)
        ))
    }
    private data class Spell(val target : Entity, val destination : Vec3d, val destNode : ILinkable, val sourceNode : ILinkable) :
        RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            target.teleport(destination.x, destination.y, destination.z)
            if (destNode.getLinkableType() == LinkableTypes.RELAY_TYPE){
                val state = env.world.getBlockState(BlockPos(destNode.getPosition().toVec3i()))
                /*val facing = state.get(Properties.FACING).vector
                val posToCheck = BlockPos(destNode.getPosition().add(facing.x.toDouble(), facing.y.toDouble(), facing.z.toDouble()))
                val state2 = ctx.world.getBlockState(posToCheck)
                if (state2.block == EphemeraBlockRegistry.TP_DETECTOR.get()){
                    (state2.block as RelayTPDetectorBlock).notifyTeleport(state, ctx.world, posToCheck)
                }*/
                for (dir in Direction.values()){
                    val checkedPos = destNode.getPosition().add(dir.vector.x.toDouble(), dir.vector.y.toDouble(), dir.vector.z.toDouble()).toVec3i()
                    val state2 = env.world.getBlockState(BlockPos(checkedPos))
                    val block = state2.block
                    if (block == EphemeraBlockRegistry.TP_DETECTOR.get()){
                        (block as RelayTPDetectorBlock).notifyTeleport(state2, env.world, BlockPos(checkedPos))
                    }
                }
                for (dir in Direction.values()){
                    val checkedPos = sourceNode.getPosition().add(dir.vector.x.toDouble(), dir.vector.y.toDouble(), dir.vector.z.toDouble()).toVec3i()
                    val state2 = env.world.getBlockState(BlockPos(checkedPos))
                    val block = state2.block
                    if (block == EphemeraBlockRegistry.TP_DETECTOR.get()){
                        (block as RelayTPDetectorBlock).notifyTeleport(state2, env.world, BlockPos(checkedPos))
                    }
                }
            }
        }

    }

    private fun calculateCost(target: Entity, castingEntity: Entity, ctx: CastingEnvironment) : Long{
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
            if (fightsBack){
                return MediaConstants.SHARD_UNIT * 3
            }
        }
        if (!target.isLiving || target is ArmorStandEntity){
            return MediaConstants.DUST_UNIT
        }
        return MediaConstants.SHARD_UNIT * 3
    }
}