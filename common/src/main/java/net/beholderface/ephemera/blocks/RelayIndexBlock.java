package net.beholderface.ephemera.blocks;

import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.common.lib.HexSounds;
import net.beholderface.ephemera.blocks.blockentity.RelayIndexBlockEntity;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RelayIndexBlock extends BlockWithEntity {

    public RelayIndexBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RelayIndexBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getStackInHand(hand);
        if (heldStack.getItem() instanceof ItemFocus focus){
            RelayIndexBlockEntity be = (RelayIndexBlockEntity) world.getBlockEntity(pos);
            if (be != null){
                //if (!world.isClient){
                    be.setWorld(world);
                //}
                if (!world.isClient){
                    if (hand == Hand.OFF_HAND){
                        focus.writeDatum(heldStack, be.getStoredIota());
                    } else {
                        be.setStoredIota(focus.readIota(heldStack, (ServerWorld) world), player);
                    }
                }
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), HexSounds.ACTUALLY_CAST, SoundCategory.BLOCKS, 1f, 1f, true);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity rawBE = world.getBlockEntity(pos);
        if (rawBE != null && rawBE.getType() == EphemeraBlockRegistry.RELAY_INDEX_ENTITY.get()){
            RelayIndexBlockEntity be = (RelayIndexBlockEntity) rawBE;
            rawBE.setWorld(world);
        }
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
