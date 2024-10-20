package net.beholderface.ephemera.forge;

import at.petrak.hexcasting.api.misc.MediaConstants;
import net.beholderface.ephemera.api.config.EphemeraConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class EphemeraConfigForge {

    public static void init() {
        Pair<Common, ForgeConfigSpec> config = (new ForgeConfigSpec.Builder()).configure(Common::new);
        Pair<Client, ForgeConfigSpec> clientConfig = (new ForgeConfigSpec.Builder()).configure(Client::new);
        Pair<Server, ForgeConfigSpec> serverConfig = (new ForgeConfigSpec.Builder()).configure(Server::new);
        EphemeraConfig.setCommon(config.getLeft());
        EphemeraConfig.setClient(clientConfig.getLeft());
        EphemeraConfig.setServer(serverConfig.getLeft());
        ModLoadingContext mlc = ModLoadingContext.get();
        mlc.registerConfig(ModConfig.Type.COMMON, config.getRight());
        mlc.registerConfig(ModConfig.Type.CLIENT, clientConfig.getRight());
        mlc.registerConfig(ModConfig.Type.SERVER, serverConfig.getRight());
    }

    public static class Common implements EphemeraConfig.CommonConfigAccess {
        public Common(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Client implements EphemeraConfig.ClientConfigAccess {
        public Client(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Server implements EphemeraConfig.ServerConfigAccess {
        /*// costs of actions
        private static ForgeConfigSpec.DoubleValue congratsCost;
        private static ForgeConfigSpec.DoubleValue signumCost;*/
        private static ForgeConfigSpec.BooleanValue lessthanequalSentinel;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.translation("text.autoconfig.ephemera.option.server.costs").push("misc");
            lessthanequalSentinel = builder.translation("text.autoconfig.ephemera.option.server.misc.lessthanequalSentinel").define("lessthanequalSentinel", DEFAULT_LESSTHANEQUAL_SENTINEL);

            //congratsCost = builder.translation("text.autoconfig.ephemera.option.server.costs.congratsCost").defineInRange("congratsCost", DEFAULT_CONGRATS_COST, DEF_MIN_COST, DEF_MAX_COST);
            //signumCost = builder.translation("text.autoconfig.ephemera.option.server.costs.signumCost").defineInRange("signumCost", DEFAULT_SIGNUM_COST, DEF_MIN_COST, DEF_MAX_COST);

            builder.pop();
        }

        @Override
        public boolean getLessThanEqualSentinel() {
            return lessthanequalSentinel.get();
        }
    }
}
