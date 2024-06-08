package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import ram.talia.hexal.api.spell.iota.EntityTypeIota

class OpFilteredEntityRaycast : ConstMediaAction {
    override val argc = 3
    override val mediaCost = MediaConstants.DUST_UNIT / 50

    private fun isAllowedInFilter(entity : Entity, filter : List<Iota>) : Boolean{
        if (entity is LivingEntity){
            val livingEntity = entity as LivingEntity
            val effects = livingEntity.statusEffects
            for (effect in effects){
                if (effect.effectType.translationKey.equals("effect.oneironaut.detection_resistance")){
                    return false
                }
            }
        }
        for (iota in filter){
            val typeIota = iota as EntityTypeIota
            if (entity.type == typeIota.entityType){
                return true
            }
        }
        return false
    }

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val filter = args.getList(0, argc)
        for (iota in filter){
            if (iota.type != EntityTypeIota.TYPE){
                throw MishapInvalidIota(args[0], 2, Text.translatable("ephemera.mishap.entitytypelistplease"))
            }
        }
        val origin = args.getVec3(1, argc)
        val look = args.getVec3(2, argc)
        val endp = Action.raycastEnd(origin, look)

        ctx.assertVecInRange(origin)

        val entityHitResult = ProjectileUtil.raycast(
            ctx.caster,
            origin,
            endp,
            Box(origin, endp),
            {isAllowedInFilter(it, filter.toList())},
            1_000_000.0
        )
        return if (entityHitResult != null && ctx.isEntityInRange(entityHitResult.entity)) {
            entityHitResult.entity.asActionResult
        } else {
            listOf(NullIota())
        }
    }
}