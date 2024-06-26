package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpPotionEffect;
import kotlin.Triple;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.casting.patterns.OpFilteredEntityRaycast;
import net.beholderface.ephemera.casting.patterns.OpFrameRotation;
import net.beholderface.ephemera.casting.patterns.link.OpNetworkScan;
import net.beholderface.ephemera.casting.patterns.math.OpGaussianRand;
import net.beholderface.ephemera.casting.patterns.link.OpNetworkTeleport;
import net.beholderface.ephemera.casting.patterns.spells.OpDatapackFunction;
import net.beholderface.ephemera.casting.patterns.spells.OpParticleBurst;
import net.beholderface.ephemera.casting.patterns.spells.OpPlasma;
import net.beholderface.ephemera.casting.patterns.spells.great.OpMageArmor;
import net.beholderface.ephemera.casting.patterns.status.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.beholderface.ephemera.Ephemera.id;

public class EphemeraPatternRegistry {
    public static List<Triple<HexPattern, Identifier, Action>> PATTERNS = new ArrayList<>();
    public static List<Triple<HexPattern, Identifier, Action>> PER_WORLD_PATTERNS = new ArrayList<>();

    //when appended to aqaa, these suffixes produce a numerical reflection corresponding to their index in the array
    //most suffixes obtained via HexBug
    private static final String[] DATAPACK_SPELL_SUFFIXES = {
            "", "w", "wa", "edwd", "waa", "q", "edw", "waq", "waqw", "waaq",
            "e", "qaw", "qwa", "wqaw", "waaqq", "edaq", "qawq", "qwaq", "waaqa", "waaqe",
            "ee", "eaw", "qawa", "dweede", "qwaa", "eaq", "eaqw", "wqaaede", "waaqqa", "qwaaq",
            "eaqq", "eqaw"
    };
    private static int index = 0;
    private static int getIndex(){
        int output = index;
        index++;
        return output;
    }

    /*public static HexPattern[] DATAPACK_SPELLS;
    static {
        ArrayList<HexPattern> spells = new ArrayList<>();
        ArrayList<Integer> argCounts = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            argCounts.add(0);
        }
        for (int i = 0; i < 10; i++){
            argCounts.add(1);
        }
        for (int i = 0; i < 5; i++){
            argCounts.add(2);
        }
        for (int i = 0; i < 3; i++){
            argCounts.add(3);
        }
        for (int i = 0; i < 2; i++){
            argCounts.add(4);
        }
        for (int i = 0; i < 2; i++){
            argCounts.add(5);
        }
        int i = 0;
        for (String suffix : DATAPACK_SPELL_SUFFIXES) {
            spells.add(register(HexPattern.fromAngles("qaeaqew" + suffix, HexDir.NORTH_WEST), "datapackspell-" + i,
                    new OpDatapackFunction(argCounts.get(i), "datapackspell-" + i)));
            i++;
        }
        //jank :(
        DATAPACK_SPELLS = new HexPattern[]{spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()),
                spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()),
                spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()),
                spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()),
                spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()),
                spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex()),
                spells.get(getIndex()), spells.get(getIndex()), spells.get(getIndex())};
        Ephemera.LOGGER.info("Registered " + DATAPACK_SPELLS.length + " datapack spells");
    }*/

    //assorted great spells
    public static HexPattern INVISIBILITY = registerPerWorld(HexPattern.fromAngles("qqqqqaewawaweqa", HexDir.SOUTH_WEST), "invisibility", new OpPotionEffect(
            StatusEffects.INVISIBILITY, (int)(MediaConstants.DUST_UNIT / 3), false, false, true));
    public static HexPattern MAGE_ARMOR = registerPerWorld(HexPattern.fromAngles("qaweqqwqqewaqeqqqqqad", HexDir.NORTH_WEST), "magearmor", new OpMageArmor());
    //status stuff
    public static HexPattern REMOVE_STATUS = register(HexPattern.fromAngles("eeeeedaqdewed", HexDir.SOUTH_WEST), "removestatus", new OpRemoveStatus());
    public static HexPattern GET_STATUS = register(HexPattern.fromAngles("qqqqqedwd", HexDir.SOUTH_WEST), "getstatus", new OpGetEffects());
    public static HexPattern GET_STATUS_CATEGORY = register(HexPattern.fromAngles("eeeeeqawa", HexDir.SOUTH_EAST), "getstatuscategory", new OpGetEffectCategory());
    public static HexPattern GET_STATUS_DURATION = register(HexPattern.fromAngles("qqqqqedwdwd", HexDir.SOUTH_WEST), "getstatusduration", new OpGetStatusDetail(false));
    public static HexPattern GET_STATUS_LEVEL = register(HexPattern.fromAngles("eeeeeqawawa", HexDir.SOUTH_EAST), "getstatuslevel", new OpGetStatusDetail(true));
    public static HexPattern GET_BY_STATUS = register(HexPattern.fromAngles("ewqqqqqwe", HexDir.EAST), "getbystatus", new OpGetEntitiesByStatus(false));
    public static HexPattern GET_BY_STATUS_INVERSE = register(HexPattern.fromAngles("qweeeeewq", HexDir.EAST), "getbystatusinverse", new OpGetEntitiesByStatus(true));
    public static HexPattern GET_BY_STATUS_SINGLE = register(HexPattern.fromAngles("eaeeeeeae", HexDir.EAST), "getbystatussingle", new OpGetEntityByStatus());
    //misc actions
    public static HexPattern FILTERED_SCOUTS = register(HexPattern.fromAngles("wqded", HexDir.EAST), "filteredentityraycast", new OpFilteredEntityRaycast());
    public static HexPattern GAUSSIAN_RAND = register(HexPattern.fromAngles("eeeeq", HexDir.NORTH_EAST), "gaussianrand", new OpGaussianRand());
    //frame stuff
    public static HexPattern READ_FRAME_ROTATION = register(HexPattern.fromAngles("wwawwqwwawwaeae", HexDir.SOUTH_WEST), "readframerotation", new OpFrameRotation(0));
    public static HexPattern SET_FRAME_ROTATION = register(HexPattern.fromAngles("wwawwqwwawwaqdq", HexDir.SOUTH_WEST), "setframerotation", new OpFrameRotation(1));

    public static HexPattern PLASMA_BEAM = register(HexPattern.fromAngles("aqqqadweaqa", HexDir.NORTH_EAST), "plasmabeam", new OpPlasma());
    //public static HexPattern LINK_DAMAGE = disabled(HexPattern.fromAngles("qqqqqwdeddwwaawaawa", HexDir.NORTH_WEST), "linkoverload", new OpLinkDamage());
    public static HexPattern LINK_SCAN = register(HexPattern.fromAngles("eqqqqqaweqaeaq", HexDir.EAST), "networkscan", new OpNetworkScan());
    public static HexPattern LINK_TELEPORT = register(HexPattern.fromAngles("qqqqqwdeddwdawqqqwaq", HexDir.WEST), "networktp", new OpNetworkTeleport());
    public static HexPattern PARTICLE_BURST = register(HexPattern.fromAngles("deeeewaaddwqqqqa", HexDir.EAST), "particleburst", new OpParticleBurst());
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
