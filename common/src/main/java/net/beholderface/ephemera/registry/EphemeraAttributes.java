package net.beholderface.ephemera.registry;

import net.beholderface.ephemera.Ephemera;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class EphemeraAttributes
{
    private static final Map<Identifier, EntityAttribute> ATTRIBUTES = new LinkedHashMap<>();
    public static void register(BiConsumer<EntityAttribute, Identifier> r) {
        for (var e : ATTRIBUTES.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    public static final EntityAttribute BREAK_TIER_BOOST = make("breakboost", new ClampedEntityAttribute(Ephemera.MOD_ID + ".attributes.breakboost",
            0.0, -64.0, 64.0));

    private static <T extends EntityAttribute> T make(String id, T attr) {
        var old = ATTRIBUTES.put(Ephemera.id(id), attr);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + id);
        }
        return attr;
    }
}
