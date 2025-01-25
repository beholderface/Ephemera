package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.ephemera.api.getStatusEffect
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class OpGetEntityByStatus : ConstMediaAction {
    override val argc = 2
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val pos = args.getVec3(1, argc)
        val effect = args.getStatusEffect(0, argc, true)
        env.assertVecInRange(pos)
        val aabb = Box(pos.add(Vec3d(-0.5, -0.5, -0.5)), pos.add(Vec3d(0.5, 0.5, 0.5)))
        val entitiesGot = env.world.getOtherEntities(null, aabb) {
            OpGetEntitiesByStatus.isAffectedAndReachable(it, effect, env, false)
        }.sortedBy { it.squaredDistanceTo(pos) }

        val entity = entitiesGot.getOrNull(0)
        return entity.asActionResult
    }
}