package app.mcsl.events.listeners;

import app.mcsl.managers.logging.LogLevel;

public interface LogListener {

    void log(LogLevel logLevel, String text);

}
