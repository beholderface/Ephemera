package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.casting.actions.spells.OpPotionEffect;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import kotlin.Triple;
import net.beholderface.ephemera.casting.patterns.OpIotaSize;
import net.beholderface.ephemera.casting.patterns.OpStackSizeDeep;
import net.beholderface.ephemera.casting.patterns.*;
import net.beholderface.ephemera.casting.patterns.link.OpNetworkScan;
import net.beholderface.ephemera.casting.patterns.link.OpNodeIndex;
import net.beholderface.ephemera.casting.patterns.math.OpGaussianRand;
import net.beholderface.ephemera.casting.patterns.link.OpNetworkTeleport;
import net.beholderface.ephemera.casting.patterns.spells.*;
import net.beholderface.ephemera.casting.patterns.spells.great.OpMageArmor;
import net.beholderface.ephemera.casting.patterns.spells.great.OpRepair;
import net.beholderface.ephemera.casting.patterns.status.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.beholderface.ephemera.Ephemera.id;

@SuppressWarnings("unused")
public class EphemeraPatternRegistry {
    public static List<Triple<HexPattern, Identifier, Action>> PATTERNS = new ArrayList<>();
    public static List<Triple<HexPattern, Identifier, Action>> PER_WORLD_PATTERNS = new ArrayList<>();

    //assorted great spells
    public static HexPattern INVISIBILITY = registerPerWorld(HexPattern.fromAngles("qqqqqaewawaweqa", HexDir.SOUTH_WEST), "invisibility", new OpPotionEffect(
            StatusEffects.INVISIBILITY, MediaConstants.DUST_UNIT / 3, false, false));
    public static HexPattern MAGE_ARMOR = registerPerWorld(HexPattern.fromAngles("qaweqqwqqewaqeqqqqqad", HexDir.NORTH_WEST), "magearmor", new OpMageArmor());
    public static HexPattern REPAIR = registerPerWorld(HexPattern.fromAngles("waqdqqwqqdqawwqqqqqaqedeq", HexDir.WEST), "repair", new OpRepair());
    //status stuff
    public static HexPattern REMOVE_STATUS = register(HexPattern.fromAngles("eeeeedaqdewed", HexDir.SOUTH_WEST), "removestatus", new OpRemoveStatus());
    public static HexPattern GET_STATUS = register(HexPattern.fromAngles("qqqqqedwd", HexDir.SOUTH_WEST), "getstatus", new OpGetEffects());
    public static HexPattern GET_STATUS_CATEGORY = register(HexPattern.fromAngles("eeeeeqawa", HexDir.SOUTH_EAST), "getstatuscategory", new OpGetEffectCategory());
    public static HexPattern GET_STATUS_DURATION = register(HexPattern.fromAngles("qqqqqedwdwd", HexDir.SOUTH_WEST), "getstatusduration", new OpGetStatusDetail(false));
    public static HexPattern GET_STATUS_LEVEL = register(HexPattern.fromAngles("eeeeeqawawa", HexDir.SOUTH_EAST), "getstatuslevel", new OpGetStatusDetail(true));
    public static HexPattern GET_BY_STATUS = register(HexPattern.fromAngles("ewqqqqqwe", HexDir.EAST), "getbystatus", new OpGetEntitiesByStatus(false));
    public static HexPattern GET_BY_STATUS_INVERSE = register(HexPattern.fromAngles("qweeeeewq", HexDir.EAST), "getbystatusinverse", new OpGetEntitiesByStatus(true));
    public static HexPattern GET_BY_STATUS_SINGLE = register(HexPattern.fromAngles("eaeeeeeae", HexDir.EAST), "getbystatussingle", new OpGetEntityByStatus());
    public static HexPattern GET_ABSORPTION = register(HexPattern.fromAngles("wqeawaqddaqw", HexDir.NORTH_EAST), "getabsorption", new OpGetAbsorption());
    //misc actions
    public static HexPattern FILTERED_SCOUTS = register(HexPattern.fromAngles("wqded", HexDir.EAST), "filteredentityraycast", new OpFilteredEntityRaycast());
    public static HexPattern GAUSSIAN_RAND = register(HexPattern.fromAngles("eeeeq", HexDir.NORTH_EAST), "gaussianrand", new OpGaussianRand());
    public static HexPattern HASH = register(HexPattern.fromAngles("qqawqaqw", HexDir.SOUTH_EAST), "hash", new OpHash());
    public static HexPattern HASH_BITS = register(HexPattern.fromAngles("eedwedew", HexDir.SOUTH_WEST), "hashbits", new OpHashBits());
    public static HexPattern GET_REVEAL_COST = register(HexPattern.fromAngles("qdeaaqqqqq", HexDir.EAST), "getrevealcost", new OpGetTransmitCost());
    public static HexPattern GET_DURABILITY_MAINHAND = register(HexPattern.fromAngles("qwdea", HexDir.EAST), "getdurabilitymainhand", new OpHandDurability(true));
    public static HexPattern GET_DURABILITY_OFFHAND = register(HexPattern.fromAngles("aedwq", HexDir.EAST), "getdurabilityoffhand", new OpHandDurability(false));
    public static HexPattern GET_IOTA_SIZE = register(HexPattern.fromAngles("qwaeawqaqdedd", HexDir.NORTH_WEST), "getiotasize", new OpIotaSize());
    public static HexPattern GET_STACK_SIZE_DEEP = register(HexPattern.fromAngles("qwaeawqaqded", HexDir.NORTH_WEST), "getstacksizedeep", new OpStackSizeDeep());
    public static HexPattern COLLISION_PROBE = register(HexPattern.fromAngles("qaqqqqqdaqa", HexDir.NORTH_WEST), "collisionprobe", new OpCollisionProbe());
    public static HexPattern THOTH_YOINK = register(HexPattern.fromAngles("qaeaqdadad", HexDir.NORTH_WEST), "thothyoink", new OpThothYoink());
    public static HexPattern THOTH_INSPECT = register(HexPattern.fromAngles("wqaqwadad", HexDir.NORTH_EAST), "thothcount", new OpThothCount());
    public static HexPattern NO = register(HexPattern.fromAngles("wwaedadqdqdqdade", HexDir.NORTH_EAST), "no", new OpNo());
    public static HexPattern GET_PROPERTIES = register(HexPattern.fromAngles("qaqqqqqwwdwewdw", HexDir.EAST), "getproperties", new OpGetProperties());
    public static HexPattern GET_PROPERTY_VALUE = register(HexPattern.fromAngles("qaqqqqqdwawqwaw", HexDir.EAST), "getpropertyvalue", new OpGetPropertyValue());
    //frame stuff
    public static HexPattern READ_FRAME_ROTATION = register(HexPattern.fromAngles("wwawwqwwawwaeae", HexDir.SOUTH_WEST), "readframerotation", new OpFrameRotation(0));
    public static HexPattern SET_FRAME_ROTATION = register(HexPattern.fromAngles("wwawwqwwawwaqdq", HexDir.SOUTH_WEST), "setframerotation", new OpFrameRotation(1));

