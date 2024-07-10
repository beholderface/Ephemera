package net.beholderface.ephemera.casting.patterns.spells.great

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell
import net.beholderface.ephemera.api.getStatusEffect
import net.beholderface.ephemera.api.getStatusTagKey
import net.beholderface.ephemera.items.ConjuredArmorItem
import net.beholderface.ephemera.items.ConjuredArmorMaterial
import net.beholderface.ephemera.registry.EphemeraItemRegistry
import net.beholderface.ephemera.registry.PotionIota
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ArmorMaterials
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import javax.annotation.Nullable
import kotlin.math.pow

/*private fun genAttributeModifier(name : String, strength : Float, slot : String, operation : Int, type : Int) : NbtCompound {
    val output = NbtCompound()
    output.putString("AttributeName", name)
    output.putString("Name", name)
    //output.putInt("Amount", strength)
    output.putInt("Operation", operation)
    output.putString("Slot", slot)
    //I have no idea what the UUID on the attribute is actually used for
    output.putUuid("UUID", UUID.randomUUID())
    if (type == 0){
        output.putInt("Amount", strength.toInt())
    }
    if (type == 1){
        output.putFloat("Amount", strength)
    }
    return output
}*/

class OpMageArmor() : SpellAction {
    override val argc = 5
    override val isGreat = true
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getPlayer(0, argc)
        val durability = args.getPositiveInt(1, argc).coerceAtMost(ConjuredArmorMaterial.staticDurability())
        //strength 10 = slightly better than non-enchanted netherite
        val armorStrength = args.getPositiveInt(2, argc).coerceAtMost(10).coerceAtLeast(1)
        val effect = if (args[3].type == PotionIota.TYPE){
            args.getStatusEffect(3, argc, true)
        } else {
            null
        }
        if (effect != null){
            val effectKeyMaybe = Registry.STATUS_EFFECT.getKey(effect)
            if (effectKeyMaybe.isPresent){
                val effectEntry = Registry.STATUS_EFFECT.entryOf(effectKeyMaybe.get())
                if (effectEntry.isIn(getStatusTagKey(Identifier("ephemera:armor_blacklist")))){
                    throw MishapDisallowedSpell("ephemera:blacklist")
                }
            }
            //this would be so absurdly OP if you could somehow get an instant status iota
            if (effect.isInstant){
                throw MishapDisallowedSpell("ephemera:instant")
            }
        }
        val effectStrength = if (effect != null){
            (args.getPositiveInt(4, argc) - 1).coerceIn(0, if (effect == StatusEffects.RESISTANCE){
                3 //no 100% invulnerability armor for you
            } else {
                Integer.MAX_VALUE
            })
        } else {
            0 //no stay of execution armor either
        }
        val slotBools = booleanArrayOf(false, false, false, false)
        val items = arrayOf(EphemeraItemRegistry.MEDIA_BOOTS.get(), EphemeraItemRegistry.MEDIA_LEGGINGS.get(),
            EphemeraItemRegistry.MEDIA_CHESTPLATE.get(), EphemeraItemRegistry.MEDIA_HELMET.get())
        //boots, legs, chest, helmet
        var affectedSlots = 0
        for ((currentSlot, piece) in target.armorItems.withIndex()){
            if (piece.item == Items.AIR){
                slotBools[currentSlot] = true
                affectedSlots++
            }
            if (items.contains(piece.item)){
                affectedSlots++
            }
        }
        val baseLifetime = (durability / 60) //minutes
        var cost = (((armorStrength.toDouble().pow(1.5) * baseLifetime) / 4) * affectedSlots).toInt()
        if (effect != null){
            cost *= effectStrength + 2
        }
        return Triple(Spell(target, slotBools, durability, armorStrength, effect, effectStrength), cost, listOf(ParticleSpray.cloud(target.pos, 2.0)))
    }

    private data class Spell(val player : ServerPlayerEntity, val slots : BooleanArray, val durability : Int, val armorStrength : Int,
                             @Nullable val effect : StatusEffect?, val effectStrength : Int) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            val items = arrayOf(EphemeraItemRegistry.MEDIA_BOOTS.get(), EphemeraItemRegistry.MEDIA_LEGGINGS.get(),
                EphemeraItemRegistry.MEDIA_CHESTPLATE.get(), EphemeraItemRegistry.MEDIA_HELMET.get())
            val armorSlots = arrayOf(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD)
            //based on the values for netherite
            val slotDefenseMultipliers = arrayOf(0.375f, 0.75f, 1f, 0.375f)
            /*val listEmpty = NbtList()
            listEmpty.add(NbtCompound())*/
            val toughness = if (armorStrength >= 6){
                (armorStrength * 0.375).toFloat()
            } else {
                0f
            }
            ArmorMaterials.NETHERITE
            val knockbackResist = if (armorStrength >= 8){
                (armorStrength * 0.015).toFloat()
            } else {
                0f
            }
            for ((currentSlot, slotBool) in slots.withIndex()){
                if (slotBool){
                    val stack = items[currentSlot].defaultStack
                    val stackNbt = stack.orCreateNbt
                    val currentArmorSlot = armorSlots[currentSlot]
                    //stackNbt.putList("Enchantments", listEmpty)
                    val normalArmor = EntityAttributeModifier("armor", (armorStrength * slotDefenseMultipliers[currentSlot]).coerceAtLeast(1f).toDouble(), EntityAttributeModifier.Operation.ADDITION)
                    val armorToughness = EntityAttributeModifier("toughness", toughness.toDouble(), EntityAttributeModifier.Operation.ADDITION)
                    val knockbackResistMod = EntityAttributeModifier("kbResist", knockbackResist.toDouble(), EntityAttributeModifier.Operation.ADDITION)
                    stack.addAttributeModifier(EntityAttributes.GENERIC_ARMOR, normalArmor, currentArmorSlot)
                    if (toughness > 0){
                        stack.addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, armorToughness, currentArmorSlot)
                    }
                    if (knockbackResist > 0){
                        stack.addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, knockbackResistMod, currentArmorSlot)
                    }
                    stackNbt.putInt("DurabilityOverride", durability)
                    stack.damage = stack.maxDamage - durability
                    //Ephemera.LOGGER.info(stack.damage)
                    player.equipStack(items[currentSlot].slotType, stack)
                }
            }
            //only one effect at a time
            //if (effect != null){
                for ((currentSlot, equippedArmor) in player.armorItems.withIndex()){
                    if (items.contains(equippedArmor.item)){
                        val stackNbt = equippedArmor.orCreateNbt;
                        stackNbt.putInt("DurabilityOverride", durability)
                        equippedArmor.damage = equippedArmor.maxDamage - durability
                        ConjuredArmorItem.setStoredStatus(equippedArmor, effect, effectStrength)
                        stackNbt.remove("AttributeModifiers");
                        equippedArmor.addAttributeModifier(EntityAttributes.GENERIC_ARMOR, EntityAttributeModifier("armor", (armorStrength * slotDefenseMultipliers[currentSlot]).coerceAtLeast(1f).toDouble(), EntityAttributeModifier.Operation.ADDITION), armorSlots[currentSlot])
                        if (toughness > 0){
                            equippedArmor.addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, EntityAttributeModifier("toughness", toughness.toDouble(), EntityAttributeModifier.Operation.ADDITION), armorSlots[currentSlot])
                        }
                        if (knockbackResist > 0){
                            equippedArmor.addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, EntityAttributeModifier("kbResist", knockbackResist.toDouble(), EntityAttributeModifier.Operation.ADDITION), armorSlots[currentSlot])
                        }
                    }
                }
            //}
        }
    }
}