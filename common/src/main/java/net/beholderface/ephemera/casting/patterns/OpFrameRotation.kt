package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getEntity
import at.petrak.hexcasting.api.spell.getPositiveInt
import at.petrak.hexcasting.api.spell.getPositiveIntUnder
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadEntity
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.text.Text

class OpFrameRotation(val adjust : Int) : ConstMediaAction {
    override val argc = adjust + 1
    override val mediaCost = (adjust * MediaConstants.DUST_UNIT) / 10
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val initialEntity = args.getEntity(0, argc)
        ctx.assertEntityInRange(initialEntity)
        if (initialEntity is ItemFrameEntity){
            val frame = initialEntity as ItemFrameEntity
            if (adjust == 1){
                frame.rotation = (args.getPositiveIntUnder(1, 8, argc))/* % 8*/
                return listOf()
            } else {
                return listOf(DoubleIota(frame.rotation.toDouble()))
            }
        } else {
            throw MishapBadEntity(initialEntity, Text.translatable("ephemera.mishap.noitemframe"))
        }
    }
}