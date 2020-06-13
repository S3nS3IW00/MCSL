package app.mcsl.window.element.dialog.customdialog;

import app.mcsl.event.DirectoryChangeEvent;
import app.mcsl.manager.HashManager;
import app.mcsl.manager.Language;
import app.mcsl.manager.file.DirectoryType;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.logging.Logger;
import app.mcsl.manager.server.ServerAction;
import app.mcsl.manager.server.ServersManager;
import app.mcsl.util.DataTypeUtil;
import app.mcsl.window.Template;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.ServerType;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import app.mcsl.window.element.dialog.Dialog;
import app.mcsl.window.element.dialog.DialogType;
import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelColor;
import app.mcsl.window.element.label.LabelType;
import app.mcsl.window.element.textfield.InputType;
import app.mcsl.window.element.textfield.TextField;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class AddServerDialog extends Dialog {

    private VBox content, inputBox;

    private Label titleLabel, descriptionLabel, errorLabel;
    private Button nextButton, cancelButton;

    private int stepIndex = 0;

    //Step 1
    private ComboBox typeComboBox;
    private TextField serverNameTextField, serverPortTextField;

    private String serverName, serverType;
    private int serverPort;

    //Step 2 - Local
    private ComboBox serverFileComboBox;
    private TextField ramTextField, customLocationTextField;
    private CheckBox eulaCheckBox, autoStartCheckBox;
    private Button chooseLocationButton;

    private HBox chooseLocationBox;

    private String serverFile;
    private int ramInMB;

    //Step 2 - External
    private TextField pluginPortTextField, serverIpTextField, usernameTextField;
    private PasswordField passwordTextField;

    private int pluginPort;
    private String serverIp, username, password;

    public AddServerDialog() {
        super(500, 450, Language.getText("addserver"), DialogType.CUSTOM, new VBox());

        titleLabel = new Label("", LabelType.H1);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(350);
        titleLabel.setWrapText(true);

        errorLabel = new Label("", LabelType.H1, LabelColor.ERROR);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMaxWidth(350);
        errorLabel.setWrapText(true);

        descriptionLabel = new Label("", LabelType.H1);
        descriptionLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setMaxWidth(350);
        descriptionLabel.setWrapText(true);

        typeComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(ServerType.displayValues())));
        typeComboBox.setPrefWidth(200);
        typeComboBox.getSelectionModel().selectFirst();

        serverFileComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(FileManager.getServerFilesFolder().list())));
        serverFileComboBox.setPrefWidth(200);
        serverFileComboBox.getSelectionModel().selectFirst();

        serverNameTextField = new TextField(InputType.LETTERS_AND_NUMBERS);
        serverNameTextField.setMaxWidth(200);
        serverNameTextField.setPromptText(Language.getText("servername"));

        serverPortTextField = new TextField(InputType.ONLY_NUMBERS);
        serverPortTextField.setMaxWidth(200);
        serverPortTextField.setPromptText("Port");

        ramTextField = new TextField(InputType.ONLY_NUMBERS);
        ramTextField.setMaxWidth(200);
        ramTextField.setPromptText(Language.getText("maxram"));

        customLocationTextField = new TextField();
        customLocationTextField.setPromptText(Language.getText("customlocation"));

        chooseLocationButton = new Button(Language.getText("choose"), ButtonType.ACTION_BUTTON);
        chooseLocationButton.setMaxHeight(30);
        chooseLocationButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(Language.getText("choosecustomlocation"));
            File serverLocation = directoryChooser.showDialog(Template.getStage());
            if (serverLocation != null) {
                customLocationTextField.setText(serverLocation.getAbsolutePath() + File.separator + serverName);
            }
        });

        chooseLocationBox = new HBox(customLocationTextField, chooseLocationButton);
        chooseLocationBox.setAlignment(Pos.CENTER);

        eulaCheckBox = new CheckBox(Language.getText("accepteula"));
        eulaCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                nextButton.setDisable(false);
                eulaCheckBox.setDisable(true);
            }
        });

        autoStartCheckBox = new CheckBox(Language.getText("autostart"));

        pluginPortTextField = new TextField(InputType.ONLY_NUMBERS);
        pluginPortTextField.setMaxWidth(200);
        pluginPortTextField.setPromptText("Plugin port");

        serverIpTextField = new TextField();
        serverIpTextField.setMaxWidth(200);
        serverIpTextField.setPromptText(Language.getText("ipaddress"));

        usernameTextField = new TextField();
        usernameTextField.setMaxWidth(200);
        usernameTextField.setPromptText(Language.getText("pluginusername"));

        passwordTextField = new PasswordField();
        passwordTextField.setMaxWidth(200);
        passwordTextField.setPromptText(Language.getText("pluginpassword"));

        inputBox = new VBox(10);
        inputBox.setAlignment(Pos.CENTER);

        content = new VBox(20, titleLabel, inputBox, descriptionLabel);
        content.setAlignment(Pos.CENTER);

        setContent(content);

        nextButton = new Button(Language.getText("next"), ButtonType.APPLY);
        nextButton.setOnAction(e -> {
            stepIndex++;
            setStepContent(stepIndex);
        });

        cancelButton = new Button(Language.getText("cancel"), ButtonType.ERROR);
        cancelButton.setOnAction(e -> {
            if (stepIndex > 0) {
                stepIndex--;
                setStepContent(stepIndex);
            } else {
                closeAndReset();
            }
        });

        addButton(nextButton, cancelButton);

        build();

        setStepContent(stepIndex);

        DirectoryChangeEvent.addListener(type -> {
            if (type == DirectoryType.SERVERFILE) {
                serverFileComboBox.setItems(FXCollections.observableList(Arrays.asList(FileManager.getServerFilesFolder().list())));
                serverFileComboBox.getSelectionModel().selectFirst();
            }
        });
    }

    private void setStepContent(int stepIndex) {
        if (stepIndex < 2) {
            switch (stepIndex) {
                case 0:
                    titleLabel.setText(Language.getText("addstep1title"));
                    descriptionLabel.setText(Language.getText("localserverdescription") + System.lineSeparator() + Language.getText("externalserverdescription"));
                    inputBox.getChildren().clear();
                    inputBox.getChildren().addAll(serverNameTextField, typeComboBox, serverPortTextField);

                    cancelButton.setText(Language.getText("cancel"));
                    nextButton.setText(Language.getText("next"));
                    nextButton.setDisable(false);
                    break;
                case 1:
                    if (!serverNameTextField.getText().isEmpty() && !serverPortTextField.getText().isEmpty() && DataTypeUtil.isInt(serverPortTextField.getText())) {
                        if (!ServersManager.isExists(serverNameTextField.getText())) {

                            serverName = serverNameTextField.getText();
                            if (!serverName.matches("[\\s\\p{L}0-9]+")) {
                                serverName = serverName.replaceAll("[^\\s\\p{L}0-9]", "");
                            }

                            serverType = ServerType.getByDisplayName(typeComboBox.getSelectionModel().getSelectedItem().toString()).getTypeName();
                            serverPort = Integer.parseInt(serverPortTextField.getText());

                            if (ServerType.valueOf(serverType.toUpperCase()) == ServerType.EXTERNAL || FileManager.getServerFilesFolder().listFiles().length > 0) {
                                inputBox.getChildren().clear();
                                switch (ServerType.valueOf(serverType.toUpperCase())) {
                                    case BUNGEE:
                                    case LOCAL:
                                        inputBox.getChildren().addAll(serverFileComboBox, ramTextField, chooseLocationBox, autoStartCheckBox, eulaCheckBox);
                                        titleLabel.setText(Language.getText("addstep2localtitle"));
                                        descriptionLabel.setText(Language.getText("addstep2localdescription"));
                                        nextButton.setDisable(true);
                                        break;
                                    case EXTERNAL:
                                        inputBox.getChildren().addAll(pluginPortTextField, serverIpTextField, usernameTextField, passwordTextField);
                                        titleLabel.setText(Language.getText("addstep2externaltitle"));
                                        descriptionLabel.setText(Language.getText("addstep2externaldescription"));
                                        break;
                                }
                                cancelButton.setText(Language.getText("back"));
                                nextButton.setText(Language.getText("add"));
                            } else {
                                showError(Language.getText("noserverfile"));
                                this.stepIndex--;
                            }
                        } else {
                            showError(Language.getText("serverexistswiththisname"));
                            this.stepIndex--;
                        }
                    } else {
                        showError(Language.getText("mustfillallfields"));
                        this.stepIndex--;
                    }
                    break;
            }
        } else {
            switch (ServerType.valueOf(serverType.toUpperCase())) {
                case LOCAL:
                    if (!ramTextField.getText().isEmpty() && DataTypeUtil.isInt(ramTextField.getText())) {
                        ramInMB = Integer.parseInt(ramTextField.getText());
                        serverFile = serverFileComboBox.getSelectionModel().getSelectedItem().toString();

                        Server server;
                        try {
                            server = FileManager.createServer(serverName,
                                    ServerType.LOCAL,
                                    new String[]{serverPort + "", serverFile + "", ramInMB + "", autoStartCheckBox.isSelected() + ""},
                                    customLocationTextField.getText().isEmpty() ? null : customLocationTextField.getText());
                        } catch (IOException e) {
                            showError(Language.getText("choosendirnotavaliable"));
                            this.stepIndex--;
                            return;
                        }

                        synchronized (server) {
                            ServerAction.add(server);
                            closeAndReset();
                        }
                    } else {
                        showError(Language.getText("mustfillallfields"));
                        this.stepIndex--;
                    }
                    break;
                case EXTERNAL:
                    if (!pluginPortTextField.getText().isEmpty() && DataTypeUtil.isInt(pluginPortTextField.getText()) && !serverIpTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !passwordTextField.getText().isEmpty()) {
                        pluginPort = Integer.parseInt(pluginPortTextField.getText());
                        serverIp = serverIpTextField.getText();
                        username = usernameTextField.getText();
                        password = HashManager.cuttedHash(passwordTextField.getText());

                        try {
                            ServerAction.add(FileManager.createServer(serverName, ServerType.EXTERNAL, new String[]{serverIp, serverPort + "", pluginPort + "", username, password}, null));
                        } catch (IOException e) {
                            Logger.exception(e);
                        }

                        closeAndReset();
                    } else {
                        showError(Language.getText("mustfillallfields"));
                        this.stepIndex--;
                    }
                    break;
                case BUNGEE:
                    if (!ramTextField.getText().isEmpty() && DataTypeUtil.isInt(ramTextField.getText())) {
                        ramInMB = Integer.parseInt(ramTextField.getText());
                        serverFile = serverFileComboBox.getSelectionModel().getSelectedItem().toString();

                        Server server;
                        try {
                            server = FileManager.createServer(serverName,
                                    ServerType.BUNGEE,
                                    new String[]{serverPort + "", serverFile + "", ramInMB + "", autoStartCheckBox.isSelected() + ""},
                                    customLocationTextField.getText().isEmpty() ? null : customLocationTextField.getText());
                        } catch (IOException e) {
                            showError(Language.getText("choosendirnotavaliable"));
                            this.stepIndex--;
                            return;
                        }

                        synchronized (server) {
                            ServerAction.add(server);
                            closeAndReset();
                        }
                    } else {
                        showError(Language.getText("mustfillallfields"));
                        this.stepIndex--;
                    }
                    break;
            }
        }
    }

    private void closeAndReset() {
        reset();
        close();
    }

    private void showError(String text) {
        if (!inputBox.getChildren().contains(errorLabel)) inputBox.getChildren().add(errorLabel);
        errorLabel.setText(text);
    }

    private void reset() {
        stepIndex = 0;
        setStepContent(stepIndex);

        serverNameTextField.clear();
        serverPortTextField.clear();
        ramTextField.clear();
        serverIpTextField.clear();
        pluginPortTextField.clear();
        usernameTextField.clear();
        passwordTextField.clear();
        eulaCheckBox.setSelected(false);
        eulaCheckBox.setDisable(false);
        autoStartCheckBox.setSelected(false);
        customLocationTextField.clear();
    }
}
