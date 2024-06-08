package net.beholderface.ephemera.casting.patterns.spells.great

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.putList
import net.beholderface.ephemera.registry.EphemeraItemRegistry
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

private fun genAttributeModifier(name : String, strength : Int, slot : String, operation : Int) : NbtCompound {
    val output = NbtCompound()
    output.putString("AttributeName", name)
    output.putString("Name", name)
    output.putInt("Amount", strength)
    output.putInt("Operation", operation)
    output.putString("Slot", slot)
    //I have no idea what the UUID on the attribute is actually used for
    output.putUuid("UUID", UUID.randomUUID())
    return output
}

class OpMageArmor() : SpellAction {
    override val argc = 3
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getPlayer(0, argc)
        val durability = args.getPositiveInt(1, argc)
        val strength = args.getPositiveInt(2, argc)
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
            for ((currentSlot, slotBool) in slots.withIndex()){
                if (slotBool){
                    val stack = items[currentSlot].defaultStack
                    val list = NbtList()
                    list.add(genAttributeModifier("generic.armor", strength, slotNames[currentSlot], 0))
                    stack.nbt.putList("AttributeModifiers", list)
                    player.equipStack(items[currentSlot].slotType, stack)
                }
            }
        }
    }
}