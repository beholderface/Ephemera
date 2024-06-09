package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpPotionEffect;
import kotlin.Triple;
import net.beholderface.ephemera.casting.patterns.OpFilteredEntityRaycast;
import net.beholderface.ephemera.casting.patterns.OpFrameRotation;
import net.beholderface.ephemera.casting.patterns.math.OpGaussianRand;
import net.beholderface.ephemera.casting.patterns.spells.great.OpMageArmor;
import net.beholderface.ephemera.casting.patterns.status.*;
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
    public static HexPattern MAGE_ARMOR = registerPerWorld(HexPattern.fromAngles("qaweqqwqqewaqeqqqqqad", HexDir.NORTH_WEST), "magearmor", new OpMageArmor());

    public static HexPattern REMOVE_STATUS = register(HexPattern.fromAngles("eeeeedaqdewed", HexDir.SOUTH_WEST), "removestatus", new OpRemoveStatus());
    public static HexPattern GET_STATUS = register(HexPattern.fromAngles("qqqqqedwd", HexDir.SOUTH_WEST), "getstatus", new OpGetEffects());
    public static HexPattern GET_STATUS_CATEGORY = register(HexPattern.fromAngles("eeeeeqawa", HexDir.SOUTH_EAST), "getstatuscategory", new OpGetEffectCategory());
    public static HexPattern GET_STATUS_DURATION = register(HexPattern.fromAngles("qqqqqedwdwd", HexDir.SOUTH_WEST), "getstatusduration", new OpGetStatusDetail(false));
    public static HexPattern GET_STATUS_LEVEL = register(HexPattern.fromAngles("eeeeeqawawa", HexDir.SOUTH_EAST), "getstatuslevel", new OpGetStatusDetail(true));
    public static HexPattern GET_BY_STATUS = register(HexPattern.fromAngles("ewqqqqqwe", HexDir.EAST), "getbystatus", new OpGetEntitiesByStatus(false));
    public static HexPattern GET_BY_STATUS_INVERSE = register(HexPattern.fromAngles("qweeeeewq", HexDir.EAST), "getbystatusinverse", new OpGetEntitiesByStatus(true));
    public static HexPattern GET_BY_STATUS_SINGLE = register(HexPattern.fromAngles("eaeeeeeae", HexDir.EAST), "getbystatussingle", new OpGetEntityByStatus());

    public static HexPattern FILTERED_SCOUTS = register(HexPattern.fromAngles("wqded", HexDir.EAST), "filteredentityraycast", new OpFilteredEntityRaycast());
    public static HexPattern GAUSSIAN_RAND = register(HexPattern.fromAngles("eeeeq", HexDir.NORTH_EAST), "gaussianrand", new OpGaussianRand());

    public static HexPattern READ_FRAME_ROTATION = register(HexPattern.fromAngles("wwawwqwwawwaeae", HexDir.SOUTH_WEST), "readframerotation", new OpFrameRotation(0));
    public static HexPattern SET_FRAME_ROTATION = register(HexPattern.fromAngles("wwawwqwwawwaqdq", HexDir.SOUTH_WEST), "setframerotation", new OpFrameRotation(1));


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
