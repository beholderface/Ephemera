package net.beholderface.ephemera.casting.patterns

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.TreeList
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.beholderface.ephemera.casting.mishaps.MishapNoThoth
import java.util.LinkedList
import java.util.Queue

class OpThothYoink : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        var output : Iota = NullIota()
        val stack = image.stack.toMutableList()
        //get thoth frame and queue of frames until thoth frame
        val frameData = continuation.findFramesUntilThoth()
        var newContinuation : SpellContinuation.NotDone
        if (frameData.found()){
            newContinuation = frameData.foundFrame!!
            val frame = newContinuation.frame as FrameForEach
            val mutableAccumulator = frame.immutableAcc.toMutableList()
            //pop from accumulator stack
            if (mutableAccumulator.isNotEmpty()){
                output = mutableAccumulator.removeLast()
                //replace original thoth frame with new frame with popped accumulator stack
                val newImmutable = TreeList.from(mutableAccumulator)
                newContinuation = newContinuation.copy(frame = frame.copy(immutableAcc = newImmutable))
                //put old frames back
                while (!frameData.isQueueEmpty()){
                    newContinuation = newContinuation.pushFrame(frameData.remove()!!.frame) as SpellContinuation.NotDone
                }
            }
        } else {
            throw MishapNoThoth()
        }
        stack.add(output)
        val image2 = image.withUsedOp().copy(stack = stack)
        return OperationResult(image2, listOf(), newContinuation, HexEvalSounds.NORMAL_EXECUTE)
    }
}

fun SpellContinuation.findFramesUntilThoth() : FoundFrameData {
    var newContinuation = this
    val frameData = FoundFrameData()
    while (newContinuation is SpellContinuation.NotDone){
        val frame = newContinuation
        if (frame.frame is FrameForEach){
            frameData.foundFrame = frame
            break
        } else {
            frameData.add(newContinuation)
        }
        newContinuation = newContinuation.next
    }
    return frameData
}

//the old version of the frame-finding code, that does not help much with actually modifying stuff in 0.11.4
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



class FoundFrameData {
    var foundFrame : SpellContinuation.NotDone? = null
    private val otherFrames : Queue<SpellContinuation.NotDone> = LinkedList()
    fun found() : Boolean = foundFrame != null
    fun add(cont : SpellContinuation.NotDone) {
        otherFrames.add(cont)
    }
    fun remove() : SpellContinuation.NotDone? {
        return if (!otherFrames.isEmpty()){
            otherFrames.remove()
        } else {
            null
        }
    }
    fun isQueueEmpty() = otherFrames.isEmpty()
}