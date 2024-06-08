package net.beholderface.ephemera.fabric;

import at.petrak.hexcasting.api.misc.MediaConstants;
import dev.architectury.platform.Platform;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.api.config.EphemeraConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.EnvType;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Config(name = Ephemera.MOD_ID)
public class EphemeraConfigFabric extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject
    public final Common common = new Common();
    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public final Client client = new Client();
    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public final Server server = new Server();

    public static void init() {
        AutoConfig.register(EphemeraConfigFabric.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        var instance = AutoConfig.getConfigHolder(EphemeraConfigFabric.class).getConfig();

        EphemeraConfig.setCommon(instance.common);

        if (Platform.getEnv().equals(EnvType.CLIENT)) {
            EphemeraConfig.setClient(instance.client);
        }

        // Needed for logical server in singleplayer, do not access server configs from client code
        EphemeraConfig.setServer(instance.server);
    }


    @Config(name = "common")
    private static class Common implements ConfigData, EphemeraConfig.CommonConfigAccess {
    }

    @Config(name = "client")
    private static class Client implements ConfigData, EphemeraConfig.ClientConfigAccess {
    }


    @Config(name = "server")
    private static class Server implements ConfigData, EphemeraConfig.ServerConfigAccess {

        @ConfigEntry.Gui.CollapsibleObject
        private Costs costs = new Costs();

        @Override
        public void validatePostLoad() throws ValidationException {
            this.costs.signumCost = EphemeraConfig.bound(this.costs.signumCost, DEF_MIN_COST, DEF_MAX_COST);
            this.costs.congratsCost = EphemeraConfig.bound(this.costs.congratsCost, DEF_MIN_COST, DEF_MAX_COST);
        }

        @Override
        public int getSignumCost() {
            return (int) (costs.signumCost * MediaConstants.DUST_UNIT);
        }

        @Override
        public int getCongratsCost() {
            return (int) (costs.congratsCost * MediaConstants.DUST_UNIT);
        }

        static class Costs {
            // costs of actions
            double signumCost = DEFAULT_SIGNUM_COST;
            double congratsCost = DEFAULT_CONGRATS_COST;
        }
    }
}