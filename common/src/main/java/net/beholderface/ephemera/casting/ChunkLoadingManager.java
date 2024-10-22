package net.beholderface.ephemera.casting;

import at.petrak.hexcasting.api.utils.NBTHelper;
import net.beholderface.ephemera.Ephemera;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkLoadingManager extends PersistentState {
    private static final List<ChunkLoadingEntry> entries = new ArrayList<>();
    public static final String POS_KEY = "pos";
    public static final String RADIUS_KEY = "radius";
    public static final String WORLD_KEY = "world";
    public static final String TIMESTAMP_KEY = "timestamp";
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (ChunkLoadingEntry entry : entries){
            if (!entry.isExpired()){
                NbtCompound entryCompound = new NbtCompound();
                NbtCompound posCompound = new NbtCompound();
                posCompound.putInt("x", entry.pos.x);
                posCompound.putInt("z", entry.pos.z);
                NBTHelper.putCompound(entryCompound, POS_KEY, posCompound);
                entryCompound.putInt(RADIUS_KEY, entry.radius);
                entryCompound.putString(WORLD_KEY, entry.world.getRegistryKey().getValue().toString());
                entryCompound.putLong(TIMESTAMP_KEY, entry.expiration);
                NBTHelper.putCompound(nbt, entry.uuid.toString(), entryCompound);
            }
        }
        return nbt;
    }

    public static ChunkLoadingManager createFromNbt(NbtCompound nbt){
        ChunkLoadingManager manager = new ChunkLoadingManager();
        for (String key : nbt.getKeys()){
            NbtCompound nbtEntry = nbt.getCompound(key);
            ServerWorld foundWorld = null;
            for (ServerWorld world : Ephemera.getCachedServer().getWorlds()){
                if (world.getRegistryKey().getValue().equals(Identifier.tryParse(nbtEntry.getString(WORLD_KEY)))){
                    foundWorld = world;
                }
            }
            if (foundWorld != null){
                NbtCompound posCompound = nbtEntry.getCompound(POS_KEY);
                ChunkPos pos = new ChunkPos(posCompound.getInt("x"), posCompound.getInt("z"));
                int radius = nbtEntry.getInt(RADIUS_KEY);
                long timestamp = nbtEntry.getLong(TIMESTAMP_KEY);
                UUID uuid = UUID.fromString(key);
                ChunkLoadingEntry entry = new ChunkLoadingEntry(pos, radius, foundWorld, timestamp, uuid);
                entry.setForced(true);
                entries.add(entry);
            }
        }
        Ephemera.LOGGER.info(entries.size() + " chunkloading entries reconstructed from NBT.");
        return manager;
    }

    public static ChunkLoadingManager getServerState(MinecraftServer server){
        PersistentStateManager stateManager = server.getOverworld().getPersistentStateManager();
        ChunkLoadingManager loadingManager = stateManager.getOrCreate(ChunkLoadingManager::createFromNbt, ChunkLoadingManager::new, Ephemera.MOD_ID);
        loadingManager.markDirty();
        return loadingManager;
    }

    public static void tick(){
        List<ChunkLoadingEntry> entriesToDelete = new ArrayList<>();
        List<ChunkLoadingEntry> entriesToRefresh = new ArrayList<>();
        for (ChunkLoadingEntry entry : entries){
            if (entry.isExpired()){
                entry.setForced(false);
                entriesToDelete.add(entry);
            } else {
                entriesToRefresh.add(entry);
            }
        }
        for (ChunkLoadingEntry entry : entriesToRefresh){
            entry.setForced(true);
        }
        for (ChunkLoadingEntry entry : entriesToDelete){
            entries.remove(entry);
        }
    }

    public static ChunkLoadingEntry createEntry(ChunkPos pos, ServerWorld world, int lifetime){
        return createEntry(pos, world, lifetime, 0);
    }
    public static ChunkLoadingEntry createEntry(ChunkPos pos, ServerWorld world, int lifetime, int radius){
        ChunkLoadingEntry entry = new ChunkLoadingEntry(pos, radius, world, world.getTime() + lifetime);
        entries.add(entry);
        return entry;
    }
}
