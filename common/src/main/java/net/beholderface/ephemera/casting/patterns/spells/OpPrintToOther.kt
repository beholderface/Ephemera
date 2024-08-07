package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getPlayer
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.utils.darkGreen
import at.petrak.hexcasting.api.utils.green
import net.beholderface.ephemera.api.arbitraryLog
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class OpPrintToOther : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val target = args.getPlayer(0, argc)
        val sentIota = args[1]
        if (sentIota.display().string.length > 255){
            throw MishapInvalidIota(sentIota, 0, Text.translatable("ephemera.mishap.toolongiota"))
        }
        val currentTimestamp = ctx.world.server.overworld.time
        val cost = RevealHistoryManager.calculateCost(target.uuid, currentTimestamp)
        return Triple(Spell(target, sentIota), cost, listOf(ParticleSpray.burst(target.pos, 2.0, 16)))
    }

    private data class Spell(val target : ServerPlayerEntity, val sentIota: Iota) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            val isString = sentIota.display().asTruncatedString(1).equals("\"") && sentIota.type != EntityIota.TYPE
            val display = sentIota.display()
            val introduction = Text.translatable("text.ephemera.revealIntroduction", ctx.caster.name).green
            val coloredQuotation = Text.literal(if (!isString){ "\"" } else { "" }).darkGreen
            val message = introduction.append(coloredQuotation).append(display).append(coloredQuotation)
            RevealHistoryManager.notifyReveal(target.uuid, ctx.world)
            target.sendMessageToClient(message, false)
        }
    }
}