package net.beholderface.ephemera.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class MishapNonconjured(val pos: BlockPos, val world : ServerWorld) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(Vec3d.ofCenter(pos), 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Text =
        error("ephemera:nonconjured", this.pos.toShortString(), blockAtPos(ctx, this.pos))

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        world.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.25f, World.ExplosionSourceType.NONE)
    }

    companion object {
        @JvmStatic
        fun of(pos: BlockPos, world : ServerWorld): MishapNonconjured {
            return MishapNonconjured(pos, world)
        }
    }

}