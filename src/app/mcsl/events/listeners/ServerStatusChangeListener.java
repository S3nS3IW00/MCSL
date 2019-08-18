package app.mcsl.events.listeners;

import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.StatusType;

public interface ServerStatusChangeListener {

    void change(Server server, StatusType newType);

}
