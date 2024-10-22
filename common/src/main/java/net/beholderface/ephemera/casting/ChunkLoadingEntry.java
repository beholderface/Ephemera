package net.beholderface.ephemera.casting;

import net.beholderface.ephemera.Ephemera;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ChunkLoadingEntry {
    public final ChunkPos pos;
    public final int radius;
    public final ServerWorld world;
    //set this to a negative value to denote indefinite duration
    public long expiration;
    private boolean currentlyForced = false;
    public final UUID uuid;

    public static final ChunkTicketType<ChunkPos> WISP = ChunkTicketType.create("wisp", Comparator.comparingLong(ChunkPos::toLong), 20);

    public ChunkLoadingEntry(ChunkPos pos, int radius, ServerWorld world, long expiration) {
        this.pos = pos;
        this.radius = radius;
        this.world = world;
        this.expiration = expiration;
        this.uuid = UUID.randomUUID();
    }
    public ChunkLoadingEntry(ChunkPos pos, int radius, ServerWorld world, long expiration, UUID uuid){
        this.pos = pos;
        this.radius = radius;
        this.world = world;
        this.expiration = expiration;
        this.uuid = uuid;
    }

    public void setForced(boolean forced){
        ServerChunkManager manager = this.world.getChunkManager();
        int i = 0;
        for (ChunkPos chunk : this.includedChunks()){
            manager.setChunkForced(chunk, forced);
            i++;
        }
        //Ephemera.LOGGER.info("Setting " + i + " chunks forced state to " + forced);
        currentlyForced = forced;
    }

    public List<ChunkPos> includedChunks(){
        List<ChunkPos> output = new ArrayList<>();
        for (int x = radius * -1; x <=  radius; x++){
            for (int z = radius * -1; z <=  radius; z++){
                output.add(new ChunkPos(this.pos.x + x, this.pos.z + z));
            }
        }
        return output;
    }

    public boolean isExpired(){
        return this.expiration >= 0 && this.world.getTime() > expiration;
    }
    public boolean isActive(){
        return this.currentlyForced;
    }
}
