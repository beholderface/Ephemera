package net.beholderface.ephemera.casting.patterns.spells.great

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadItem
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.extractMedia
import at.petrak.hexcasting.api.utils.isMediaItem
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.ephemera.api.getItemTagKey
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class OpRepair : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val mediaEntity = args.getItemEntity(0, argc)
        val mediaStack = mediaEntity.stack
        val isMedia = isMediaItem(mediaStack)
        var isUsableMedia = false
        if (isMedia){
            val holder = IXplatAbstractions.INSTANCE.findMediaHolder(mediaStack)
            if (holder?.canConstructBattery() == true){
                isUsableMedia = true
            }
        }
        if (!isUsableMedia){
            throw MishapBadItem.of(mediaEntity, "media")
        }
        if (env.castingEntity == null){
            throw MishapBadCaster()
        }
        val toRepair = env.castingEntity!!.getStackInHand(env.otherHand)
        val whitelisted = toRepair.isIn(getItemTagKey(Identifier("ephemera:repairwhitelist")))
        val blacklisted = toRepair.isIn(getItemTagKey(Identifier("ephemera:repairblacklist")))
        val conversion = 5.0
        if (toRepair.isDamaged && ((whitelisted || EnchantmentHelper.getLevel(Enchantments.MENDING, toRepair) > 0) && !blacklisted)){
            val cost = MediaConstants.SHARD_UNIT
            val repairCost = ((toRepair.damage / conversion) * MediaConstants.DUST_UNIT).coerceAtMost(extractMedia(mediaStack,
                ((toRepair.damage / conversion) * MediaConstants.DUST_UNIT).toLong(), true, true).toDouble())
            return SpellAction.Result(Spell(mediaEntity, toRepair, repairCost.toLong(), ((repairCost / MediaConstants.DUST_UNIT) * 20).toInt()),
                cost, listOf(ParticleSpray.burst(mediaEntity.pos, 1.0, 16)))
        } else {
            throw MishapBadOffhandItem.of(toRepair, "ephemera:repairable")
        }
    }

    private data class Spell(val mediaItem : ItemEntity, val toRepair : ItemStack, val withdrawal : Long, val repairAmount : Int) :
        RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            extractMedia(mediaItem.stack, withdrawal, true, false)
            //Ephemera.LOGGER.info("Withdrawing $withdrawal media from stack, to restore $repairAmount durability")
            toRepair.damage -= repairAmount
        }
    }
}