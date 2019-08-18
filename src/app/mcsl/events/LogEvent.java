package app.mcsl.events;

import app.mcsl.events.listeners.LogListener;
import app.mcsl.managers.logging.LogLevel;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LogEvent {

    private static List<LogListener> listeners = new ArrayList<>();

    public static void addListener(LogListener listener) {
        listeners.add(listener);
    }

    public static void log(LogLevel logLevel, String text) {
        Platform.runLater(() -> {
            for (Iterator<LogListener> it = listeners.iterator(); it.hasNext(); ) {
                it.next().log(logLevel, text);
            }
        });
    }

}
