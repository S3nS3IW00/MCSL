package app.mcsl.managers.server;

import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.managers.logging.Logger;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerContent;
import app.mcsl.windows.contents.server.ServerType;
import app.mcsl.windows.contents.server.StateType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServersManager {

    private static List<Server> servers = new ArrayList<>();
    private static Map<Server, ServerContent> serverContents = new HashMap<>();

    static void addServer(Server server) {
        Logger.info("Adding server with name '" + server.getName() + "'...");

        servers.add(server);
        serverContents.put(server, new ServerContent(server));
        ServerStateChangeEvent.change(server, StateType.CREATED);
    }

    static void removeServer(Server server) {
        Logger.info("Removing server '" + server.getName() + "'...");

        ServerStateChangeEvent.change(server, StateType.DELETED);
        servers.remove(server);
    }

    public static List<Server> getServers() {
        return servers;
    }

    public static int getCount() {
        return servers.size();
    }

    public static Server getServerByName(String name) {
        for (Server server : servers) {
            if (server.getName().equalsIgnoreCase(name))
                return server;
        }
        return null;
    }

    public static List<Server> getServersByType(ServerType type) {
        List<Server> serversByType = new ArrayList<>();
        for (Server server : servers) {
            if (server.getType() == type)
                serversByType.add(server);
        }
        return serversByType;
    }

    public static List<String> getServerNamesByType(ServerType type) {
        List<String> serversByType = new ArrayList<>();
        for (Server server : servers) {
            if (server.getType() == type)
                serversByType.add(server.getName());
        }
        return serversByType;
    }

    public static List<Server> getOnlineServers() {
        List<Server> onlineservers = new ArrayList<>();
        for (Server server : servers) {
            if (server.isRun()) {
                onlineservers.add(server);
            }
        }
        return onlineservers;
    }

    public static int getOnlineServerCount() {
        return getOnlineServers().size();
    }

    public static int getCountByType(ServerType type) {
        return getServersByType(type).size();
    }

    public static boolean isExists(String serverName) {
        return getServerByName(serverName) != null;
    }

    public static ServerContent getServerContent(Server server) {
        return serverContents.get(server);
    }


}
