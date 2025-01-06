package net.beholderface.ephemera.blocks;

import at.petrak.hexcasting.annotations.SoftImplement;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import net.beholderface.ephemera.blocks.blockentity.ExtraConnectedSlateBlockEntity;
import net.beholderface.ephemera.registry.EphemeraItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class InertSlateBlock extends BlockSlate {
    public InertSlateBlock(Settings settings) {
        super(settings);
        //this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACH_FACE, WATERLOGGED, BlockSlate.ENERGIZED);
    }

    @Override
    public boolean canEnterFromDirection(Direction enterDir, Direction normalDir, BlockPos pos, BlockState bs, World world) {
        return false;
    }

    @Override
    public EnumSet<Direction> exitDirections(BlockPos pos, BlockState bs, World world) {
        return EnumSet.noneOf(Direction.class);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }

    @Override
    public @Nullable
    HexPattern getPattern(BlockPos pos, BlockState bs, World world) {
        return null;
    }
}
