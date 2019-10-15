package app.mcsl.event;

import app.mcsl.event.listener.ServerStateChangeListener;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.StateType;
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
