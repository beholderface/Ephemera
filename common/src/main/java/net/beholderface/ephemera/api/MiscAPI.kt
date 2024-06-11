package net.beholderface.ephemera.api

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import net.beholderface.ephemera.Ephemera
import net.beholderface.ephemera.registry.PotionIota
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import ram.talia.hexal.api.linkable.ILinkable
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

fun effectToIdentifier(targetEffect: StatusEffect): Identifier? {
    return Registry.STATUS_EFFECT.getId(targetEffect)
}

fun ILinkable.getConnected(@Nullable previous : ILinkable?, connectionMap : HashMap<ILinkable, ILinkable>, recursion : Int, maxRecursion : Int) : HashMap<ILinkable, ILinkable>{
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
                if (!connectionMap.contains(linkToCheck)){
                    //Ephemera.LOGGER.info("Found new position ${linkToCheck.getPosition()}")
                    if (recursion <= maxRecursion) {
                        connectionMap[linkToCheck] = linkToCheck
                        linkToCheck.getConnected(this, connectionMap, recursion + 1, maxRecursion)
                    }
                }/* else {
                    Ephemera.LOGGER.info("Connection map already contains position ${linkToCheck.getPosition()}")
                }*/
            }
        }
    }
    return connectionMap
}

fun ILinkable.getConnected(maxRecursion: Int) : HashMap<ILinkable, ILinkable>{
    val map = HashMap<ILinkable, ILinkable>()
    map[this] = this
    return this.getConnected(null, map, 0, maxRecursion)
}