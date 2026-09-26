package net.beholderface.ephemera.casting.patterns.spells.great

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.ephemera.Ephemera
import net.beholderface.ephemera.EphemeraMixinUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import ram.talia.hexal.api.getStrictlyPositiveLong

class OpFreezeEntity() : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getEntity(0, argc)
        val duration = args.getStrictlyPositiveLong(1, argc)
        return SpellAction.Result(Spell(target, duration * 20), duration * 10000, listOf(ParticleSpray.burst(target.pos, 2.0, 16)))
    }

    private data class Spell(val target: Entity, val duration : Long) : RenderedSpell{
        override fun cast(env: CastingEnvironment) {
            if (target !is PlayerEntity){
                if (EphemeraMixinUtils.getFreezeTicks(target) < duration){
                    //Ephemera.LOGGER.info("freezing {} for {} ticks at timestamp {}", target.name.string, duration, env.world.time)
                    EphemeraMixinUtils.freeze(target, duration)
                }
            } else {
                //NYI
            }
        }

    }
}