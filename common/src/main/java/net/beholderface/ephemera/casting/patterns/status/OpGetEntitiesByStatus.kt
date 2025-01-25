package net.beholderface.ephemera.casting.patterns.status

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveDouble
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import net.beholderface.ephemera.api.getStatusEffect
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.registry.Registries
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.Identifier

class OpGetEntitiesByStatus(val invert : Boolean) : ConstMediaAction {
    override val argc = 3
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val status = args.getStatusEffect(0, argc, false)
        val target = args.getVec3(1, argc)
        env.assertVecInRange(target)
        val radius = args.getPositiveDouble(2, argc)
        val box = Box(target.add(Vec3d(-radius, -radius, -radius)), target.add(Vec3d(radius, radius, radius)))
        val entities = env.world.getOtherEntities(null, box) {
            isAffectedAndReachable(it, status, env, invert) && it.squaredDistanceTo(target) <= (radius * radius)}.sortedBy { it.squaredDistanceTo(target) }
        return entities.map(::EntityIota).asActionResult
    }
    companion object{
        fun isAffectedAndReachable(e: Entity, s : StatusEffect, env : CastingEnvironment, invert : Boolean) : Boolean{
            if (e.isLiving && env.isEntityInRange(e)){
                val le = e as LivingEntity
                //I would not be so foolish :)
                if (le.hasStatusEffect(Registries.STATUS_EFFECT.get(Identifier.tryParse("oneironaut:detection_resistance")))){
                    return false
                }
                var result = le.hasStatusEffect(s)
                if (!result && s == StatusEffects.ABSORPTION){
                    result = le.absorptionAmount > 0
                }
                if (invert){
                    result = !result
                }
                return result
            } else {
                return false
            }
        }
    }
}