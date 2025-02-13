package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.beholderface.ephemera.casting.mishaps.MishapNoThoth

class OpThothYoink : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        var output : Iota = NullIota()
        val stack = image.stack.toMutableList()
        val frame = continuation.findThothFrame()
        if (frame != null){
            val accumulator = frame.acc
            if (accumulator.isNotEmpty()){
                output = accumulator.removeLast()
            }
        } else {
            throw MishapNoThoth()
        }
        stack.add(output)
        val image2 = image.withUsedOp().copy(stack = stack)
        return OperationResult(image2, listOf(), continuation, HexEvalSounds.NORMAL_EXECUTE)
    }
}

fun SpellContinuation.findThothFrame() : FrameForEach? {
    //largely based on the relevant code in OpModifyThoth from Overevaluate
    var newContinuation = this
    var forEach : FrameForEach? = null
    while (newContinuation is SpellContinuation.NotDone){
        val frame = newContinuation.frame
        if (frame is FrameForEach){
            forEach = frame
            break
        }
        newContinuation = newContinuation.next
    }
    return forEach
}