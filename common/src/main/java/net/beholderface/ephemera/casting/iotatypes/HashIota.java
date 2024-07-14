package net.beholderface.ephemera.casting.iotatypes;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.beholderface.ephemera.api.MiscAPIKt;
import net.beholderface.ephemera.registry.EphemeraIotaTypeRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class HashIota extends Iota {

    public static final String HASH_KEY = "hash_key";

    public HashIota(@NotNull NbtElement payload) {
        super(EphemeraIotaTypeRegistry.HASH, MiscAPIKt.hash(payload.toString()));
    }

    public HashIota(@NotNull String payload) {
        super(EphemeraIotaTypeRegistry.HASH, payload);
    }

    public static HashIota of(Iota iota){
        NbtCompound container = new NbtCompound();
        container.put("iotaData", iota.serialize());
        Identifier id = HexIotaTypes.REGISTRY.getId(iota.getType());
        assert id != null;
        container.putString("iotaType", id.toString());
        return new HashIota(container);
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        if (that instanceof HashIota hashIota){
            return hashIota.getHashString().equals(this.getHashString());
        }
        return false;
    }

    @Override
    public @NotNull NbtElement serialize() {
        var data = new NbtCompound();
        var payload = this.getHashString();
        data.putString(HASH_KEY, payload);
        return data;
    }

    public @NotNull String getHashString(){
        return (String) this.payload;
    }

    public static IotaType<HashIota> TYPE = new IotaType<>() {
        @Override
        public HashIota deserialize(NbtElement tag, ServerWorld world) throws IllegalArgumentException {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            String hashData = ctag.getString(HASH_KEY);
            return new HashIota(hashData);
        }

        @Override
        public Text display(NbtElement tag) {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            String hash = ctag.getString(HASH_KEY);
            Text hashText = Text.translatable("ephemera.iota.hashlabel", hash);
            Style originalStyle = hashText.getStyle();
            byte[] bytes = hash.getBytes();
            int color = ByteBuffer.wrap(Arrays.copyOf(bytes, 4)).getInt();
            Style formattedStyle = originalStyle.withColor((color + 0xFFFFFF) / 2);
            return hashText.copy().setStyle(formattedStyle);
        }
        @Override
        public int color() {
            return 0xff_990000;
        }
    };
}
