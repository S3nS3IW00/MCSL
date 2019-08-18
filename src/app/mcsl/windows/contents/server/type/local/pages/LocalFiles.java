package app.mcsl.windows.contents.server.type.local.pages;

import app.mcsl.MainClass;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.windows.contents.server.type.local.LocalServer;
import app.mcsl.windows.elements.IconCard;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.types.ConfirmationDialog;
import app.mcsl.windows.elements.label.LabelColor;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class LocalFiles extends HBox {

    private LocalServer server;

    private TreeView treeView;
    private TextArea textEditor;
    private Button saveButton, resetButton, filesButton, addPluginButton, deleteFileButton;

    private File selectedFile;
    private boolean canEditFile = false;

    public LocalFiles(LocalServer server){
        this.server = server;

        treeView = new TreeView();
        treeView.setMaxWidth(200);
        treeView.setMinWidth(200);
        VBox.setVgrow(treeView, Priority.ALWAYS);
        displayTreeView(MainClass.getFileManager().getServerFolder(server.getName()).getAbsolutePath());
        EventHandler<MouseEvent> mouseEventHandle = this::handleMouseClicked;
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);

        textEditor = new TextArea();
        VBox.setVgrow(textEditor, Priority.ALWAYS);
        HBox.setHgrow(textEditor, Priority.ALWAYS);
        textEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            if(saveButton.isDisabled() && canEditFile) saveButton.setDisable(false);
            if(resetButton.isDisabled() && canEditFile) resetButton.setDisable(false);
        });

        saveButton = new Button(Language.getText("save"), app.mcsl.windows.elements.button.ButtonType.APPLY_ACTION_BUTTON);
        saveButton.setDisable(true);
        saveButton.setOnAction(e -> {
            PrintWriter prw;
            try {
                prw = new PrintWriter(selectedFile);
                prw.println(textEditor.getText());
                prw.close();
                saveButton.setDisable(true);
                resetButton.setDisable(true);
            } catch (FileNotFoundException ex) {
                //empty catch block
            }
        });

        resetButton = new Button(Language.getText("reset"), app.mcsl.windows.elements.button.ButtonType.WARNING_ACTION_BUTTON);
        resetButton.setDisable(true);
        resetButton.setOnAction(e -> {
            textEditor.clear();
            try {
                for(String line : Files.readAllLines(selectedFile.toPath())){
                    textEditor.appendText(line+System.lineSeparator());
                }
                saveButton.setDisable(true);
                resetButton.setDisable(true);
            } catch (IOException ex) {
                textEditor.setText(Language.getText("cantloadfile", selectedFile.getAbsolutePath()));
            }
        });

        deleteFileButton = new Button(Language.getText("deletefile"), app.mcsl.windows.elements.button.ButtonType.ERROR_ACTION_BUTTON);
        deleteFileButton.setDisable(true);
        deleteFileButton.setOnAction(e -> new ConfirmationDialog(200, 400, Language.getText("deletefile"), Language.getText("suredeletefile", selectedFile.getName())) {
            @Override
            public void yesAction() {
                MainClass.getFileManager().deleteFile(selectedFile);
                refreshFiles();
                canEditFile = false;
                deleteFileButton.setDisable(true);
                saveButton.setDisable(true);
                resetButton.setDisable(true);
                textEditor.clear();
                close();
            }

            @Override
            public void noAction() {
                close();
            }
        }.show());

        HBox editorActionButtonBox = new HBox(5, saveButton, resetButton, deleteFileButton);
        editorActionButtonBox.setMinHeight(40);
        editorActionButtonBox.setAlignment(Pos.CENTER);

        Label editFilesWarnLabel = new Label(Language.getText("editfileswarn"));
        HBox.setHgrow(editFilesWarnLabel, Priority.ALWAYS);
        editFilesWarnLabel.setStyle("-fx-text-fill: -fx-themetypecolor;\n" +
                "    -fx-font-size: 15px;\n" +
                "    -fx-font-weight: bold;");
        editFilesWarnLabel.setWrapText(true);

        IconCard editFilesWarnIconCard = new IconCard(new ImageView(FileManager.TIP_ICON), editFilesWarnLabel, 200, 50);
        HBox.setHgrow(editFilesWarnIconCard, Priority.ALWAYS);

        VBox editorBox = new VBox(10, editFilesWarnIconCard, textEditor, editorActionButtonBox);
        VBox.setVgrow(editorBox, Priority.ALWAYS);
        HBox.setHgrow(editorBox, Priority.ALWAYS);

        filesButton = new Button(Language.getText("files"), app.mcsl.windows.elements.button.ButtonType.ACTION_BUTTON);
        filesButton.setMaxWidth(Double.MAX_VALUE);
        filesButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(MainClass.getFileManager().getServerFolder(server.getName()));
            } catch (IOException ex) {
                //empty catch block
            }
        });

        addPluginButton = new Button(Language.getText("addplugin"), ButtonType.ACTION_BUTTON);
        addPluginButton.setMaxWidth(Double.MAX_VALUE);
        addPluginButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Language.getText("addplugin"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JAR", "*.jar")
            );
            File plugin = fileChooser.showOpenDialog(MainClass.getTemplate());
            if (plugin != null) {
                try {
                    MainClass.getFileManager().addPlugin(server.getName(), plugin);
                    MainClass.getTemplate().showNotification(Language.getText("pluginadded"), LabelColor.ERROR);
                    refreshFiles();
                } catch (IOException ex) {
                    //empty catch block
                }
            }
        });

        VBox sideBox = new VBox(5, treeView, filesButton, addPluginButton);

        setSpacing(10);
        getChildren().addAll(sideBox, editorBox);
    }

    private static void createTree(File file, CheckBoxTreeItem<File> parent) {
        CheckBoxTreeItem<File> treeItem = new CheckBoxTreeItem<>(file);
        if (file.isDirectory()) {
            parent.getChildren().add(treeItem);
            for (File f : file.listFiles()) {
                createTree(f, treeItem);
            }
        } else {
            parent.getChildren().add(treeItem);
        }
    }

    private void displayTreeView(String inputDirectoryLocation) {
        CheckBoxTreeItem<File> rootItem = new CheckBoxTreeItem<>(new File(inputDirectoryLocation));

        treeView.setShowRoot(false);
        treeView.setCellFactory(CheckBoxTreeCell.<File>forTreeView());

        File fileInputDirectoryLocation = new File(inputDirectoryLocation);
        File fileList[] = fileInputDirectoryLocation.listFiles();

        for (File file : fileList) {
            createTree(file, rootItem);
        }

        treeView.setRoot(rootItem);
        treeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            public TreeCell<File> call(TreeView<File> tv) {
                return new TreeCell<File>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        setText((empty || item == null) ? "" : item.getName());
                    }
                };
            }
        });
    }

    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            CheckBoxTreeItem<File> treeItem = (CheckBoxTreeItem<File>)treeView.getSelectionModel().getSelectedItem();
            if(treeItem != null && treeItem.getValue() != null) {
                if (treeItem.getValue().isFile() && (FileManager.getFileExtension(treeItem.getValue()).equalsIgnoreCase(".yml") ||
                        FileManager.getFileExtension(treeItem.getValue()).equalsIgnoreCase(".properties") ||
                        FileManager.getFileExtension(treeItem.getValue()).equalsIgnoreCase(".txt") ||
                        FileManager.getFileExtension(treeItem.getValue()).equalsIgnoreCase(".json") ||
                        FileManager.getFileExtension(treeItem.getValue()).equalsIgnoreCase(".log"))) {
                    textEditor.clear();
                    try {
                        for (String line : Files.readAllLines(treeItem.getValue().toPath())) {
                            textEditor.appendText(line + System.lineSeparator());
                        }
                        canEditFile = true;
                    } catch (IOException e) {
                        textEditor.setText(Language.getText("cantloadfile", treeItem.getValue().getAbsolutePath()));
                        canEditFile = false;
                    }
                } else {
                    textEditor.setText(Language.getText("cantloadfile", treeItem.getValue().getAbsolutePath()));
                    canEditFile = false;
                }
                selectedFile = treeItem.getValue();
                deleteFileButton.setDisable(false);
                saveButton.setDisable(true);
                resetButton.setDisable(true);
            }
        }
    }

    public void refreshFiles(){
        saveButton.setDisable(true);
        resetButton.setDisable(true);
        deleteFileButton.setDisable(true);
        textEditor.clear();
        displayTreeView(MainClass.getFileManager().getServerFolder(server.getName()).getAbsolutePath());
    }

}
