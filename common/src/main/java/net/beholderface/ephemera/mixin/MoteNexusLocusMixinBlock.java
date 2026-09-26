package net.beholderface.ephemera.mixin;

import at.petrak.hexcasting.api.casting.circles.ICircleComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.utils.NBTHelper;
import com.mojang.datafixers.util.Pair;
import net.beholderface.ephemera.api.config.EphemeraConfig;
import net.beholderface.ephemera.blocks.blockentity.EnergizedBlockEntityInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ram.talia.hexal.api.casting.iota.MoteIota;
import ram.talia.hexal.common.blocks.BlockMediafiedStorage;
import ram.talia.hexal.common.blocks.entity.BlockEntityMediafiedStorage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Mixin(BlockMediafiedStorage.class)
public class MoteNexusLocusMixinBlock implements ICircleComponent {

    @Unique
    private static final EnumSet<Direction> ephemera$horizontalDirections = EnumSet.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);

    @Override
    public ControlFlow acceptControlFlow(CastingImage image, CircleCastEnv env, Direction dir, BlockPos pos, BlockState state, ServerWorld world) {
        NbtCompound nbt = image.getUserData();
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof BlockEntityMediafiedStorage nexusEntity){
            NBTHelper.putUUID(nbt, MoteIota.TAG_TEMP_STORAGE, nexusEntity.getUuid());
        }
        List<Pair<BlockPos, Direction>> outputs = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (d != dir.getOpposite()) {
                outputs.add(this.exitPositionFromDirection(pos, d));
            }
        }
        return new ControlFlow.Continue(image, outputs);
    }

    @Override
    public boolean canEnterFromDirection(Direction direction, BlockPos blockPos, BlockState blockState, ServerWorld serverWorld) {
        if (!EphemeraConfig.getServer().getNexusLocusEnabled()){
            return false;
        }
        return ephemera$horizontalDirections.contains(direction);
    }

    @Override
    public EnumSet<Direction> possibleExitDirections(BlockPos blockPos, BlockState blockState, World world) {
        return ephemera$horizontalDirections;
    }

    @Override
    public BlockState startEnergized(BlockPos blockPos, BlockState blockState, World world) {
        BlockEntity entity = world.getBlockEntity(blockPos);
        if (entity instanceof EnergizedBlockEntityInterface locusEntity){
            locusEntity.ephemera$setEnergized(true);
        }
        return blockState;
    }

    @Override
    public boolean isEnergized(BlockPos blockPos, BlockState blockState, World world) {
        BlockEntity entity = world.getBlockEntity(blockPos);
        if (entity instanceof EnergizedBlockEntityInterface locusEntity){
            return locusEntity.ephemera$isEnergized();
        }
        return false;
    }

    @Override
    public BlockState endEnergized(BlockPos blockPos, BlockState blockState, World world) {
        BlockEntity entity = world.getBlockEntity(blockPos);
        if (entity instanceof EnergizedBlockEntityInterface locusEntity){
            locusEntity.ephemera$setEnergized(false);
        }
        return blockState;
    }
}
