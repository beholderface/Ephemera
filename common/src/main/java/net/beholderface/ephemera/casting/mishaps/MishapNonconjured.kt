package net.beholderface.ephemera.casting.mishaps

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.explosion.Explosion

class MishapNonconjured(val pos: BlockPos/*, val expected: Text*/) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingContext) =
        ParticleSpray.burst(Vec3d.ofCenter(pos), 1.0)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Text =
        error("oneironaut:nonconjured", this.pos.toShortString(), blockAtPos(ctx, this.pos))

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {
        ctx.world.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.25f, Explosion.DestructionType.NONE)
    }

    companion object {
        @JvmStatic
        fun of(pos: BlockPos/*, stub: String*/): MishapNonconjured {
            return MishapNonconjured(pos/*, Text.translatable("oneironaut.mishap.uninfusable")*/)
        }
    }

}