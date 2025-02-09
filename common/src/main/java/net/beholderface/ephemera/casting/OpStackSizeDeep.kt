package net.beholderface.ephemera.casting

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds

class OpStackSizeDeep : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        var count = 0
        val stack = image.stack.toMutableList()
        for (iota in stack){
            count += iota.size()
        }
        stack.add(DoubleIota(count.toDouble()))
        val image2 = image.withUsedOp().copy(stack = stack)
        return OperationResult(image2, listOf(), continuation, HexEvalSounds.NORMAL_EXECUTE)
    }
}