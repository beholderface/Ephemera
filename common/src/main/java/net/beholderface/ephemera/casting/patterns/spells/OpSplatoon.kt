package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.api.utils.getUUID
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.beholderface.ephemera.api.toVec3i
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.beholderface.ephemera.casting.mishaps.MishapNonconjured
import net.beholderface.ephemera.casting.ISplatoonableBlock;
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import ram.talia.moreiotas.api.casting.iota.ItemTypeIota

class OpSplatoon : SpellAction {
    override val argc = 2
    //override val mediaCost = 1 * MediaConstants.DUST_UNIT
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getVec3(0, argc)
        val colorItem = args[1]
        var colorItemFinal = Items.BARRIER
        var costMultiplier = 1.0f

        env.assertVecInRange(target)
        val target3i = target.toVec3i()
        if (env.world.getBlockState(BlockPos(target3i)).block !is BlockConjured
            && !ISplatoonableBlock.isSplatable(env.world.getBlockState(BlockPos(target3i)).block)){
            throw MishapNonconjured.of(BlockPos(target3i), env.world)
        }

        if (colorItem.type == ItemTypeIota.TYPE){
            if (IXplatAbstractions.INSTANCE.isPigment((colorItem as ItemTypeIota).item?.defaultStack)){
                colorItemFinal = colorItem.item
            }
        }
        val targetBEData = env.world.getBlockEntity(BlockPos(target3i))?.createNbtWithIdentifyingData()
        val targetBlock = env.world.getBlockState(BlockPos(target3i)).block
        if (targetBlock is BlockConjured){
            if (targetBEData.getCompound("tag_colorizer").getUUID("owner") == env.castingEntity?.uuid){
                costMultiplier = 0.1f
            }
        } else if (targetBlock == Registries.BLOCK.get(Identifier.tryParse("oneironaut:wisp_lantern"))){
            if (targetBEData.getCompound("color").getUUID("owner") == env.castingEntity?.uuid){
                costMultiplier = 0.1f
            }
        }
        //ctx.caster.sendMessage(Text.of(costMultiplier.toString()))

        return SpellAction.Result(
            Spell(BlockPos(target3i), colorItemFinal),
            (MediaConstants.DUST_UNIT * costMultiplier).toLong(),
            listOf(ParticleSpray.cloud(target, 2.0))
        )
    }

    private data class Spell(val target: BlockPos, val colorizer : Item?) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            var appliedColor = env.pigment
            if (colorizer != Items.BARRIER){
                appliedColor = FrozenPigment(colorizer?.defaultStack, env.castingEntity?.uuid)
            }
            if (env.world.getBlockState(target).block is BlockConjured) {
                    BlockConjured.setColor(env.world, target, appliedColor)
            } else if (ISplatoonableBlock.isSplatable(env.world.getBlockState(target).block)){
                ISplatoonableBlock.splatBlock(env.world, target, appliedColor)
                env.world.getBlockEntity(target)?.markDirty()
                env.world.updateListeners(target, env.world.getBlockState(target), env.world.getBlockState(target), 0b10)
                //hehe janky workaround go brr
                /*val prevItemstack = ctx.caster.getStackInHand(Hand.MAIN_HAND)
                val colorizerStack = colorizer?.defaultStack
                ctx.caster.setStackInHand(Hand.MAIN_HAND, colorizerStack)
                ctx.world.getBlockState(target).onUse(ctx.world, ctx.caster, Hand.MAIN_HAND,
                    BlockHitResult(Vec3d(target.x.toDouble() + 0.5, target.y.toDouble() + 0.3, target.z.toDouble() + 0.5), Direction.DOWN, target, true))
                ctx.caster.setStackInHand(Hand.MAIN_HAND, prevItemstack)
                //ctx.world.sendPacket(ctx.world.getBlockEntity(target)?.toUpdatePacket())
                //ctx.caster.sendChunkPacket(ChunkPos(target), ctx.world.getBlockEntity(target)?.toUpdatePacket())
                ctx.world.updateListeners(target, ctx.world.getBlockState(target), ctx.world.getBlockState(target), 0b10)*/
                /*val lantern : WispLanternEntity = ctx.world.getBlockEntity(target) as WispLanternEntity
                lantern.setColor(colorizer?.defaultStack, ctx.caster)
                lantern.markDirty()*/
            }
        }
    }
}