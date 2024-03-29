package app.mcsl.network;

import app.mcsl.manager.logging.Logger;
import app.mcsl.util.Crypter;
import app.mcsl.window.content.server.type.external.CommandManager;
import app.mcsl.window.content.server.type.external.ExternalServer;
import javafx.application.Platform;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    private Socket client;
    private String username;
    private String password;
    private CommandManager commandManager;
    private ExternalServer server;

    public Client(ExternalServer server, Socket client, String username, String password) {
        this.client = client;
        this.username = username;
        this.password = password;
        this.server = server;
        this.commandManager = new CommandManager(server);

        Thread readData = new ReadData(this);
        readData.start();

        auth();
    }

    private void auth() {
        sendData(Crypter.encode("#auth;" + username + ";" + password));
    }

    void readData(String data) {
        Platform.runLater(() -> commandManager.runCommand(data));
    }

    public void sendData(String data) {
        Logger.info("Sending external command '" + data + "' from '" + server.getName() + "' to '" + client.getInetAddress() + "'...");

        data = Crypter.encode(data);
        if (!client.isClosed()) {
            try {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(data + "\n");
                writer.flush();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }
    }

    public Socket getClient() {
        return client;
    }

    public ExternalServer getServer() {
        return server;
    }
}
