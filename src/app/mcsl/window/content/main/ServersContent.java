package app.mcsl.window.content.main;

import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.tab.TabClass;
import app.mcsl.manager.tab.TabType;
import app.mcsl.window.Template;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.ServerType;
import app.mcsl.window.element.ServerCard;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import app.mcsl.window.element.dialog.customdialog.AddServerDialog;
import app.mcsl.window.element.dialog.type.AlertDialog;
import app.mcsl.window.element.dialog.type.AlertType;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServersContent implements TabClass {

    private VBox body;

    private VBox serverBox;
    private ComboBox categoryComboBox;
    private TextField searchTextField;
    private ScrollPane scrollPane;
    private StackPane serversStack;

    private Label noServersLabel, noSearchServersLabel;

    private AddServerDialog addServerDialog;
    private Map<Server, ServerCard> serversList = new HashMap<>();
    private String[] cats = new String[]{"all", "local", "external"};

    public ServersContent() {
        noServersLabel = new Label(Language.getText("donthaveservers"));
        noServersLabel.setOpacity(0.5);
        noServersLabel.setStyle("-fx-font-size: 25px;-fx-text-fill: gray;-fx-alignment:center;");

        noSearchServersLabel = new Label(Language.getText("noserverfound"));
        noSearchServersLabel.setOpacity(0.5);
        noSearchServersLabel.setStyle("-fx-font-size: 25px;-fx-text-fill: gray;-fx-alignment:center;");

        addServerDialog = new AddServerDialog();

        serverBox = new VBox(10);
        serverBox.setPadding(new Insets(5));

        scrollPane = new ScrollPane();
        scrollPane.setContent(serverBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setMaxWidth(Double.MAX_VALUE);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Region bottomRegion = new Region();
        VBox.setVgrow(bottomRegion, Priority.ALWAYS);

        Button addServer = new Button(Language.getText("addserver"), ButtonType.ACTION_BUTTON);
        addServer.setStyle("-fx-font-size: 15px;");
        addServer.setPrefWidth(300);
        addServer.setOnAction(e -> addServerDialog.show());

        Button importServer = new Button(Language.getText("importserver"), ButtonType.WARNING_ACTION_BUTTON);
        importServer.setStyle("-fx-font-size: 15px;");
        importServer.setPrefWidth(300);
        importServer.setOnAction(e -> {
            if (FileManager.getServerFilesFolder().listFiles().length > 0) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(Language.getText("chooseserverslocation"));
                File serverLocation = directoryChooser.showDialog(Template.getStage());
                if (serverLocation != null) {
                    if (!FileManager.checkImportServerFiles(serverLocation)) {
                        new AlertDialog(200, 400, Language.getText("error"), Language.getText("choosendirnotavaliable"), AlertType.ERROR).show();
                    }
                }
            } else {
                new AlertDialog(200, 400, Language.getText("error"), Language.getText("noserverfile"), AlertType.ERROR).show();
            }
        });

        HBox buttonBox = new HBox(5, addServer, importServer);
        buttonBox.setMinHeight(50);
        buttonBox.setMaxWidth(Double.MAX_VALUE);
        buttonBox.setAlignment(Pos.CENTER);

        Label serversTitle = new Label(Language.getText("servers"));
        serversTitle.setId("underlined-title");

        Region titleRegion = new Region();
        HBox.setHgrow(titleRegion, Priority.ALWAYS);

        searchTextField = new TextField();
        searchTextField.setPromptText(Language.getText("search"));
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> displayItemsBySearch(newValue.toLowerCase()));

        categoryComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(new String[]{Language.getText("all"), Language.getText("local"), Language.getText("external")})));
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> displayItemsByType(cats[categoryComboBox.getSelectionModel().getSelectedIndex()]));

        HBox titleBox = new HBox(5, serversTitle, titleRegion, searchTextField, categoryComboBox);
        titleBox.setPadding(new Insets(0, 5, 0, 5));

        serversStack = new StackPane(scrollPane, noServersLabel);
        StackPane.setAlignment(noServersLabel, Pos.CENTER);
        StackPane.setAlignment(noSearchServersLabel, Pos.CENTER);
        VBox.setVgrow(serversStack, Priority.ALWAYS);

        body = new VBox(5, titleBox, serversStack, bottomRegion, buttonBox);
        body.setPadding(new Insets(10));
        body.setMinHeight(0);

        categoryComboBox.getSelectionModel().selectFirst();
    }

    public void addServer(Server server) {
        if (serversList.containsKey(server)) return;
        ServerCard serverCard = new ServerCard(server, 79);
        serversList.put(server, serverCard);
        serverBox.getChildren().add(serverCard);
        if (serversStack.getChildren().size() > 1 && (serversStack.getChildren().get(1).equals(noServersLabel) || serversStack.getChildren().get(1).equals(noSearchServersLabel)))
            serversStack.getChildren().remove(1);

        displayItemsByType(cats[categoryComboBox.getSelectionModel().getSelectedIndex()]);
        if (!searchTextField.getText().isEmpty()) displayItemsBySearch(searchTextField.getText());
    }

    public void removeServer(Server server) {
        if (!serversList.containsKey(server)) return;
        serverBox.getChildren().remove(serversList.get(server));
        serversList.remove(server);

        if (serversList.size() == 0) {
            serversStack.getChildren().add(noServersLabel);
        }
    }

    private void displayItemsByType(String type) {
        for (Server server : serversList.keySet()) {
            if (type.equalsIgnoreCase("all")) {
                if (!serverBox.getChildren().contains(serversList.get(server)))
                    if (searchTextField.getText().isEmpty() || server.getName().toLowerCase().startsWith(searchTextField.getText().toLowerCase()))
                        serverBox.getChildren().add(serversList.get(server));
            } else {
                if (serverBox.getChildren().contains(serversList.get(server))) {
                    if (server.getType() != ServerType.valueOf(type.toUpperCase())) {
                        if (searchTextField.getText().isEmpty() || server.getName().toLowerCase().startsWith(searchTextField.getText().toLowerCase()))
                            serverBox.getChildren().remove(serversList.get(server));
                    }
                } else {
                    if (server.getType() == ServerType.valueOf(type.toUpperCase())) {
                        if (searchTextField.getText().isEmpty() || server.getName().toLowerCase().startsWith(searchTextField.getText().toLowerCase()))
                            serverBox.getChildren().add(serversList.get(server));
                    }
                }
            }
        }

        if (serversList.size() > 0) {
            if (serverBox.getChildren().size() == 0) {
                if (serversStack.getChildren().size() == 1) serversStack.getChildren().add(noSearchServersLabel);
            } else {
                if (serversStack.getChildren().size() > 1 && serversStack.getChildren().get(1).equals(noSearchServersLabel))
                    serversStack.getChildren().remove(1);
            }
        }
    }

    private void displayItemsBySearch(String search) {
        for (Server server : serversList.keySet()) {
            if (cats[categoryComboBox.getSelectionModel().getSelectedIndex()].equalsIgnoreCase("all") || server.getType() == ServerType.valueOf(cats[categoryComboBox.getSelectionModel().getSelectedIndex()].toUpperCase())) {
                if (serverBox.getChildren().contains(serversList.get(server))) {
                    if (!server.getName().toLowerCase().startsWith(search)) {
                        serverBox.getChildren().remove(serversList.get(server));
                    }
                } else {
                    if (server.getName().toLowerCase().startsWith(search)) {
                        serverBox.getChildren().add(serversList.get(server));
                    }
                }
            }
        }

        if (serversList.size() > 0) {
            if (serverBox.getChildren().size() == 0) {
                if (serversStack.getChildren().size() == 1) serversStack.getChildren().add(noSearchServersLabel);
            } else {
                if (serversStack.getChildren().size() > 1 && serversStack.getChildren().get(1).equals(noSearchServersLabel))
                    serversStack.getChildren().remove(1);
            }
        }
    }

    public ServerCard getServerCardByServer(Server server) {
        return serversList.get(server);
    }

    @Override
    public Pane getContent() {
        return body;
    }

    @Override
    public TabType getType() {
        return TabType.MAIN;
    }

    @Override
    public String getTitle() {
        return Language.getText("servers");
    }

    @Override
    public Image getIcon() {
        return FileManager.SERVER_ICON;
    }
}
