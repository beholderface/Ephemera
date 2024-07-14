package net.beholderface.ephemera.api

import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.beholderface.ephemera.Ephemera
import net.beholderface.ephemera.casting.iotatypes.PotionIota
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.common.entities.BaseWisp
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.annotation.Nullable

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

fun getBlockTagKey(id : Identifier) : TagKey<Block> {
    return TagKey.of(Registry.BLOCK_KEY, id)
}
fun getEntityTagKey(id : Identifier) : TagKey<EntityType<*>> {
    return TagKey.of(Registry.ENTITY_TYPE_KEY, id)
}
fun getItemTagKey(id : Identifier) : TagKey<Item> {
    return TagKey.of(Registry.ITEM_KEY, id)
}
fun getStatusTagKey(id : Identifier) : TagKey<StatusEffect> {
    return TagKey.of(Registry.MOB_EFFECT_KEY, id)
}

fun StatusEffect.effectToIdentifier(): Identifier? {
    return Registry.STATUS_EFFECT.getId(this)
}

fun ILinkable.getConnected(@Nullable previous : ILinkable?, connectionSet : HashSet<ILinkable>, recursion : Int, maxRecursion : Int) : HashSet<ILinkable>{
    if (recursion < 0 || recursion > maxRecursion + 1){
        throw IllegalAccessException("Recusion depth must be between 0 and max recursion.")
    }
    if (maxRecursion > 1024 || maxRecursion < 0){
        //I really hope nobody thinks 1024 recursion is necessary
        throw IllegalArgumentException("Max recursion must be between 0 and 1024.")
    }
    val toScan = this.numLinked()
    if (toScan > 0){
        for (i in 0 until toScan){
            val linkToCheck = this.getLinked(i)
            //Ephemera.LOGGER.info("Recursion depth: $recursion, iteration: $i, position: ${linkToCheck?.getPosition()}")
            if (linkToCheck == previous){
                //do nothing, this check is just for performance purposes
            } else if (linkToCheck != null){
                if (!connectionSet.contains(linkToCheck)){
                    //Ephemera.LOGGER.info("Found new position ${linkToCheck.getPosition()}")
                    if (recursion <= maxRecursion) {
                        connectionSet.add(linkToCheck)
                        linkToCheck.getConnected(this, connectionSet, recursion + 1, maxRecursion)
                    }
                }/* else {
                    Ephemera.LOGGER.info("Connection map already contains position ${linkToCheck.getPosition()}")
                }*/
            }
        }
    }
    /*if (recursion == 0){
        Ephemera.LOGGER.info("Found ${connectionSet.size} connected nodes.")
    }*/
    return connectionSet
}

fun ILinkable.getConnected(maxRecursion: Int) : HashSet<ILinkable>{
    val set = HashSet<ILinkable>()
    set.add(this)
    return this.getConnected(null, set, 0, maxRecursion)
}

fun List<Iota>.getWispOrPlayer(idx: Int, argc: Int = 0) : Entity {
    val iota = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (iota is EntityIota){
        val entity = iota.entity
        if (entity is BaseWisp || entity is PlayerEntity){
            return entity
        }
    }
    throw MishapInvalidIota.of(iota, if (argc==0) idx else argc - (idx + 1), "wisporplayer")
}

fun stringToWorld(key : Identifier) : ServerWorld? {
    val server = Ephemera.getCachedServer();
    var output : ServerWorld? = null
    server.worlds?.forEach {
        if (it.registryKey.value.equals(key)){
            output = it
        }
    }
    return output
}

fun String.hash() : String{
    return try {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(this.toByteArray(StandardCharsets.UTF_8))
        String(digest.digest())
    } catch (exception: NoSuchAlgorithmException) {
        "???"
        //do nothing? idk
    }
}