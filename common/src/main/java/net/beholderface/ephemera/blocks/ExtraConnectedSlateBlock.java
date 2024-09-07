package net.beholderface.ephemera.blocks;

import at.petrak.hexcasting.annotations.SoftImplement;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import at.petrak.hexcasting.common.lib.HexItems;
import net.beholderface.ephemera.blocks.blockentity.ExtraConnectedSlateBlockEntity;
import net.beholderface.ephemera.registry.EphemeraItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ExtraConnectedSlateBlock extends BlockSlate {
    public ExtraConnectedSlateBlock(Settings settings) {
        super(settings);
    }

    /*@Override
    public boolean canEnterFromDirection(Direction enterDir, Direction normalDir, BlockPos pos, BlockState bs, World world) {
        var face = bs.get(ATTACH_FACE);
        if (face == WallMountLocation.WALL){
            return enterDir != bs.get(FACING);
        } else if (face == WallMountLocation.CEILING){
            return enterDir != Direction.DOWN;
        } else {
            return enterDir != Direction.UP;
        }
    }*/

    @Override
    public EnumSet<Direction> exitDirections(BlockPos pos, BlockState bs, World world) {
        var allDirs = EnumSet.allOf(Direction.class);
        var normal = this.normalDir(pos, bs, world);
        allDirs.remove(normal);
        return allDirs;
    }

    @Override
    @SoftImplement("forge")
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockView level, BlockPos pos,
                                       PlayerEntity player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlockEntitySlate slate) {
            ItemStack stack = new ItemStack(EphemeraItemRegistry.SNEAKY_SLATE.get());
            if (slate.pattern != null) {
                EphemeraItemRegistry.SNEAKY_SLATE.get().writeDatum(stack, new PatternIota(slate.pattern));
            }
            return stack;
        }

        return new ItemStack(this);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new ExtraConnectedSlateBlockEntity(pPos, pState);
    }
}
