package app.mcsl.events;

import app.mcsl.events.listeners.ServerStateChangeListener;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.StateType;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerStateChangeEvent {

    private static List<ServerStateChangeListener> listeners = new ArrayList<>();

    public static void addListener(ServerStateChangeListener listener) {
        listeners.add(listener);
    }

    public static void change(Server server, StateType newType) {
        Platform.runLater(() -> {
            for (Iterator<ServerStateChangeListener> it = listeners.iterator(); it.hasNext(); ) {
                it.next().change(server, newType);
            }
        });
    }

}
