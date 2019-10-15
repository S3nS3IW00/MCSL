package app.mcsl.event.listener;

import app.mcsl.manager.logging.LogLevel;

public interface LogListener {

    void log(LogLevel logLevel, String line, String message);

}
