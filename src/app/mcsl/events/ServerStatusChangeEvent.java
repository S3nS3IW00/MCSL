package app.mcsl.events;

import app.mcsl.events.listeners.ServerStatusChangeListener;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.StatusType;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class ServerStatusChangeEvent {

    private static List<ServerStatusChangeListener> listeners = new ArrayList<>();

    public static void addListener(ServerStatusChangeListener listener){
        listeners.add(listener);
    }

    public static void change(Server server, StatusType newType){
        Platform.runLater(() -> listeners.forEach(l -> l.change(server, newType)));

    }

}
