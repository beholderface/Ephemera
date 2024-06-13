package net.beholderface.ephemera.recipe;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

public class CopyProperties {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockState copyProperties(BlockState original, BlockState copyTo) {
        for (Property prop : original.getProperties()) {
            if (copyTo.contains(prop)) {
                copyTo = copyTo.with(prop, original.get(prop));
            }
        }

        return copyTo;
    }
}
