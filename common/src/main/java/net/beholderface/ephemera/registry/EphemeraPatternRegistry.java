package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpPotionEffect;
import kotlin.Triple;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.beholderface.ephemera.Ephemera.id;

public class EphemeraPatternRegistry {
    public static List<Triple<HexPattern, Identifier, Action>> PATTERNS = new ArrayList<>();
    public static List<Triple<HexPattern, Identifier, Action>> PER_WORLD_PATTERNS = new ArrayList<>();
    // IMPORTANT: be careful to keep the registration calls looking like this, or you'll have to edit the hexdoc pattern regex.
    /*public static HexPattern CONGRATS = registerPerWor asdawdsawd ld(HexPattern.fromAngles("eed", HexDir.WEST), "congrats", new OpCongrats());
    public static HexPattern SIGNUM = regis dawdasdawd ter(HexPattern.fromAngles("edd", HexDir.NORTH_WEST), "signum", new OpSignum());*/

    public static HexPattern INVISIBILITY = registerPerWorld(HexPattern.fromAngles("qqqqqaewawaweqa", HexDir.SOUTH_WEST), "invisibility", new OpPotionEffect(
            StatusEffects.INVISIBILITY, (int)(MediaConstants.DUST_UNIT / 3), false, false, true));

    public static void init() {
        try {
            for (Triple<HexPattern, Identifier, Action> patternTriple : PATTERNS) {
                PatternRegistry.mapPattern(patternTriple.getFirst(), patternTriple.getSecond(), patternTriple.getThird());
            }
            for (Triple<HexPattern, Identifier, Action> patternTriple : PER_WORLD_PATTERNS) {
                PatternRegistry.mapPattern(patternTriple.getFirst(), patternTriple.getSecond(), patternTriple.getThird(), true);
            }
        } catch (PatternRegistry.RegisterPatternException e) {
            e.printStackTrace();
        }
    }

    private static HexPattern register(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, Identifier, Action> triple = new Triple<>(pattern, id(name), action);
        PATTERNS.add(triple);
        return pattern;
    }

    private static HexPattern registerPerWorld(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, Identifier, Action> triple = new Triple<>(pattern, id(name), action);
        PER_WORLD_PATTERNS.add(triple);
        return pattern;
    }
}
