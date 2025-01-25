package net.beholderface.ephemera.casting.patterns.link

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.beholderface.ephemera.api.getConnected
import net.beholderface.ephemera.api.toVec3i
import net.beholderface.ephemera.blocks.blockentity.RelayIndexBlockEntity
import net.beholderface.ephemera.registry.EphemeraBlockRegistry
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import ram.talia.hexal.api.linkable.LinkableRegistry

class OpNodeIndex() : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val sourceNode = LinkableRegistry.linkableFromIota(args[0], env.world)
            ?: throw MishapInvalidIota.ofType(args[0], 0, "linkable")
        env.assertVecInRange(sourceNode.getPosition())
        val soughtKey = args[1]
        val connectedNodes = sourceNode.getConnected(32)
        for (node in connectedNodes){
            for (dir in Direction.values()){
                val checkedPos = node.getPosition().add(dir.vector.x.toDouble(), dir.vector.y.toDouble(), dir.vector.z.toDouble())
                val state2 = env.world.getBlockState(BlockPos(checkedPos.toVec3i()))
                val block = state2.block
                if (block == EphemeraBlockRegistry.RELAY_INDEX.get()){
                    val be = env.world.getBlockEntity(BlockPos(checkedPos.toVec3i())) as RelayIndexBlockEntity
                    val iota = be.storedIota
                    //Ephemera.LOGGER.info("Found node index, with iota $iota")
                    if (IotaType.serialize(iota).equals(IotaType.serialize(soughtKey))){
                        return node.asActionResult
                    }
                }
            }
        }
        return listOf(NullIota())
    }
}