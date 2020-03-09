package app.mcsl.manager.server;

import app.mcsl.event.ServerStateChangeEvent;
import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.logging.Logger;
import app.mcsl.manager.tab.TabAction;
import app.mcsl.manager.tab.TabManager;
import app.mcsl.manager.tab.TabType;
import app.mcsl.window.Template;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.StateType;
import app.mcsl.window.element.dialog.type.ConfirmationDialog;
import javafx.scene.image.ImageView;

public class ServerAction {

    public static void choose(Server server) {
        Logger.info("Choosing server '" + server.getName() + "'...");

        if (!TabManager.isTabByTypeExists(TabManager.getTabClassByServer(server), TabType.SERVER)) {
            TabAction.add(ServersManager.getServerContent(ServersManager.getServerByName(server.getName())), new ImageView(server.getType().getIcon()), true);
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
                FileManager.deleteServer(server.getName());
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
