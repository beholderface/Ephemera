package net.beholderface.ephemera.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

//joke pattern
class MishapNo(val pos: BlockPos) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.RED)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(Vec3d.ofCenter(pos), 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text =
        error("ephemera:no")

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
    }

    companion object {
        @JvmStatic
        fun of(pos: BlockPos): MishapNo {
            return MishapNo(pos)
        }
    }
}