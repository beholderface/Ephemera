package net.beholderface.ephemera.casting.patterns.spells.great

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.putList
import net.beholderface.ephemera.items.ConjuredArmorMaterial
import net.beholderface.ephemera.registry.EphemeraItemRegistry
import net.minecraft.item.ArmorMaterials
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

private fun genAttributeModifier(name : String, strength : Float, slot : String, operation : Int, type : Int) : NbtCompound {
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
}

class OpMageArmor() : SpellAction {
    override val argc = 3
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getPlayer(0, argc)
        val durability = args.getPositiveInt(1, argc)
        //strength 10 = slightly better than non-enchanted netherite
        val strength = args.getPositiveInt(2, argc).coerceAtMost(10)
        val slotBools = booleanArrayOf(false, false, false, false)
        //boots, legs, chest, helmet
        var currentSlot = 0
        for (piece in target.armorItems){
            if (piece.item == Items.AIR){
                slotBools[currentSlot] = true
            }
            currentSlot++
        }
        return Triple(Spell(target, slotBools, durability, strength), 1, listOf(ParticleSpray.cloud(target.pos, 2.0)))
    }

    private data class Spell(val player : ServerPlayerEntity, val slots : BooleanArray, val durability : Int, val strength : Int) : RenderedSpell{
        override fun cast(ctx: CastingContext) {
            val items = arrayOf(EphemeraItemRegistry.MEDIA_BOOTS.get(), EphemeraItemRegistry.MEDIA_LEGGINGS.get(),
                EphemeraItemRegistry.MEDIA_CHESTPLATE.get(), EphemeraItemRegistry.MEDIA_HELMET.get())
            val slotNames = arrayOf("feet", "legs", "chest", "head")
            //based on the values for netherite
            val slotDefenseMultipliers = arrayOf(0.375f, 0.75f, 1f, 0.375f)
            val listEmpty = NbtList()
            listEmpty.add(NbtCompound())
            val toughness = if (strength >= 6){
                (strength * 0.375).toFloat()
            } else {
                0f
            }
            ArmorMaterials.NETHERITE
            val knockbackResist = if (strength >= 8){
                (strength * 0.015).toFloat()
            } else {
                0f
            }
            for ((currentSlot, slotBool) in slots.withIndex()){
                if (slotBool){
                    val adjustedDurability = durability.coerceAtMost(ConjuredArmorMaterial.staticDurability())
                    val stack = items[currentSlot].defaultStack
                    val stackNbt = stack.orCreateNbt
                    val list = NbtList()
                    val currentSlotName = slotNames[currentSlot]
                    list.add(genAttributeModifier("generic.armor",
                        (strength * slotDefenseMultipliers[currentSlot]).coerceAtLeast(1f), currentSlotName, 0, 0))
                    if (toughness > 0){
                        list.add(genAttributeModifier("generic.armor_toughness",
                            toughness, currentSlotName, 0, 0))
                    }
                    if (knockbackResist > 0){
                        list.add(genAttributeModifier("generic.knockback_resistance",
                            knockbackResist, currentSlotName, 0, 1))
                    }
                    stackNbt.putList("AttributeModifiers", list)
                    stackNbt.putList("Enchantments", listEmpty)
                    stackNbt.putInt("DurabilityOverride", adjustedDurability)
                    //stack.addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, EntityAttributeModifier("b", 1.0, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.HEAD)
                    stack.damage = stack.maxDamage - adjustedDurability
                    //Ephemera.LOGGER.info(stack.damage)
                    player.equipStack(items[currentSlot].slotType, stack)
                }
            }
        }
    }
}