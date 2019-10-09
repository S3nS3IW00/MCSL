package app.mcsl.windows.elements.dialog.customdialogs;

import app.mcsl.MainClass;
import app.mcsl.events.ServerStatusChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.mainside.TrayManager;
import app.mcsl.managers.server.ServersManager;
import app.mcsl.managers.tab.TabManager;
import app.mcsl.windows.Template;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerStage;
import app.mcsl.windows.contents.server.StatusType;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class QuitDialog extends Dialog {

    private VBox content;
    private ProgressBar serversStopProgressBar;
    private Button noButton, yesButton, restartButton, hideButton;
    private Label textLabel;

    private ObservableList<Server> onlineServers;
    private int onlineServerCount;

    public QuitDialog() {
        super(200, 500, Language.getText("quit"), DialogType.CUSTOM, new VBox());

        textLabel = new Label(Language.getText("surewantquit") + (onlineServerCount > 0 ? "\n" + Language.getText("serverswillstop") : ""));

        onlineServers = FXCollections.observableList(ServersManager.getOnlineServers());
        onlineServerCount = onlineServers.size();

        serversStopProgressBar = new ProgressBar();
        serversStopProgressBar.setPrefSize(500, 30);

        noButton = new Button(Language.getText("no"), ButtonType.ERROR);
        noButton.setOnAction(e -> close());

        yesButton = new Button(Language.getText("yes"), ButtonType.APPLY);
        yesButton.setOnAction(e -> {
            noButton.setDisable(true);
            yesButton.setDisable(true);
            restartButton.setDisable(true);
            hideButton.setDisable(true);
            textLabel.setText(Language.getText("stoppingservers"));

            onlineServers = FXCollections.observableList(ServersManager.getOnlineServers());
            onlineServerCount = onlineServers.size();

            quit(false);
        });

        restartButton = new Button(Language.getText("restart"), ButtonType.WARNING);
        restartButton.setOnAction(e -> {
            noButton.setDisable(true);
            yesButton.setDisable(true);
            restartButton.setDisable(true);
            hideButton.setDisable(true);
            textLabel.setText(Language.getText("stoppingservers"));

            onlineServers = FXCollections.observableList(ServersManager.getOnlineServers());
            onlineServerCount = onlineServers.size();

            quit(true);
        });

        hideButton = new Button(Language.getText("hide"), ButtonType.WARNING);
        hideButton.setOnAction(e -> {
            Logger.info("Hiding application...");
            close();

            Platform.setImplicitExit(false);
            Template.getStage().hide();
            for (ServerStage serverStage : TabManager.getServerStages()) serverStage.hide();
            TrayManager.displayTray(Language.getText("waithere"), TrayIcon.MessageType.INFO);
            //Notifications.push(null, new Notification(Language.getText("hiding"), Language.getText("waithere"), NotificationAlertType.INFO));
        });

        content = new VBox(textLabel);

        setContent(content);
        addButton(noButton, yesButton, restartButton, hideButton);

        build();
    }

    private void quit(boolean restart) {
        FileManager.getConfigProps().setProp("lastWidth", Template.getStage().getScene().getWidth());
        FileManager.getConfigProps().setProp("lastHeight", Template.getStage().getScene().getHeight() + "");
        FileManager.getConfigProps().setProp("isMaximized", Template.getStage().isMaximized());
        if (ServersManager.getServers().size() > 0) {
            Logger.info("Saving server settings...");
            for (Server server : ServersManager.getServers()) {
                server.saveSettings();
            }
        }
        if (ServersManager.getServers().size() > 0 && onlineServerCount > 0) {
            Logger.info("Stopping servers...");
            CompletableFuture.runAsync(() -> Platform.runLater(() -> {
                content.getChildren().add(serversStopProgressBar);

                onlineServers.addListener((ListChangeListener<Server>) c -> {
                    if (c.next()) {
                        serversStopProgressBar.setProgress((onlineServerCount - c.getList().size()) / onlineServerCount);
                        if (c.getList().size() == 0) {
                            if (restart) {
                                restartApplication();
                            } else {
                                System.exit(0);
                            }
                        }
                    }
                });

                for (Server s : onlineServers) {
                    ServerStatusChangeEvent.addListener((server, newType) -> {
                        if (server == s && newType == StatusType.STOPPED) {
                            onlineServers.remove(s);
                        }
                    });
                    s.stop();
                }
            }));
        } else {
            if (restart) {
                restartApplication();
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    public void showAndOverlay() {
        onlineServers = FXCollections.observableList(ServersManager.getOnlineServers());
        onlineServerCount = onlineServers.size();
        textLabel.setText(Language.getText("surewantquit") + (onlineServerCount > 0 ? "\n" + Language.getText("serverswillstop") : ""));
        super.show();
    }

    public void hide() {
        show();
        hideButton.fire();
    }

    public void restart() {
        show();
        restartButton.fire();
    }

    public void quit() {
        show();
        yesButton.fire();
    }

    private void restartApplication() {
        Logger.info("Restarting application...");
        try {
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(MainClass.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if (!currentJar.getName().endsWith(".jar"))
                return;

            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException | URISyntaxException e) {
            Logger.exception(e);
        }
        System.exit(0);
    }

}
