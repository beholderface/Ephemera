package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.ephemera.api.containsPermissive
import net.beholderface.ephemera.api.toVec3i
import net.minecraft.util.math.BlockPos

class OpCollisionProbe : ConstMediaAction {
    override val mediaCost = 0L
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val targetPoint = args.getVec3(0, argc)
        val targetPointI = targetPoint.toVec3i()
        val targetPos = BlockPos(targetPointI)
        val offsetPoint = targetPoint.subtract(targetPointI.x.toDouble(), targetPointI.y.toDouble(), targetPointI.z.toDouble())
        val targetState = env.world.getBlockState(targetPos)
        val outlineBoxes = targetState.getOutlineShape(env.world, targetPos).boundingBoxes
        for (box in outlineBoxes){
            if (box.containsPermissive(offsetPoint)){
                return true.asActionResult
            }
        }
        val collisionBoxes = targetState.getCollisionShape(env.world, targetPos).boundingBoxes
        for (box in collisionBoxes){
            if (box.containsPermissive(offsetPoint)){
                return true.asActionResult
            }
        }
        return false.asActionResult
    }
}