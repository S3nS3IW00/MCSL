package app.mcsl.window.content.server;

import app.mcsl.manager.tab.TabClass;
import app.mcsl.manager.tab.TabType;
import javafx.scene.image.Image;
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

    @Override
    public Image getIcon() {
        return null;
    }
}
