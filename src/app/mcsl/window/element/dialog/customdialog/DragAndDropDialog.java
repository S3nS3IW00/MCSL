package app.mcsl.window.element.dialog.customdialog;

import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.server.ServersManager;
import app.mcsl.window.Template;
import app.mcsl.window.content.server.ServerType;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import app.mcsl.window.element.dialog.Dialog;
import app.mcsl.window.element.dialog.DialogType;
import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelColor;
import app.mcsl.window.element.label.LabelType;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;

public class DragAndDropDialog extends Dialog {

    private File file;
    private Label textLabel;
    private ComboBox localServersBox;
    private VBox pluginSelectionBox, serverFileSelectionBox;

    public DragAndDropDialog() {
        super(350, 400, Language.getText("draganddrop"), DialogType.CUSTOM, new VBox());

        textLabel = new Label("What is this?", LabelType.H1);

        Button serverFileButton = new Button(Language.getText("serverfile").toUpperCase(), ButtonType.ACTION_BUTTON);
        serverFileButton.setStyle("-fx-font-size: 13px;");
        serverFileButton.setOnAction(e -> {
            try {
                FileManager.addServerfile(file);
                Template.showNotification(Language.getText("fileaddedas", Language.getText("serverfile")), LabelColor.ERROR);
            } catch (IOException ex) {
                //empty catch block
            }
            close();
        });

        Region serverFileSelectionRegion = new Region();
        VBox.setVgrow(serverFileSelectionRegion, Priority.ALWAYS);

        serverFileSelectionBox = new VBox(10, new ImageView(FileManager.FILE_ICON), serverFileSelectionRegion, serverFileButton);
        serverFileSelectionBox.setAlignment(Pos.TOP_CENTER);

        Button pluginButton = new Button("Plugin".toUpperCase(), ButtonType.ACTION_BUTTON);
        pluginButton.setStyle("-fx-font-size: 13px;");
        pluginButton.setOnAction(e -> {
            try {
                FileManager.addPlugin(localServersBox.getSelectionModel().getSelectedItem().toString(), file);
                Template.showNotification(Language.getText("fileaddedas", "plugin"), LabelColor.ERROR);
            } catch (IOException ex) {
                //empty catch block
            }
            close();
        });

        localServersBox = new ComboBox();

        pluginSelectionBox = new VBox(10, new ImageView(FileManager.FILE_ICON), localServersBox, pluginButton);
        pluginSelectionBox.setAlignment(Pos.TOP_CENTER);

        HBox selectionBox = new HBox(20, serverFileSelectionBox, pluginSelectionBox);
        HBox.setHgrow(selectionBox, Priority.ALWAYS);
        selectionBox.setAlignment(Pos.CENTER);

        VBox content = new VBox(20, textLabel, selectionBox);

        setContent(content);

        Button cancelButton = new Button(Language.getText("cancel"), ButtonType.ERROR);
        cancelButton.setOnAction(e -> close());

        addButton(cancelButton);

        build();
    }

    public void show(File file) {
        this.file = file;
        textLabel.setText(Language.getText("whatisthis", file.getName()));

        if (ServersManager.getServersByType(ServerType.LOCAL).size() == 0) {
            pluginSelectionBox.setDisable(true);
        } else {
            localServersBox.setItems(FXCollections.observableList(ServersManager.getServerNamesByType(ServerType.LOCAL)));
            localServersBox.getSelectionModel().selectFirst();
            pluginSelectionBox.setDisable(false);
        }

        showAndOverlay();
    }

}
