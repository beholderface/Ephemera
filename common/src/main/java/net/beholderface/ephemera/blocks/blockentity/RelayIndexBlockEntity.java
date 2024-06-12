package net.beholderface.ephemera.blocks.blockentity;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.beholderface.ephemera.registry.EphemeraBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RelayIndexBlockEntity extends BlockEntity {
    private Iota storedIota = new NullIota();
    public RelayIndexBlockEntity(BlockPos pos, BlockState state) {
        super(EphemeraBlockRegistry.RELAY_INDEX_ENTITY.get(), pos, state);
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
        NBTHelper.put(nbt, "StoredIota", HexIotaTypes.serialize(this.storedIota));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try {
            if (!this.world.isClient){
                this.storedIota = HexIotaTypes.deserialize(nbt.getCompound("StoredIota"), (ServerWorld) this.world);
            }
        } catch (NullPointerException ex){
            //nothing
        }
    }
}
