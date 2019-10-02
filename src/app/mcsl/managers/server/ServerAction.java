package app.mcsl.managers.server;

import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.tab.TabAction;
import app.mcsl.managers.tab.TabManager;
import app.mcsl.managers.tab.TabType;
import app.mcsl.windows.Template;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerType;
import app.mcsl.windows.contents.server.StateType;
import app.mcsl.windows.elements.dialog.types.ConfirmationDialog;
import javafx.scene.image.ImageView;

public class ServerAction {

    public static void choose(Server server) {
        Logger.info("Choosing server '" + server.getName() + "'...");

        if (!TabManager.isTabByTypeExists(TabManager.getTabClassByServer(server), TabType.SERVER)) {
            TabAction.add(ServersManager.getServerContent(ServersManager.getServerByName(server.getName())), new ImageView(server.getType() == ServerType.LOCAL ? FileManager.SERVER_ICON : FileManager.EXTERNAL_SERVER_ICON), true);
        } else {
            if (TabManager.isDetached(TabManager.getTabByServer(server))) {
                TabManager.getServerStageFromTab(TabManager.getTabByServer(server)).setIconified(false);
                TabManager.getServerStageFromTab(TabManager.getTabByServer(server)).toFront();
            } else {
                TabAction.choose(TabManager.getTabClassByServer(server));
            }
        }
    }

    public static void rename(Server server, String newName) {
        Logger.info("Renaming server '" + server.getName() + "'...");

        String serverName = server.getName();

        server.rename(newName);
        FileManager.renameServer(serverName, FileManager.getServerFolder(serverName), newName);
        ServerStateChangeEvent.change(server, StateType.RENAMED);
    }

    public static void delete(Server server) {
        new ConfirmationDialog(200, 400, Language.getText("deleteserver"), Language.getText("suredeleteserver", server.getName())) {
            @Override
            public void yesAction() {
                ServersManager.removeServer(server);
                FileManager.deleteServer(server.getName(), FileManager.getServerFolder(server.getName()));
                Template.getServersContent().removeServer(server);
                close();
            }

            @Override
            public void noAction() {
                close();
            }
        }.show();
    }

    public void remove(Server server) {
        ServersManager.removeServer(server);
        Template.getServersContent().removeServer(server);
    }

    public static void add(Server server) {
        Template.getServersContent().addServer(server);
        ServersManager.addServer(server);
    }

}
