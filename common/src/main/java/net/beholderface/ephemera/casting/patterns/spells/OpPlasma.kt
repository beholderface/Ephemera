package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import ram.talia.hexal.api.spell.casting.IMixinCastingContext
import ram.talia.hexal.api.spell.mishaps.MishapNoWisp
import ram.talia.hexal.common.entities.BaseCastingWisp
import ram.talia.hexal.common.entities.TickingWisp
import ram.talia.hexal.common.network.MsgParticleLinesAck

class OpPlasma() : SpellAction {
    override val argc: Int = 0
    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val mCast = ctx as? IMixinCastingContext
        if (mCast == null || !mCast.hasWisp())
            throw MishapNoWisp()
        val wisp = mCast.wisp
        return Triple(Spell(wisp!!), MediaConstants.DUST_UNIT * 2, listOf())
    }

    private data class Spell(val wisp : BaseCastingWisp) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            val maxrange = 8.0
            val allegedlyNotWisp = wisp as Entity
            val origin = allegedlyNotWisp.pos
            var dir = if (wisp is TickingWisp){
                if (wisp.getTargetMovePos() != null){
                    wisp.getTargetMovePos()?.subtract(allegedlyNotWisp.pos)?.normalize()!!
                } else {
                    Vec3d.ZERO
                }
            } else {
                wisp.rotationVector
            }
            if (dir.equals(Vec3d.ZERO) || dir == null){
                val random = allegedlyNotWisp.world.random
                dir = Vec3d(random.nextBetween(-100, 100).toDouble(), random.nextBetween(-100, 100).toDouble(), random.nextBetween(-100, 100).toDouble()).normalize()
            }
            val endpoint1 = origin.add(dir.multiply(maxrange))
            val blockCast = ctx.world.raycast(RaycastContext(origin, endpoint1, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, ctx.caster))
            //if the block raycast hit anything, limit the entity raycasts to only as far as the block raycast got
            val endpoint2 = if (blockCast != null){
                blockCast.pos
            } else {
                endpoint1
            }
            val hitEntities = mutableListOf<LivingEntity>()
            var latestHit: EntityHitResult? = null
            do {
                latestHit = ProjectileUtil.raycast(allegedlyNotWisp, origin, endpoint2, Box(origin, endpoint2),
                    {it.pos.isInRange(origin, maxrange) && it.isLiving && !hitEntities.contains(it)}, 1000000.0)
                if (latestHit != null){
                    hitEntities.add(latestHit.entity as LivingEntity)
                }
            } while (latestHit != null)
            for (target in hitEntities){
                target.damage(DamageSource.MAGIC, 4f)
            }
            if (blockCast != null){
                val hitBlockState = ctx.world.getBlockState(blockCast.blockPos)
                val hitBlockType = hitBlockState.block
                if (hitBlockType.hardness in 0.0..2.5){
                    if (ctx.world.random.nextBetween(0, 5) >= (hitBlockType.hardness)
                        && IXplatAbstractions.INSTANCE.isBreakingAllowed(ctx.world, blockCast.blockPos, hitBlockState, ctx.caster)){
                        ctx.world.breakBlock(blockCast.blockPos, true, ctx.caster)
                    }
                }
            }
            IXplatAbstractions.INSTANCE.sendPacketNear(origin, 128.0, ctx.world, MsgParticleLinesAck(listOf(origin, endpoint2),
                IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)))
        }

    }
}