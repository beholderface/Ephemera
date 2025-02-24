package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.*
import net.beholderface.ephemera.api.toVec3d
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.util.math.Direction
import ram.talia.moreiotas.api.casting.iota.StringIota
import ram.talia.moreiotas.api.getString
import java.util.*

class OpGetPropertyValue : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getBlockPos(0, argc)
        val state = env.world.getBlockState(target)
        val key = args.getString(1, argc).lowercase(Locale.getDefault())
        for (property in state.properties){
            //Ephemera.LOGGER.info("Property name: " + property.name)
            if (property.name.equals(key)){
                val output : Iota = if (property is BooleanProperty){
                    BooleanIota(state.get(property))
                } else if (property is IntProperty){
                    DoubleIota((state.get(property) as Int).toDouble())
                } else if (property is DirectionProperty){
                    Vec3Iota((state.get(property) as Direction).vector.toVec3d())
                } else if (property is EnumProperty){
                    StringIota.make(state.get(property).name)
                } else {
                    //what kind of fucked up property is some other mod adding?
                    StringIota.make(state.get(property).toString())
                }
                return listOf(output)
            }
        }
        return listOf(NullIota())
    }

}