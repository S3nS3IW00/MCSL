package app.mcsl.window.content.server.type.external;

import app.mcsl.event.ServerStatusChangeEvent;
import app.mcsl.manager.Language;
import app.mcsl.manager.logging.Logger;
import app.mcsl.manager.tab.TabManager;
import app.mcsl.window.content.server.StatusType;
import app.mcsl.window.element.dialog.type.AlertDialog;
import app.mcsl.window.element.dialog.type.AlertType;
import app.mcsl.window.element.notification.Notification;
import app.mcsl.window.element.notification.NotificationAlertType;
import app.mcsl.window.element.notification.Notifications;
import javafx.application.Platform;

import java.io.IOException;
import java.util.Arrays;

public class CommandManager {

    private ExternalServer server;

    public CommandManager(ExternalServer server) {
        this.server = server;
    }

    private boolean isCommand(String data) {
        return data.startsWith("#");
    }

    public void runCommand(String data) {
        if (isCommand(data)) {
            String[] raw = data.split(";");
            String cmd = raw[0].substring(1);
            String[] args = raw.length > 1 ? Arrays.copyOfRange(raw, 1, raw.length) : new String[]{};
            Logger.info("Running external command '" + data + "' on server '" + server.getName() + "'...");
            Platform.runLater(() -> {
                switch (cmd) {
                    case "showerror":
                        if (args.length > 0) {
                            server.getConsole().appendLine("§c[MinecraftServerLauncher] " + Language.getText(args[0]));
                            Notifications.push(TabManager.getTabClassByServer(server), new Notification(server.getName(), Language.getText(args[0]), NotificationAlertType.ERROR));
                        }
                        break;
                    case "showinfo":
                        if (args.length > 0) {
                            server.getConsole().appendLine("§a[MinecraftServerLauncher] " + Language.getText(args[0]));
                            Notifications.push(TabManager.getTabClassByServer(server), new Notification(server.getName(), Language.getText(args[0]), NotificationAlertType.INFO));
                        }
                        break;
                    case "showwarn":
                        if (args.length > 0) {
                            server.getConsole().appendLine("§e[MinecraftServerLauncher] " + Language.getText(args[0]));
                            Notifications.push(TabManager.getTabClassByServer(server), new Notification(server.getName(), Language.getText(args[0]), NotificationAlertType.WARNING));
                        }
                        break;
                    case "showerrordialog":
                        if (args.length > 0) {
                            new AlertDialog(200, 400, Language.getText("warning"), Language.getText(args[0]), AlertType.ERROR).show();
                        }
                        break;
                    case "showinfodialog":
                        if (args.length > 0) {
                            new AlertDialog(200, 400, Language.getText("warning"), Language.getText(args[0]), AlertType.DEFAULT).show();
                        }
                        break;
                    case "showwarndialog":
                        if (args.length > 0) {
                            new AlertDialog(200, 400, Language.getText("warning"), Language.getText(args[0]), AlertType.WARNING).show();
                        }
                        break;
                    case "appendconsole":
                        if (args.length > 0) {
                            server.parseLine(args[0]);
                        }
                        break;
                    case "disconnect":
                        try {
                            server.getConnection().getClient().getClient().close();
                            ServerStatusChangeEvent.change(server, StatusType.STOPPED);
                            Platform.runLater(() -> server.getConsole().appendLine("§a[MinecraftServerLauncher] " + Language.getText("disconnectedfromserver")));
                        } catch (IOException e) {
                            //empty catch block
                        }
                        break;
                    case "errorthrown":

                        break;
                    default:
                        server.parseLine(args[0]);
                        break;
                }
            });
        } else {
            server.parseLine(data);
        }
    }

}
