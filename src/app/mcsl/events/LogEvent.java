package app.mcsl.events;

import app.mcsl.events.listeners.LogListener;
import app.mcsl.managers.logging.LogLevel;

import java.util.ArrayList;
import java.util.List;

public class LogEvent {

    public static List<LogListener> listeners = new ArrayList<>();

    public static void addListener(LogListener listener) {
        listeners.add(listener);
    }

    public static void log(LogLevel logLevel, String line, String message) {
        for (LogListener listener : listeners) {
            listener.log(logLevel, line, message);
        }
    }

}
