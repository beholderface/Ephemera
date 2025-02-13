package net.beholderface.ephemera.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.beholderface.ephemera.Ephemera
import net.minecraft.text.Text
import net.minecraft.util.DyeColor

class MishapNoThoth : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
        return dyeColor(DyeColor.CYAN)
    }

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text {
        return error(Ephemera.MOD_ID + ":nothoth")
    }

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        //noop
    }
}