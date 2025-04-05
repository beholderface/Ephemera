package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.xplat.IXplatAbstractions

class OpFlightType : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getPlayer(0, argc)
        val ability = IXplatAbstractions.INSTANCE.getFlight(target)
        if (ability != null){
            //to work more smoothly with OpCancelFlight
            if (ability.timeLeft == 0 && ability.radius == 0.0){
                return listOf(NullIota())
            }
            val isAnchorite = ability.radius != -1.0
            return listOf(BooleanIota(isAnchorite))
        }
        return listOf(NullIota())
    }

}