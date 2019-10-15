package app.mcsl.event;

import app.mcsl.event.listener.ServerStatusChangeListener;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.StatusType;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class ServerStatusChangeEvent {

    private static List<ServerStatusChangeListener> listeners = new ArrayList<>();

    public static void addListener(ServerStatusChangeListener listener) {
        listeners.add(listener);
    }

    public static void change(Server server, StatusType newType) {
        Platform.runLater(() -> listeners.forEach(l -> l.change(server, newType)));

    }

}
