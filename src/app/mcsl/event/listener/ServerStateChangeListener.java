package app.mcsl.event.listener;

import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.StateType;

public interface ServerStateChangeListener {

    void change(Server server, StateType newType);

}
