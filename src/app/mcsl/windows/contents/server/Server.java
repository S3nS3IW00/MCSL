package app.mcsl.windows.contents.server;

import javafx.scene.layout.Pane;

import java.io.File;

public interface Server {

    Pane getContent();
    void start();
    void stop();
    void restart();
    boolean isRun();
    String getName();
    void rename(String name);
    ServerType getType();
    void sendCommand(String command);
    StatusType getStatus();
    void saveSettings();
    File getRoot();

}
