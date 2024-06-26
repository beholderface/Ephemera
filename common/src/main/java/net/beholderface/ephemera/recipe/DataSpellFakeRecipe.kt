package net.beholderface.ephemera.recipe

/*
import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.common.recipe.RecipeSerializerBase
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredientHelper
import com.google.gson.JsonObject
import net.beholderface.ephemera.casting.patterns.spells.OpDatapackFunction
import net.beholderface.ephemera.registry.EphemeraPatternRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.lang.IllegalArgumentException

class DataSpellFakeRecipe(val identifier: Identifier, val functionString : String, val assignedSpell : OpDatapackFunction, val mediaCost : Int, val argc : Int*/
/*, val argTypes : List<IotaType<*>?>, val enforceVecAmbit : Boolean*//*
) : Recipe<Inventory> {
    override fun matches(inventory: Inventory, world: World) = false

    fun matches(spell : OpDatapackFunction) = this.assignedSpell == spell

    override fun craft(inventory: Inventory?): ItemStack = ItemStack.EMPTY

    override fun fits(width: Int, height: Int) = false

    override fun getOutput() : ItemStack = ItemStack.EMPTY.copy()

    override fun getId() = identifier

    override fun getSerializer(): RecipeSerializer<*>  = EphemeraRecipeSerializer.DATA_SPELL

    override fun getType(): RecipeType<*> = EphemeraRecipeTypes.DATA_SPELL_TYPE

    class Serializer : RecipeSerializerBase<DataSpellFakeRecipe>() {
        override fun read(recipeID: Identifier, json: JsonObject): DataSpellFakeRecipe {
            val functionString = JsonHelper.getString(json, "function")
            val cost = JsonHelper.getInt(json, "mediaCost")
            val assignedSpellID = JsonHelper.getInt(json, "spellID")
            val assignedSpell = PatternRegistry.lookupPatternByShape(EphemeraPatternRegistry.DATAPACK_SPELLS[assignedSpellID]) as OpDatapackFunction
            val argc = JsonHelper.getInt(json, "argc")
            if (assignedSpell.argc != argc){
                throw IllegalArgumentException("Number of arguments specified in json does not match the number of arguments accepted by datapack spell #$assignedSpellID.")
            }
            return DataSpellFakeRecipe(recipeID, functionString, assignedSpell, cost, argc)
        }

        override fun write(buf: PacketByteBuf, recipe: DataSpellFakeRecipe) {
            buf.writeString(recipe.functionString)
            buf.writeIdentifier(PatternRegistry.lookupPattern(recipe.assignedSpell))
            buf.writeInt(recipe.mediaCost)
            buf.writeInt(recipe.argc)
        }

        override fun read(recipeID: Identifier, buf: PacketByteBuf): DataSpellFakeRecipe {
            val function = buf.readString()
            val assignedSpell = PatternRegistry.lookupPattern(buf.readIdentifier()).action as OpDatapackFunction
            val cost = buf.readInt()
            val argc = buf.readInt()
            return DataSpellFakeRecipe(recipeID, function, assignedSpell, cost, argc)
        }
    }
}*/
