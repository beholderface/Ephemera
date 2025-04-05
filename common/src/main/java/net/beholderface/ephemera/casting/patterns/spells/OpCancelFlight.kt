package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.player.FlightAbility
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.ephemera.Ephemera
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d

class OpCancelFlight : ConstMediaAction {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env.castingEntity is ServerPlayerEntity){
            val caster = env.castingEntity as ServerPlayerEntity
            if (IXplatAbstractions.INSTANCE.getFlight(caster) != null){
                IXplatAbstractions.INSTANCE.setFlight(caster, null)
                Ephemera.notifyFlightReset(caster)
                if (!(caster.isCreative || caster.isSpectator)){
                    caster.abilities.allowFlying = false
                    caster.sendAbilitiesUpdate()
                }
            }
        }
        return listOf()
    }
}