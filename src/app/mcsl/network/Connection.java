package app.mcsl.network;

import app.mcsl.event.ServerStatusChangeEvent;
import app.mcsl.manager.Language;
import app.mcsl.window.content.server.StatusType;
import app.mcsl.window.content.server.type.external.ExternalServer;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.Socket;

public class Connection {

    private Socket socket = new Socket();
    private ExternalServer server;
    private final String address;
    private final int port;
    private String username;
    private String password;
    private Task<Boolean> task;
    private Client client;

    public Connection(ExternalServer server, String address, int port, String username, String password) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.server = server;

        task = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                if (socket.isConnected()) {
                    this.succeeded();
                    return true;
                } else {
                    try {
                        socket = new Socket(address, port);
                        client = new Client(server, socket, username, password);
                        this.succeeded();
                        ServerStatusChangeEvent.change(server, StatusType.CONNECTED);
                        return true;
                    } catch (IOException ex) {
                        this.failed();
                        Platform.runLater(() -> server.getConsole().appendLine("§c[MinecraftServerLauncher] " + Language.getText("cantconnectserver")));
                        ServerStatusChangeEvent.change(server, StatusType.STOPPED);
                        return false;
                    }
                }
            }
        };
    }

    public void connect() {
        new Thread(task).start();
    }

    public Socket getSocket() {
        return socket;
    }

    public Client getClient() {
        return client;
    }

    public Task<Boolean> getTask() {
        return task;
    }

    public String getAddress() {
        return address;
    }
}
