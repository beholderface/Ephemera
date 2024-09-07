package net.beholderface.ephemera.blocks.blockentity;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.blocks.circles.BlockEntitySlate;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ExtraConnectedSlateBlockEntity extends HexBlockEntity {
    public ExtraConnectedSlateBlockEntity(BlockPos pos, BlockState state) {
        super(EphemeraBlockRegistry.SNEAKY_SLATE_ENTITY.get(), pos, state);
    }


    public static final String TAG_PATTERN = "pattern";

    @Nullable
    public HexPattern pattern;

    @Override
    protected void saveModData(NbtCompound tag) {
        if (this.pattern != null) {
            tag.put(TAG_PATTERN, this.pattern.serializeToNBT());
        } else {
            tag.put(TAG_PATTERN, new NbtCompound());
        }
    }

    @Override
    protected void loadModData(NbtCompound tag) {
        if (tag.contains(TAG_PATTERN, NbtElement.COMPOUND_TYPE)) {
            NbtCompound patternTag = tag.getCompound(TAG_PATTERN);
            if (HexPattern.isPattern(patternTag)) {
                this.pattern = HexPattern.fromNBT(patternTag);
            } else {
                this.pattern = null;
            }
        } else {
            this.pattern = null;
        }
    }
}
