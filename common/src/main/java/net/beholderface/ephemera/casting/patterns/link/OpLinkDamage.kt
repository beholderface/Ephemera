package net.beholderface.ephemera.casting.patterns.link

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.world.explosion.Explosion
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.api.linkable.LinkableRegistry
import javax.annotation.Nullable

//unimplemented because it's too easy
class OpLinkDamage : SpellAction {
    override val argc = 2

    //don't try to use this one, it's been superseded by the one in MiscAPI
    private fun getConnected(target : ILinkable, @Nullable previous : ILinkable?, connectionMap : HashMap<ILinkable, ILinkable>, recursion : Int, maxRecursion : Int){
        if (recursion < 0 || recursion > maxRecursion + 1){
            throw IllegalAccessException("Recusion depth must be between 0 and max recursion.")
        }
        if (maxRecursion > 1024 || maxRecursion < 0){
            //I really hope nobody thinks 1024 recursion is necessary
            throw IllegalArgumentException("Max recursion must be between 0 and 1024.")
        }
        val toScan = target.numLinked()
        if (toScan > 0){
            for (i in 0 until toScan){
                val linkToCheck = target.getLinked(i)
                if (linkToCheck == previous){
                    //do nothing, this check is just for performance purposes
                } else if (linkToCheck != null && !connectionMap.contains(linkToCheck)){
                    if (recursion <= maxRecursion) {
                        connectionMap[linkToCheck] = linkToCheck
                        getConnected(linkToCheck, target, connectionMap, recursion + 1, maxRecursion)
                    }
                }
            }
        }
    }
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val initialTarget = LinkableRegistry.linkableFromIota(args[0], ctx.world) ?: throw MishapInvalidIota.ofType(args[0], 0, "linkable")
        ctx.assertVecInRange(initialTarget.getPosition())
        val maxRecursion = args.getPositiveInt(1, argc).coerceAtMost(32)
        val connectedThings = HashMap<ILinkable, ILinkable>()
        connectedThings[initialTarget] = initialTarget
        getConnected(initialTarget, null, connectedThings, 0, maxRecursion)
        val particles : MutableList<ParticleSpray> = mutableListOf()
        for (linked in connectedThings.values){
            particles.add(ParticleSpray.burst(linked.getPosition(), 2.0, 16))
        }
        return Triple(Spell(initialTarget, connectedThings), ((connectedThings.size * 2) + 10) * MediaConstants.DUST_UNIT, particles)
    }

    private data class Spell(val initialTarget : ILinkable, val connectedThings : HashMap<ILinkable, ILinkable>) : RenderedSpell{

        override fun cast(ctx: CastingContext) {
            for (linked in connectedThings.values){
                //val type = linked.getLinkableType()
                //if (type == LinkableTypes.LINKABLE_ENTITY_TYPE || type == LinkableTypes.PLAYER_LINKSTORE_TYPE){
                    if (linked != initialTarget){ //if it damaged the initial target it might as well just be a boring "do damage" spell
                        val pos = linked.getPosition()
                        ctx.world.createExplosion(ctx.caster, pos.x, pos.y - 0.1, pos.z, 2f, false, Explosion.DestructionType.NONE)
                    }
                //}
            }
        }
    }
}