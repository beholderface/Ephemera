package net.beholderface.ephemera.blocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RelayTPDetectorBlock extends Block {
    public RelayTPDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(Properties.POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.POWERED);
        builder.add(Properties.FACING);
    }

    public void notifyTeleport(BlockState state, World world, BlockPos pos){
        world.setBlockState(pos, state.with(Properties.POWERED, true));
        world.createAndScheduleBlockTick(pos, this, 20);
    }
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        //hopefully this makes it not turn off prematurely if there are two closely-timed teleports
        if (!world.getBlockTickScheduler().isQueued(pos, this)){
            world.setBlockState(pos, state.with(Properties.POWERED, false));
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.getRedstoneOutput(state);
    }
    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.getRedstoneOutput(state);
    }

    public int getRedstoneOutput(BlockState state){
        return state.get(Properties.POWERED) ? 15 : 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext pContext) {
        return this.getDefaultState().with(Properties.FACING, pContext.getSide());
    }
    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        return VoxelShapes.cuboid(VoxelShapes.fullCube().getBoundingBox().contract(0.25f));
    }
}
