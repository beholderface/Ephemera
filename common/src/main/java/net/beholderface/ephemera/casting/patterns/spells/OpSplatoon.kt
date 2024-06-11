package net.beholderface.ephemera.casting.patterns.spells

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.api.utils.getUUID
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.beholderface.ephemera.casting.mishaps.MishapNonconjured
import ram.talia.hexal.api.spell.iota.ItemTypeIota
import net.beholderface.ephemera.casting.ISplatoonableBlock;
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class OpSplatoon : SpellAction {
    override val argc = 2
    //override val mediaCost = 1 * MediaConstants.DUST_UNIT
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>>? {
        val target = args.getVec3(0, argc)
        val colorItem = args[1]
        var colorItemFinal = Items.BARRIER
        var costMultiplier = 1.0f

        ctx.assertVecInRange(target)
        if (ctx.world.getBlockState(BlockPos(target)).block !is BlockConjured
            && !ISplatoonableBlock.isSplatable(ctx.world.getBlockState(BlockPos(target)).block)){
            throw MishapNonconjured.of(BlockPos(target))
        }

        if (colorItem.type == ItemTypeIota.TYPE){
            if (IXplatAbstractions.INSTANCE.isColorizer((colorItem as ItemTypeIota).item?.defaultStack)){
                colorItemFinal = colorItem.item
            }
        }
        val targetBEData = ctx.world.getBlockEntity(BlockPos(target))?.createNbtWithIdentifyingData()
        val targetBlock = ctx.world.getBlockState(BlockPos(target)).block
        if (targetBlock is BlockConjured){
            if (targetBEData.getCompound("tag_colorizer").getUUID("owner") == ctx.caster.uuid){
                costMultiplier = 0.1f
            }
        } else if (targetBlock == Registry.BLOCK.get(Identifier.tryParse("oneironaut:wisp_lantern"))){
            if (targetBEData.getCompound("color").getUUID("owner") == ctx.caster.uuid){
                costMultiplier = 0.1f
            }
        }
        //ctx.caster.sendMessage(Text.of(costMultiplier.toString()))

        return Triple(
            Spell(BlockPos(target), colorItemFinal),
            (MediaConstants.DUST_UNIT * costMultiplier).toInt(),
            listOf(ParticleSpray.cloud(target, 2.0))
        )
    }

    private data class Spell(val target: BlockPos, val colorizer : Item?) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            var appliedColor = IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)
            if (colorizer != Items.BARRIER){
                appliedColor = FrozenColorizer(colorizer?.defaultStack, ctx.caster.uuid)
            }
            if (ctx.world.getBlockState(target).block is BlockConjured) {
                    BlockConjured.setColor(ctx.world, target, appliedColor)
            } else if (ISplatoonableBlock.isSplatable(ctx.world.getBlockState(target).block)){
                ISplatoonableBlock.splatBlock(ctx.world, target, appliedColor)
                ctx.world.getBlockEntity(target)?.markDirty()
                ctx.world.updateListeners(target, ctx.world.getBlockState(target), ctx.world.getBlockState(target), 0b10)
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