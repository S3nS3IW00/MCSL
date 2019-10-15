package app.mcsl.event;

import app.mcsl.event.listener.DirectoryChangeListener;
import app.mcsl.manager.file.DirectoryType;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DirectoryChangeEvent {

    private static List<DirectoryChangeListener> listeners = new ArrayList<>();

    public static void addListener(DirectoryChangeListener listener) {
        listeners.add(listener);
    }

    public static void change(DirectoryType type) {
        Platform.runLater(() -> {
            for (Iterator<DirectoryChangeListener> it = listeners.iterator(); it.hasNext(); ) {
                it.next().change(type);
            }
        });
    }

}
