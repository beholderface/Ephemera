package net.beholderface.ephemera.blocks.blockentity;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registries;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.api.MiscAPIKt;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class RelayIndexBlockEntity extends BlockEntity {
    private Iota storedIota = new NullIota();
    private static final String STORAGE_TAG = "StoredIota";
    private static final String WORLD_TAG = "savedworld";
    public RelayIndexBlockEntity(BlockPos pos, BlockState state) {
        super(EphemeraBlockRegistry.RELAY_INDEX_ENTITY.get(), pos, state);
        if (this.world == null){
            this.world = Ephemera.getCachedServer().getOverworld();
        }
    }

    public Iota getStoredIota() {
        return storedIota;
    }
    public boolean setStoredIota(Iota iota, PlayerEntity player){
        PlayerEntity foundPlayer = MishapOthersName.getTrueNameFromDatum(iota, player);
        if (foundPlayer != null){
            return false;
        } else {
            this.storedIota = iota;
            return true;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NBTHelper.put(nbt, STORAGE_TAG, HexIotaTypes.serialize(this.storedIota));
        nbt.putString(WORLD_TAG, this.world.getRegistryKey().getValue().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.world = MiscAPIKt.stringToWorld(new Identifier(nbt.getString(WORLD_TAG)));
        if (this.world != null){
            Ephemera.LOGGER.info("Nonnull world " + this.world.asString());
        } else {
            Ephemera.LOGGER.info("Null world");
        }
        try {
            if (this.world != null && !this.world.isClient){

                this.storedIota = HexIotaTypes.deserialize(nbt.getCompound(STORAGE_TAG), (ServerWorld) this.world);
            }
        } catch (NullPointerException ex){
            Ephemera.LOGGER.info("NULL POINTER WOOOOOOO");
            //nothing
        }
    }
}
