package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.utils.darkGreen
import at.petrak.hexcasting.api.utils.green
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class OpPrintToOther : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getPlayer(0, argc)
        val sentIota = args[1]
        if (sentIota.display().string.length > 255){
            throw MishapInvalidIota(sentIota, 0, Text.translatable("ephemera.mishap.toolongiota"))
        }
        val currentTimestamp = env.world.server.overworld.time
        val cost = RevealHistoryManager.calculateCost(target.uuid, currentTimestamp)
        return SpellAction.Result(Spell(target, sentIota), cost, listOf(ParticleSpray.burst(target.pos, 2.0, 16)))
    }

    private data class Spell(val target : ServerPlayerEntity, val sentIota: Iota) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val isString = sentIota.display().asTruncatedString(1).equals("\"") && sentIota.type != EntityIota.TYPE
            val display = sentIota.display()
            val name : Text = if (env.castingEntity != null){
                (env.castingEntity)!!.name
            } else {
                Text.translatable("text.ephemera.nosender")
            }
            val introduction = Text.translatable("text.ephemera.revealIntroduction", name).green
            val coloredQuotation = Text.literal(if (!isString){ "\"" } else { "" }).darkGreen
            val message = introduction.append(coloredQuotation).append(display).append(coloredQuotation)
            RevealHistoryManager.notifyReveal(target.uuid, env.world)
            target.sendMessageToClient(message, false)
        }
    }
}