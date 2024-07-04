package net.beholderface.ephemera.blocks.blockentity;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.api.MiscAPIKt;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RelayIndexBlockEntity extends BlockEntity {
    private Iota storedIota = new NullIota();
    private static final String STORAGE_TAG = "StoredIota";
    private static final String WORLD_TAG = "savedworld";

    private static final boolean debugMessages = false;

    public RelayIndexBlockEntity(BlockPos pos, BlockState state) {
        super(EphemeraBlockRegistry.RELAY_INDEX_ENTITY.get(), pos, state);
        if (this.world == null && Ephemera.getCachedServer() != null){
            this.world = Ephemera.getCachedServer().getOverworld();
        }
    }

    public Iota getStoredIota() {
        return storedIota;
    }
    public boolean setStoredIota(Iota iota, PlayerEntity player){
        PlayerEntity foundPlayer = MishapOthersName.getTrueNameFromDatum(iota, player);
        if (foundPlayer != null){
            Ephemera.boolLogger("Found a truename", debugMessages);
            return false;
        } else {
            Ephemera.boolLogger("No truename found, setting iota", debugMessages);
            this.storedIota = iota;
            return true;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NBTHelper.putCompound(nbt, STORAGE_TAG, HexIotaTypes.serialize(this.storedIota));
        if (this.world != null){
            Ephemera.boolLogger("Nonnull world, recording registry key", debugMessages);
            nbt.putString(WORLD_TAG, this.world.getRegistryKey().getValue().toString());
        } else {
            Ephemera.boolLogger("World is null, recording ???", debugMessages);
            nbt.putString(WORLD_TAG, "???");
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        String worldString = nbt.getString(WORLD_TAG);
        if (!worldString.equals("???")){
            Ephemera.boolLogger("not ???", debugMessages);
            this.world = MiscAPIKt.stringToWorld(new Identifier(worldString));
        } else {
            Ephemera.boolLogger("???", debugMessages);
            this.world = null;
        }
        try {
            if (this.world != null && !this.world.isClient){
                Ephemera.boolLogger("Attempting to deserialize stored iota", debugMessages);
                this.storedIota = HexIotaTypes.deserialize(nbt.getCompound(STORAGE_TAG), (ServerWorld) this.world);
            } else {
                Ephemera.boolLogger("Can't even try to deserialize iota", debugMessages);
                this.storedIota = new NullIota();
            }
        } catch (NullPointerException ex){
            Ephemera.LOGGER.info("NULL POINTER WOOOOOOO");
            //nothing
        }
    }
}
