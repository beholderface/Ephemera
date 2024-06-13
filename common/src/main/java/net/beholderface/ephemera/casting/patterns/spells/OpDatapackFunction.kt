package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.*
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.beholderface.ephemera.Ephemera
import net.beholderface.ephemera.recipe.EphemeraRecipeTypes
import net.minecraft.entity.Entity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.Registry
import ram.talia.hexal.api.spell.iota.*
import ram.talia.hexal.common.lib.HexalIotaTypes
import ram.talia.moreiotas.api.spell.iota.MatrixIota
import ram.talia.moreiotas.api.spell.iota.StringIota
import ram.talia.moreiotas.common.lib.MoreIotasIotaTypes
import java.security.MessageDigest

class OpDatapackFunction(override val argc: Int, val id : String) : SpellAction {
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        //val functionManager = ctx.world.server.commandFunctionManager
        //functionManager.execute(functionManager.getFunction(functionName).get(), ctx.world.server.commandSource)
        val recipeManger = ctx.world.recipeManager
        val spellRecipes = recipeManger.listAllOfType(EphemeraRecipeTypes.DATA_SPELL_TYPE)
        val recipe = spellRecipes.find { it.matches(this) } ?: throw MishapDisallowedSpell()
        val scoreboardMap = HashMap<String, Pair<Int, List<Entity>>>()
        for ((i, _) in args.withIndex()){
            val trip = args.getScoreboardData(i, argc)
            scoreboardMap[trip.first] = Pair(trip.second, trip.third)
        }
        /*for ((i, iota) in args.withIndex()){
            if (iota.type != recipe.argTypes[i]){
                throw MishapInvalidIota(iota, (argc - 1) - i, Text.translatable("ephemera:mismatchediota"))
            }
            if (iota.type == HexIotaTypes.VEC3 && recipe.enforceVecAmbit){
                ctx.assertVecInRange((iota as Vec3Iota).vec3)
            }
            if (iota.type == HexIotaTypes.ENTITY){
                ctx.assertEntityInRange((iota as EntityIota).entity)
            }
        }*/

