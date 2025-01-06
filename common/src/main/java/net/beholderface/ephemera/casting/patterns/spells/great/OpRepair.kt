package net.beholderface.ephemera.casting.patterns.spells.great

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getItemEntity
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadItem
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.utils.extractMedia
import at.petrak.hexcasting.api.utils.isMediaItem
import at.petrak.hexcasting.common.casting.operators.spells.OpRecharge
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.ephemera.Ephemera
import net.beholderface.ephemera.api.getItemTagKey
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class OpRepair : SpellAction {
    override val argc = 1
    override val alwaysProcessGreatSpell = true
    override val isGreat = true
    override val causesBlindDiversion = true

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
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

        val toRepair = ctx.caster.getStackInHand(ctx.otherHand)
        val whitelisted = toRepair.isIn(getItemTagKey(Identifier("ephemera:repairwhitelist")))
        val blacklisted = toRepair.isIn(getItemTagKey(Identifier("ephemera:repairblacklist")))
        if (toRepair.isDamaged && ((whitelisted || EnchantmentHelper.getLevel(Enchantments.MENDING, toRepair) > 0) && !blacklisted)){
            val cost = MediaConstants.SHARD_UNIT
            val repairCost = ((toRepair.damage / 20.0) * MediaConstants.DUST_UNIT).coerceAtMost(extractMedia(mediaStack,
                ((toRepair.damage / 20.0) * MediaConstants.DUST_UNIT).toInt(), true, true).toDouble())
            return Triple(Spell(mediaEntity, toRepair, repairCost.toInt(), ((repairCost / MediaConstants.DUST_UNIT) * 20).toInt()), cost, listOf(ParticleSpray.burst(mediaEntity.pos, 1.0, 16)))
        } else {
            throw MishapBadOffhandItem.of(toRepair, ctx.otherHand, "ephemera:repairable")
        }
    }

    private data class Spell(val mediaItem : ItemEntity, val toRepair : ItemStack, val withdrawal : Int, val repairAmount : Int) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            extractMedia(mediaItem.stack, withdrawal, true, false)
            //Ephemera.LOGGER.info("Withdrawing $withdrawal media from stack, to restore $repairAmount durability")
            toRepair.damage -= repairAmount
        }
    }
}