package app.mcsl.windows.contents.server;

import app.mcsl.managers.tab.TabClass;
import app.mcsl.managers.tab.TabType;
import javafx.scene.layout.Pane;

public class ServerContent implements TabClass {

    private Server server;

    public ServerContent(Server server) {
        this.server = server;
    }

    @Override
    public Pane getContent() {
        return server.getContent();
    }

    @Override
    public TabType getType() {
        return TabType.SERVER;
    }

    @Override
    public String getTitle() {
        return server.getName();
    }

    public Server getServer() {
        return server;
    }
}
