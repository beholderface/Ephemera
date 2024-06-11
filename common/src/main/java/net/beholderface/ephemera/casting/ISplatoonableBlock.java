package net.beholderface.ephemera.casting;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Method;
//sam is cool
public interface ISplatoonableBlock {
    // soft implemented on blocks in other mods that can be splatted
    public void splatPigmentOntoBlock(World world, BlockPos pos, FrozenColorizer pigment);

    // use reflection to check if the block has the method
    public static boolean isSplatable(Block block){
        try {
            block.getClass().getMethod("splatPigmentOntoBlock", World.class, BlockPos.class, FrozenColorizer.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    // use reflection to actually call the method on the block
    public static void splatBlock(World world, BlockPos pos, FrozenColorizer pigment){
        Block block = world.getBlockState(pos).getBlock();
        try {
            Method splatMethod = block.getClass().getMethod("splatPigmentOntoBlock", World.class, BlockPos.class, FrozenColorizer.class);
            splatMethod.invoke(block, world, pos, pigment);
        } catch (Exception e) {

        }
    }
}