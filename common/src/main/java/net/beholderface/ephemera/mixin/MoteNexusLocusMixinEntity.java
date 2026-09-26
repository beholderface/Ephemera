package net.beholderface.ephemera.mixin;

import net.beholderface.ephemera.blocks.blockentity.EnergizedBlockEntityInterface;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ram.talia.hexal.common.blocks.entity.BlockEntityMediafiedStorage;

@Mixin(BlockEntityMediafiedStorage.class)
public class MoteNexusLocusMixinEntity implements EnergizedBlockEntityInterface {

    @Unique protected boolean ephemera$isEnergized = false;
    @Unique private static final String ephemera$energizedTag = "ephemera:energized";

    public boolean ephemera$isEnergized() {
        return ephemera$isEnergized;
    }
    public void ephemera$setEnergized(boolean newValue){
        this.ephemera$isEnergized = newValue;
    }

    @Inject(method = "saveModData", at = @At(value = "HEAD", remap = false), remap = false)
    protected void saveEnergized(NbtCompound tag, CallbackInfo ci){
        tag.putBoolean(ephemera$energizedTag, this.ephemera$isEnergized);
    }

    @Inject(method = "loadModData", at = @At(value = "HEAD", remap = false), remap = false)
    protected void loadEnergized(NbtCompound tag, CallbackInfo ci){
        if (tag.contains(ephemera$energizedTag)){
            this.ephemera$isEnergized = tag.getBoolean(ephemera$energizedTag);
        }
    }
}
