package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.util.math.Vec3d

class OpFlightRemaining : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getPlayer(0, argc)
        val ability = IXplatAbstractions.INSTANCE.getFlight(target)
        if (ability != null){
            val duration = ability.timeLeft
            val radius = ability.radius
            //to work more smoothly with OpCancelFlight
            if (duration == 0 && radius == 0.0){
                return listOf(NullIota())
            }
            if (duration == -1){
                val origin = ability.origin
                val equalAltitudeOrigin = Vec3d(origin.x, target.y, origin.z)
                val distanceToEdge = radius - target.pos.distanceTo(equalAltitudeOrigin)
                return listOf(DoubleIota(distanceToEdge))
            } else {
                return listOf(DoubleIota(duration.toDouble() / 20.0))
            }
        }
        return listOf(NullIota())
    }
}