package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.ephemera.api.toVec3i
import net.beholderface.ephemera.casting.mishaps.MishapNo
import net.minecraft.util.math.BlockPos

class OpNo : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getVec3(0, argc)
        env.assertVecInRange(target)
        throw MishapNo(BlockPos(target.toVec3i()))
    }
}