package app.mcsl.windows.contents.mainpage;

import app.mcsl.MainClass;
import app.mcsl.events.DirectoryChangeEvent;
import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.DirectoryType;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.server.ServersManager;
import app.mcsl.managers.tab.TabClass;
import app.mcsl.managers.tab.TabType;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.StateType;
import app.mcsl.windows.elements.GroupBox;
import app.mcsl.windows.elements.Table;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.types.ConfirmationDialog;
import app.mcsl.windows.elements.label.LabelColor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FilesContent implements TabClass {

    private ScrollPane content;

    public FilesContent() {
        Table serversTable = new Table();
        serversTable.setAlignment(Pos.TOP_CENTER);
        serversTable.addColumn(Language.getText("servername"), Language.getText("location"), Language.getText("size"), Language.getText("files"));

        GroupBox serversBox = new GroupBox(Language.getText("servers"));
        serversBox.getBody().setPadding(new Insets(20));
        serversBox.add(serversTable);
        serversBox.getBody().setAlignment(Pos.CENTER);

        Table serverFilesTable = new Table();
        serverFilesTable.setAlignment(Pos.TOP_CENTER);
        serverFilesTable.addColumn(Language.getText("serverfile"), Language.getText("size"), Language.getText("delete"));
        for (File serverFile : MainClass.getFileManager().getServerFilesFolder().listFiles()) {
            app.mcsl.windows.elements.button.Button deleteButton = new app.mcsl.windows.elements.button.Button("", ButtonType.ROUNDED_ERROR);
            deleteButton.setGraphic(new ImageView(FileManager.DELETE_ICON));
            deleteButton.setOnAction(e -> new ConfirmationDialog(200, 400, Language.getText("delete"), Language.getText("suredeletefile", serverFile.getName())) {
                @Override
                public void yesAction() {
                    MainClass.getFileManager().deleteFile(serverFile);
                    DirectoryChangeEvent.change(DirectoryType.SERVERFILE);
                    close();
                }

                @Override
                public void noAction() {
                    close();
                }
            }.show());

            int sizeInMb = (int) serverFile.length() / (1024 * 1024);

            serverFilesTable.addRow(serverFile.getName(), sizeInMb + "MB", deleteButton);
        }

        Label addServerFileLabel = new Label(Language.getText("addserverfiletext"));
        addServerFileLabel.setOpacity(0.5);
        addServerFileLabel.setMaxWidth(300);
        addServerFileLabel.setWrapText(true);
        addServerFileLabel.setStyle("-fx-font-size: 15px;-fx-text-fill: gray;-fx-alignment:center;");

        app.mcsl.windows.elements.button.Button addServerFileButton = new app.mcsl.windows.elements.button.Button(Language.getText("addserverfile"), ButtonType.ACTION_BUTTON);
        addServerFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Language.getText("addserverfile"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JAR", "*.jar")
            );
            File serverFile = fileChooser.showOpenDialog(MainClass.getTemplate());
            if (serverFile != null) {
                try {
                    MainClass.getFileManager().addServerfile(serverFile);
                    MainClass.getTemplate().showNotification(Language.getText("serverfileadded"), LabelColor.ERROR);
                } catch (IOException ex) {
                    //empty catch block
                }
            }
        });

        VBox addServerFileBox = new VBox(5, addServerFileLabel, addServerFileButton);
        addServerFileBox.setAlignment(Pos.CENTER);

        HBox serverFilesContentBox = new HBox(20, serverFilesTable, addServerFileBox);
        serverFilesContentBox.setAlignment(Pos.CENTER);

        GroupBox serverFilesBox = new GroupBox(Language.getText("serverfiles"));
        serverFilesBox.getBody().setPadding(new Insets(20));
        serverFilesBox.add(serverFilesContentBox);
        serverFilesBox.getBody().setAlignment(Pos.CENTER);

        VBox bodyBox = new VBox(20, serversBox, serverFilesBox);
        bodyBox.setPadding(new Insets(10));

        content = new ScrollPane();
        content.setFitToWidth(true);
        content.setContent(bodyBox);

        ServerStateChangeEvent.addListener((s, newType) -> {
            if (newType == StateType.CREATED) {
                app.mcsl.windows.elements.button.Button filesButton = new app.mcsl.windows.elements.button.Button("", ButtonType.ROUNDED);
                filesButton.setGraphic(new ImageView(FileManager.CHOOSE_ICON));
                filesButton.setOnAction(e -> {
                    try {
                        Desktop.getDesktop().open(MainClass.getFileManager().getServerFolder(s.getName()));
                    } catch (IOException ex) {
                        Logger.exception(ex);
                    }
                });

                int sizeInMb = (int) FileManager.folderSize(MainClass.getFileManager().getServerFolder(s.getName())) / (1024 * 1024);

                serversTable.addRow(s.getName(), s.getRoot().getAbsolutePath(), sizeInMb + "MB", filesButton);
            } else if (newType == StateType.DELETED) {
                serversTable.deleteAllRow();
                for (Server server : ServersManager.getServers()) {
                    app.mcsl.windows.elements.button.Button filesButton = new app.mcsl.windows.elements.button.Button("", ButtonType.ROUNDED);
                    filesButton.setGraphic(new ImageView(FileManager.CHOOSE_ICON));
                    filesButton.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().open(MainClass.getFileManager().getServerFolder(server.getName()));
                        } catch (IOException ex) {
                            Logger.exception(ex);
                        }
                    });

                    int sizeInMb = (int) FileManager.folderSize(MainClass.getFileManager().getServerFolder(server.getName())) / (1024 * 1024);

                    serversTable.addRow(server.getName(), server.getRoot().getAbsolutePath(), sizeInMb + "MB", filesButton);
                }
            }
        });

        DirectoryChangeEvent.addListener(type -> {
            if (type == DirectoryType.SERVERFILE) {
                serverFilesTable.deleteAllRow();
                for (File serverFile : MainClass.getFileManager().getServerFilesFolder().listFiles()) {
                    app.mcsl.windows.elements.button.Button deleteButton = new Button("", ButtonType.ROUNDED_ERROR);
                    deleteButton.setGraphic(new ImageView(FileManager.DELETE_ICON));
                    deleteButton.setOnAction(e -> new ConfirmationDialog(200, 400, Language.getText("delete"), Language.getText("suredeletefile", serverFile.getName())) {
                        @Override
                        public void yesAction() {
                            MainClass.getFileManager().deleteFile(serverFile);
                            close();
                        }

                        @Override
                        public void noAction() {
                            close();
                        }
                    }.show());

                    int sizeInMb = (int) serverFile.length() / (1024 * 1024);

                    serverFilesTable.addRow(serverFile.getName(), sizeInMb + "MB", deleteButton);
                }
            }
        });
    }

    @Override
    public ScrollPane getContent() {
        return content;
    }

    @Override
    public TabType getType() {
        return TabType.MAIN;
    }

    @Override
    public String getTitle() {
        return Language.getText("filemanager");
    }
}
