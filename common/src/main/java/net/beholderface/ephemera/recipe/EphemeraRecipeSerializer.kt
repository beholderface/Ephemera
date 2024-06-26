package net.beholderface.ephemera.recipe

import net.beholderface.ephemera.Ephemera
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import java.util.function.BiConsumer

class EphemeraRecipeSerializer {
    companion object {
        @JvmStatic
        fun registerSerializers(r: BiConsumer<RecipeSerializer<*>, Identifier>) {
            for ((key, value) in SERIALIZERS) {
                r.accept(value, key)
            }
        }

        private val SERIALIZERS: MutableMap<Identifier, RecipeSerializer<*>> = LinkedHashMap()

        //val DATA_SPELL: RecipeSerializer<*> = register("dataspell", DataSpellFakeRecipe.Serializer())

        private fun <T : Recipe<*>?> register(name: String, rs: RecipeSerializer<T>): RecipeSerializer<T> {
            val old = SERIALIZERS.put(Ephemera.id(name), rs)
            require(old == null) { "Typo? Duplicate id $name" }
            return rs
        }
    }
}