    public static HexPattern LINK_SCAN = register(HexPattern.fromAngles("eqqqqqaweqaeaq", HexDir.EAST), "networkscan", new OpNetworkScan());
    public static HexPattern LINK_INDEX = register(HexPattern.fromAngles("eqqqqqaweqaeaqa", HexDir.EAST), "networkindex", new OpNodeIndex());
    public static HexPattern LINK_TELEPORT = register(HexPattern.fromAngles("qqqqqwdeddwdawqqqwaq", HexDir.NORTH_WEST), "networktp", new OpNetworkTeleport());
    public static HexPattern PARTICLE_BURST = register(HexPattern.fromAngles("deeeewaaddwqqqqa", HexDir.EAST), "particleburst", new OpParticleBurst());
    public static HexPattern PAINT_CONJURED = register(HexPattern.fromAngles("eqdweeqdwweeqddqdwwwdeww", HexDir.WEST), "paintconjured", new OpSplatoon());
    public static HexPattern REVEAL_TO_OTHER = register(HexPattern.fromAngles("qde", HexDir.EAST), "revealtoother", new OpPrintToOther());
    public static HexPattern CLEAR_REVEAL_COST = register(HexPattern.fromAngles("qdeqa", HexDir.EAST), "clearrevealcost", new OpClearTransmitHistory());
    public static HexPattern RIDE_WISP = register(HexPattern.fromAngles("aqadqqdaqa", HexDir.NORTH_WEST), "ridewisp", new OpRideWisp());

    public static void init() {
        try {
            for (Triple<HexPattern, Identifier, Action> patternTriple : PATTERNS) {
                Registry.register(HexActions.REGISTRY, patternTriple.getSecond(), new ActionRegistryEntry(patternTriple.getFirst(), patternTriple.getThird()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HexPattern register(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, Identifier, Action> triple = new Triple<>(pattern, id(name), action);
        PATTERNS.add(triple);
        return pattern;
    }

    private static HexPattern registerPerWorld(HexPattern pattern, String name, Action action) {
        return register(pattern, name, action);
    }
}
