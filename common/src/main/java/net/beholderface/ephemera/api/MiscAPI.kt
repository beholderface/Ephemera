package net.beholderface.ephemera.api

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import net.beholderface.ephemera.registry.PotionIota
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

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