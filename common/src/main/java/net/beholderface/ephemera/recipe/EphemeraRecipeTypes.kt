package net.beholderface.ephemera.recipe

import net.beholderface.ephemera.Ephemera.MOD_ID
import net.beholderface.ephemera.Ephemera.id
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.BiConsumer

class EphemeraRecipeTypes {
    companion object {
        //val debugMessages = false
        @JvmStatic
        fun registerTypes(r: BiConsumer<RecipeType<*>, Identifier>) {
            for ((key, value) in TYPES) {
                //Ephemera.boolLogger("Attempting to register type $value with key $key", debugMessages)
                r.accept(value, key)
            }
        }

        private val TYPES: MutableMap<Identifier, RecipeType<*>> = LinkedHashMap()

        var DATA_SPELL_TYPE: RecipeType<DataSpellFakeRecipe> = registerType("dataspell")

        private fun <T : Recipe<*>> registerType(name: String): RecipeType<T> {
            val type: RecipeType<T> = object : RecipeType<T> {
                override fun toString(): String {
                    return "$MOD_ID:$name"
                }
            }
            // never will be a collision because it's a new object
            TYPES[id(name)] = type
            //Ephemera.boolLogger("Attempting to register type $name, with id ${type.toString()}", debugMessages)
            return type
        }

        public fun <T> bind(registry: Registry<in T>): BiConsumer<T, Identifier> =
            BiConsumer<T, Identifier> { t, id -> Registry.register(registry, id, t) }
    }
}