package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import net.beholderface.ephemera.casting.iotatypes.PotionIota
import net.minecraft.entity.effect.StatusEffects

class OpGetEffects : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
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
        if (!target.hasStatusEffect(StatusEffects.ABSORPTION) && target.absorptionAmount > 0){
            effectDetails.add(PotionIota(StatusEffects.ABSORPTION))
        }
        return listOf(ListIota(effectDetails.toList()))
    }
}