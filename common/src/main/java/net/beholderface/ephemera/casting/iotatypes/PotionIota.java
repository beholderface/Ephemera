package net.beholderface.ephemera.casting.iotatypes;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.beholderface.ephemera.registry.EphemeraIotaTypeRegistry;
import net.beholderface.ephemera.registry.EphemeraMiscRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

//import java.awt.*;

public class PotionIota extends Iota {
    public PotionIota(@NotNull StatusEffect effect){
        super(EphemeraIotaTypeRegistry.POTION, effect);
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    protected boolean toleratesOther(Iota that) {
        if (that.getType().equals(this.type)){
            PotionIota other = (PotionIota) that;
            return this.payload.equals(other.payload);
        }
        return false;
    }

    public @NotNull NbtElement serialize() {
        //Ephemera.LOGGER.info("serializing potion iota");
        var data = new NbtCompound();
        var payload = (StatusEffect) this.payload;
        Identifier id = Registries.STATUS_EFFECT.getId(payload);
        String key = id != null ? id.toString() : "ephemera:missing";
        data.putString("potion_key", key);
        //Ephemera.LOGGER.info("serialized potion iota");
        return data;
    }
    public @NotNull StatusEffect getEffect(/*PotionIota iota*/){
        return (StatusEffect) this.payload;
    }
    public static IotaType<PotionIota> TYPE = new IotaType<>() {
        @Override
        public PotionIota deserialize(NbtElement tag, ServerWorld world) throws IllegalArgumentException {
            //Ephemera.LOGGER.info("deserializing potion iota");
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            //Iterator<StatusEffect> statusEffectIterator = Registries.STATUS_EFFECT.iterator();
            String potionString = ctag.getString("potion_key");
            var potionKey = RegistryKey.of(RegistryKeys.STATUS_EFFECT, Identifier.tryParse(potionString));
            StatusEffect foundEffect = Registries.STATUS_EFFECT.get(potionKey);
            if (foundEffect == null){
                foundEffect = EphemeraMiscRegistry.MISSING.get();
            }
            /*while (statusEffectIterator.hasNext()){
                currentEffect = statusEffectIterator.next();
                if (currentEffect.getTranslationKey().equals(potionKey)){
                    break;
                }
            }*/
            //Ephemera.LOGGER.info("deserialized potion iota");
            return new PotionIota(foundEffect);
        }

        @Override
        public Text display(NbtElement tag) {
            var ctag = HexUtils.downcast(tag, NbtCompound.TYPE);
            Identifier text = Identifier.tryParse(ctag.getString("potion_key"));
            StatusEffect effect = Registries.STATUS_EFFECT.get(text);
            String key = effect != null ? effect.getTranslationKey() : EphemeraMiscRegistry.MISSING.get().getTranslationKey();
            if (effect == null){
                effect = EphemeraMiscRegistry.MISSING.get();
            }
            Text translatedName = Text.translatable(key);
            Style originalStyle = translatedName.getStyle();
            Style formattedStyle = originalStyle.withColor(effect.getColor());
            return translatedName.copy().setStyle(formattedStyle);
        }
        @Override
        public int color() {
            return 0xff_5555FF;
        }
    };
}
