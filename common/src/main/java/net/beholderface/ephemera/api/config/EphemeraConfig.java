package net.beholderface.ephemera.api.config;

import net.beholderface.ephemera.Ephemera;

import java.util.Collection;

/**
 * Platform-agnostic class for statically accessing current config values.
 * If any of the config types (common, client, server) are not needed in your mod,
 * feel free to remove anything related to them in this class and platform-specific config implementations.
 */
public class EphemeraConfig {
    private static final CommonConfigAccess dummyCommon = new CommonConfigAccess() {
    };
    private static final ClientConfigAccess dummyClient = new ClientConfigAccess() {
    };
    private static final ServerConfigAccess dummyServer = new ServerConfigAccess() {
        @Override
        public boolean getLessThanEqualSentinel() {
            throw new IllegalStateException("Attempted to access property of Dummy Config Object");
        }
        /*@Override
        public int getCongratsCost() {
            throw new IllegalStateException("Attempted to access property of Dummy Config Object");
        }

        @Override
        public int getSignumCost() {
            throw new IllegalStateException("Attempted to access property of Dummy Config Object");
        }*/
    };
    private static CommonConfigAccess common = dummyCommon;
    private static ClientConfigAccess client = dummyClient;
    private static ServerConfigAccess server = dummyServer;

    public static CommonConfigAccess getCommon() {
        return common;
    }

    public static void setCommon(CommonConfigAccess common) {
        if (EphemeraConfig.common != dummyCommon) {
            Ephemera.LOGGER.warn("CommonConfigAccess was replaced! Old {} New {}", EphemeraConfig.common.getClass().getName(), common.getClass().getName());
        }
        EphemeraConfig.common = common;
    }

    public static ClientConfigAccess getClient() {
        return client;
    }

    public static void setClient(ClientConfigAccess client) {
        if (EphemeraConfig.client != dummyClient) {
            Ephemera.LOGGER.warn("ClientConfigAccess was replaced! Old {} New {}", EphemeraConfig.client.getClass().getName(), client.getClass().getName());
        }
        EphemeraConfig.client = client;
    }

    public static ServerConfigAccess getServer() {
        return server;
    }

    public static void setServer(ServerConfigAccess server) {

        if (EphemeraConfig.server != dummyServer) {
            Ephemera.LOGGER.warn("ServerConfigAccess was replaced! Old {} New {}", EphemeraConfig.server.getClass().getName(), server.getClass().getName());
        }
        EphemeraConfig.server = server;
    }

    public static int bound(int toBind, int lower, int upper) {
        return Math.min(Math.max(toBind, lower), upper);
    }

    public static double bound(double toBind, double lower, double upper) {
        return Math.min(Math.max(toBind, lower), upper);
    }

    public interface CommonConfigAccess {
    }

    public interface ClientConfigAccess {
    }

    public interface ServerConfigAccess {
        boolean DEFAULT_LESSTHANEQUAL_SENTINEL = true;
        boolean getLessThanEqualSentinel();
    }
}
