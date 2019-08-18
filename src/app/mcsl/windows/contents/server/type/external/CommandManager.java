package app.mcsl.windows.contents.server.type.external;

import app.mcsl.events.ServerStatusChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.logging.Logger;
import app.mcsl.windows.contents.server.StatusType;
import app.mcsl.windows.elements.dialog.types.AlertDialog;
import app.mcsl.windows.elements.dialog.types.AlertType;
import javafx.application.Platform;

import java.io.IOException;

public class CommandManager {

    private ExternalServer server;

    public CommandManager(ExternalServer server) {
        this.server = server;
    }

    private boolean isCommand(String data) {
        return data.startsWith("#");
    }

    private String getCommand(String data) {
        if (isCommand(data)) {
            data = data.replace("#", "");
            return data.split(";")[0];
        }
        return null;
    }

    private String[] getArgs(String data) {
        if (isCommand(data)) {
            data = data.replace("#" + getCommand(data) + ";", "");
            return data.split(";");
        }
        return null;
    }

    public void runCommand(String data) {
        if (isCommand(data)) {
            Logger.info("Running external command '" + data + "' on server '" + server.getName() + "'...");
            Platform.runLater(() -> {
                switch (getCommand(data)) {
                    case "showerror":
                        if (getArgs(data).length > 0) {
                            server.getConsole().appendLine("§c[MinecraftServerLauncher] " + Language.getText(getArgs(data)[0]));
                        }
                        break;
                    case "showinfo":
                        if (getArgs(data).length > 0) {
                            server.getConsole().appendLine("§a[MinecraftServerLauncher] " + Language.getText(getArgs(data)[0]));
                        }
                        break;
                    case "showwarn":
                        if (getArgs(data).length > 0) {
                            server.getConsole().appendLine("§e[MinecraftServerLauncher] " + Language.getText(getArgs(data)[0]));
                        }
                        break;
                    case "showerrordialog":
                        if (getArgs(data).length > 0) {
                            new AlertDialog(200, 400, Language.getText("warning"), Language.getText(getArgs(data)[0]), AlertType.ERROR).show();
                        }
                        break;
                    case "showinfodialog":
                        if (getArgs(data).length > 0) {
                            new AlertDialog(200, 400, Language.getText("warning"), Language.getText(getArgs(data)[0]), AlertType.DEFAULT).show();
                        }
                        break;
                    case "showwarndialog":
                        if (getArgs(data).length > 0) {
                            new AlertDialog(200, 400, Language.getText("warning"), Language.getText(getArgs(data)[0]), AlertType.WARNING).show();
                        }
                        break;
                    case "appendconsole":
                        if (getArgs(data).length > 0) {
                            server.parseLine(getArgs(data)[0]);
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
                        server.parseLine(getArgs(data)[0]);
                        break;
                }
            });
        } else {
            server.parseLine(data);
        }
    }

}
