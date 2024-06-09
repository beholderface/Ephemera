package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import net.beholderface.ephemera.api.getStatusEffect
import net.beholderface.ephemera.casting.patterns.status.OpGetEntitiesByStatus
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class OpGetEntityByStatus : ConstMediaAction {
    override val argc = 2
    override val mediaCost = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pos = args.getVec3(1, argc)
        val effect = args.getStatusEffect(0, argc, true)
        ctx.assertVecInRange(pos)
        val aabb = Box(pos.add(Vec3d(-0.5, -0.5, -0.5)), pos.add(Vec3d(0.5, 0.5, 0.5)))
        val entitiesGot = ctx.world.getOtherEntities(null, aabb) {
            OpGetEntitiesByStatus.isAffectedAndReachable(it, effect, ctx, false)
        }.sortedBy { it.squaredDistanceTo(pos) }

        val entity = entitiesGot.getOrNull(0)
        return entity.asActionResult
    }
}