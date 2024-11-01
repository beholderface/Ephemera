package net.beholderface.ephemera.blocks;

import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.EnumSet;

public class RedstoneSlateBlock extends BlockSlate {
    private final static BooleanProperty REDSTONE_POWERED = Properties.POWERED;
    public RedstoneSlateBlock(Settings p_53182_) {
        super(p_53182_);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, ATTACH_FACE, WATERLOGGED, REDSTONE_POWERED);
    }

    @Override
    public boolean canEnterFromDirection(Direction enterDir, Direction normalDir, BlockPos pos, BlockState bs,
                                         World world) {
        var thisNormal = this.normalDir(pos, bs, world);
        return enterDir != thisNormal && normalDir == thisNormal && bs.get(REDSTONE_POWERED);
    }

    @Override
    public EnumSet<Direction> exitDirections(BlockPos pos, BlockState bs, World world) {
        if (!bs.get(REDSTONE_POWERED)){
            return EnumSet.noneOf(Direction.class);
        }
        var allDirs = EnumSet.allOf(Direction.class);
        var normal = this.normalDir(pos, bs, world);
        allDirs.remove(normal);
        allDirs.remove(normal.getOpposite());
        return allDirs;
    }

    @Override
    public void neighborUpdate(BlockState pState, World pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
                               boolean pIsMoving) {
        super.neighborUpdate(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

        if (!pLevel.isClient) {
            boolean currentlyPowered = pState.get(REDSTONE_POWERED);
            if (currentlyPowered != pLevel.isReceivingRedstonePower(pPos)) {
                pLevel.setBlockState(pPos, pState.with(REDSTONE_POWERED, !currentlyPowered), 2);
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext pContext) {
        BlockState output = this.getDefaultState().with(REDSTONE_POWERED, pContext.getWorld().isReceivingRedstonePower(pContext.getBlockPos()));
        if (pContext.getPlayer() != null){
            return output.with(Properties.FACING, !pContext.getPlayer().isSneaking() ? pContext.getSide() : pContext.getSide().getOpposite());
        } else {
            return output.with(Properties.FACING, pContext.getSide());
        }
    }
}
