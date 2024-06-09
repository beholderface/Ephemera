package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import net.beholderface.ephemera.registry.PotionIota

class OpGetEffects : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        val effects = target.statusEffects
        //val effectIotas : MutableList<PotionIota> = mutableListOf()
        var currentList : PotionIota
        val effectDetails : MutableList<PotionIota> = mutableListOf()
        for (effect in effects){

            currentList = PotionIota(effect.effectType)
            effectDetails.add(currentList)
            //effectIotas.add(PotionIota(effect.effectType))
        }
        return listOf(ListIota(effectDetails.toList()))
    }
}