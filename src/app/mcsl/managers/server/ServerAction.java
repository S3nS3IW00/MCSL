package app.mcsl.managers.server;

import app.mcsl.MainClass;
import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.tab.TabAction;
import app.mcsl.managers.tab.TabType;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerType;
import app.mcsl.windows.contents.server.StateType;
import app.mcsl.windows.elements.dialog.types.ConfirmationDialog;
import javafx.scene.image.ImageView;

public class ServerAction {

    public static void choose(Server server) {
        Logger.info("Choosing server '" + server.getName() + "'...");

        if (!MainClass.getTabManager().isTabByTypeExists(MainClass.getTabManager().getTabClassByServer(server), TabType.SERVER)) {
            TabAction.add(ServersManager.getServerContent(ServersManager.getServerByName(server.getName())), new ImageView(server.getType() == ServerType.LOCAL ? FileManager.SERVER_ICON : FileManager.EXTERNAL_SERVER_ICON), true);
        } else {
            if (MainClass.getTabManager().isDetached(MainClass.getTabManager().getTabByServer(server))) {
                MainClass.getTabManager().getServerStageFromTab(MainClass.getTabManager().getTabByServer(server)).setIconified(false);
                MainClass.getTabManager().getServerStageFromTab(MainClass.getTabManager().getTabByServer(server)).toFront();
            } else {
                TabAction.choose(MainClass.getTabManager().getTabClassByServer(server));
            }
        }
    }

    public static void rename(Server server, String newName) {
        Logger.info("Renaming server '" + server.getName() + "'...");

        String serverName = server.getName();

        server.rename(newName);
        MainClass.getFileManager().renameServer(serverName, MainClass.getFileManager().getServerFolder(serverName), newName);
        ServerStateChangeEvent.change(server, StateType.RENAMED);
    }

    public static void delete(Server server) {
        new ConfirmationDialog(200, 400, Language.getText("deleteserver"), Language.getText("suredeleteserver", server.getName())) {
            @Override
            public void yesAction() {
                if (!MainClass.getFileManager().isExternalServerRoot(server)) close();
                ServersManager.removeServer(server);
                MainClass.getFileManager().deleteServer(server.getName(), MainClass.getFileManager().getServerFolder(server.getName()));
                MainClass.getTemplate().getServersContent().removeServer(server);
            }

            @Override
            public void noAction() {
                close();
            }
        }.show();
    }

    public static void add(Server server) {
        MainClass.getTemplate().getServersContent().addServer(server);
        ServersManager.addServer(server);
    }

}
