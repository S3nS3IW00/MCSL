package app.mcsl.events.listeners;

import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.StateType;

public interface ServerStateChangeListener {

    void change(Server server, StateType newType);

}