        return Triple<RenderedSpell, Int, List<ParticleSpray>>(
            Spell(scoreboardMap, argc, id, ctx.caster.commandSource.withSilent().withLevel(2), recipe.functionString),
            recipe.mediaCost,
            listOf()
        )
    }

    private fun List<Iota>.getScoreboardData(idx: Int, argc: Int = 0) : Triple<String, Int, List<Entity>>{
        val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
        if (x is ListIota){
            val list = x.list.toList()
            if (list.size == 3){
                if (list[0].type == MoreIotasIotaTypes.STRING_TYPE && list[1].type == HexIotaTypes.DOUBLE && list[2].type == HexIotaTypes.LIST){
                    val string = (list[0] as StringIota).string
                    val num = (list[1] as DoubleIota).double.toInt()
                    val entities : MutableList<Entity> = mutableListOf()
                    for (iota in (list[2] as ListIota).list){
                        if (iota is EntityIota){
                            entities.add(iota.entity)
                        } else {
                            throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "ephemera:scoreboardsyntax")
                        }
                    }
                    return Triple(string, num, entities.toList())
                }
            }
        }
        throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "ephemera:scoreboardsyntax")
    }

    /*
    fun List<Iota>.getStatusEffect(idx: Int, argc: Int = 0, allowShroud : Boolean) : StatusEffect {
        val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
        if (x is PotionIota) {
            if (!allowShroud && (x as PotionIota).effect == Registry.STATUS_EFFECT.get(Identifier.tryParse("oneironaut:detection_resistance"))){
                throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "ephemera:visiblestatus")
            }
            return (x as PotionIota).effect
        }
    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "ephemera:status")
    }
    * */

    private data class Spell(val scoreboardData : Map<String, Pair<Int, List<Entity>>>, val argc : Int, val id : String, val source : ServerCommandSource, val function : String?) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            if (function != null){
                val manager = ctx.world.server.commandManager
                //Ephemera.LOGGER.info("calculated function command: $command")
                for (key in scoreboardData.keys){
                    manager.executeWithPrefix(source, "scoreboard objectives add $key dummy")
                    for (entity in scoreboardData[key]!!.second){
                        manager.executeWithPrefix(source, "scoreboard players set ${entity.uuid} $key ${scoreboardData[key]!!.first}")
                    }
                }
                val functionCommand = "function $function"
                manager.executeWithPrefix(source, functionCommand)
                for (key in scoreboardData.keys){
                    manager.executeWithPrefix(source, "scoreboard objectives remove $key")
                }
            }
            //ctx.caster.sendMessage(Text.of("Executed $id ($function), with $argc arguments."))
        }

        private fun Iota.prepareStringArg(random : Random) : String{
            when(this.type){
                HexIotaTypes.ENTITY -> {
                    val castIota = (this as EntityIota)
                    return " " + castIota.entity.uuidAsString
                }
                HexIotaTypes.VEC3 -> {
                    val castIota = (this as Vec3Iota)
                    val vec = castIota.vec3
                    return " ${vec.x} ${vec.y} ${vec.z}"
                }
                HexIotaTypes.DOUBLE -> {
                    val castIota = (this as DoubleIota)
                    return castIota.double.toString()
                }
                HexIotaTypes.BOOLEAN -> {
                    val castIota = (this as BooleanIota)
                    return if (castIota.bool){
                        " true"
                    } else {
                        " false"
                    }
                }
                HexIotaTypes.NULL -> {
                    //use this to omit an optional argument
                    return ""
                }
                HexIotaTypes.GARBAGE -> {
                    //what's the use case for this? idfk, what's the use case for any garbage iota?
                    val digest = MessageDigest.getInstance("SHA-256")
                    digest.update(Ephemera.getKTbytes(random.nextInt(Integer.MAX_VALUE).toString()))
                    val output = String(digest.digest())
                    Ephemera.LOGGER.info(output)
                    return " $output"
                }
                HexalIotaTypes.ENTITY_TYPE -> {
                    val castIota = (this as EntityTypeIota)
                    val type = castIota.entityType
                    return " " + Registry.ENTITY_TYPE.getId(type).toString()
                }
                HexalIotaTypes.IOTA_TYPE -> {
                    val castIota = (this as IotaTypeIota)
                    return " " + castIota.iotaType.typeName().toString().substring(castIota.iotaType.typeName().toString().lastIndexOf('.') + 1)
                }
                HexalIotaTypes.ITEM_TYPE -> {
                    val castIota = (this as ItemTypeIota)
                    val either = castIota.either
                    return if (either.left().isPresent){
                        val item = either.left().get()
                        " " + Registry.ITEM.getId(item).toString()
                    } else {
                        val block = either.right().get()
                        " " + Registry.BLOCK.getId(block).toString()
                    }
                }
                HexalIotaTypes.GATE -> {
                    val castIota = (this as GateIota)
                    return " " + castIota.gateIndex.toString()
                }
                HexalIotaTypes.ITEM -> {
                    val castIota = (this as MoteIota)
                    val stacks = castIota.getStacksToDrop(Integer.MAX_VALUE)
                    var count = 0
                    for (stack in stacks){
                        count += stack.count
                    }
                    val bigStack = stacks[0].copy()
                    bigStack.count = count
                    return " " + bigStack.serializeToNBT().toString()
                }
                MoreIotasIotaTypes.STRING_TYPE -> {
                    val castIota = (this as StringIota)
                    return " " + castIota.string
                }
                MoreIotasIotaTypes.MATRIX_TYPE -> {
                    //I'm not going to handle this in more detail because I don't want to deal with the existing MoreIotas matrix bug
                    val castIota = (this as MatrixIota)
                    return " " + castIota.display().string
                }
                HexIotaTypes.LIST ->{
                    val castIota = (this as ListIota)
                    return " " + castIota.display().string
                }
                else ->{
                    return " $this"
                }
            }
        }

    }

}