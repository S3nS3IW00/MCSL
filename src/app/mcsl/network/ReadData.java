package app.mcsl.network;

import app.mcsl.event.ServerStatusChangeEvent;
import app.mcsl.manager.Language;
import app.mcsl.utils.Crypter;
import app.mcsl.window.content.server.StatusType;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReadData extends Thread {

    private Client client;

    ReadData(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!client.getClient().isClosed()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getClient().getInputStream(), StandardCharsets.UTF_8));

                String data;
                while (!client.getClient().isClosed() && (data = reader.readLine()) != null) {
                    client.readData(Crypter.decode(data));
                }
            } catch (IOException e) {
                try {
                    client.getClient().close();
                    ServerStatusChangeEvent.change(client.getServer(), StatusType.STOPPED);
                    Platform.runLater(() -> client.getServer().getConsole().appendLine("§a[MinecraftServerLauncher] " + Language.getText("disconnectedfromserver")));
                } catch (IOException e1) {
                    //empty catch block
                }
            }
        }
    }
}
