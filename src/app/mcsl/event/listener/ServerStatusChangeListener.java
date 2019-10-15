package app.mcsl.event.listener;

import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.StatusType;

public interface ServerStatusChangeListener {

    void change(Server server, StatusType newType);

}
