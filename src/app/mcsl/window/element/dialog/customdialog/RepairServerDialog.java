package app.mcsl.window.element.dialog.customdialog;

import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.file.PropertiesManager;
import app.mcsl.util.DataTypeUtil;
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
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class RepairServerDialog extends Dialog {

    private PropertiesManager settingsProps, serverProps;

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
    private TextField ramTextField;
    private CheckBox autostartCheckBox;

    private String serverFile;
    private int ramInMB;

    //Step 2 - External
    private TextField pluginPortTextField, serverIpTextField, usernameTextField;
    private PasswordField passwordTextField;

    private int pluginPort;
    private String serverIp, username, password;

    public RepairServerDialog(String serverName, PropertiesManager settingsProps, PropertiesManager serverProps) {
        super(450, 400, Language.getText("repairserver"), DialogType.CUSTOM, new VBox());

        this.settingsProps = settingsProps;
        this.serverProps = serverProps;

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
        typeComboBox.getSelectionModel().select(settingsProps.hasProp("type") && ServerType.getFromDisplayName(settingsProps.getProp("type").toUpperCase()) != null ? ServerType.getFromDisplayName(settingsProps.getProp("type")) : 0);
        typeComboBox.setPrefWidth(200);
        typeComboBox.getSelectionModel().selectFirst();

        serverFileComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(FileManager.getServerFilesFolder().list())));
        serverFileComboBox.getSelectionModel().select(settingsProps.hasProp("serverfile") ? settingsProps.getProp("serverfile") : 0);
        serverFileComboBox.setPrefWidth(200);

        serverNameTextField = new TextField(serverName, InputType.LETTERS_AND_NUMBERS);
        serverNameTextField.setDisable(true);
        serverNameTextField.setMaxWidth(200);
        serverNameTextField.setPromptText(Language.getText("servername"));

        serverPortTextField = new TextField("25565", InputType.ONLY_NUMBERS);
        serverPortTextField.setMaxWidth(200);
        serverPortTextField.setPromptText("Port");

        ramTextField = new TextField(settingsProps.hasProp("ram") ? settingsProps.getProp("ram") : "", InputType.ONLY_NUMBERS);
        ramTextField.setMaxWidth(200);
        ramTextField.setPromptText(Language.getText("maxram"));

        autostartCheckBox = new CheckBox(Language.getText("autostart"));

        pluginPortTextField = new TextField(settingsProps.hasProp("pluginport") ? settingsProps.getProp("pluginport") : "", InputType.ONLY_NUMBERS);
        pluginPortTextField.setMaxWidth(200);
        pluginPortTextField.setPromptText("Plugin port");

        serverIpTextField = new TextField(settingsProps.hasProp("address") ? settingsProps.getProp("address") : "");
        serverIpTextField.setMaxWidth(200);
        serverIpTextField.setPromptText(Language.getText("ipaddress"));

        usernameTextField = new TextField(settingsProps.hasProp("username") ? settingsProps.getProp("username") : "");
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
                close();
            }
        });

        addButton(nextButton, cancelButton);

        build();

        setStepContent(stepIndex);
    }

    private void setStepContent(int stepIndex) {
        if (stepIndex < 2) {
            switch (stepIndex) {
                case 0:
                    titleLabel.setText(Language.getText("addstep1title"));
                    descriptionLabel.setText(Language.getText("repairstep1description"));
                    inputBox.getChildren().clear();
                    inputBox.getChildren().addAll(serverNameTextField, typeComboBox, serverPortTextField);

                    cancelButton.setText(Language.getText("cancel"));
                    nextButton.setText(Language.getText("next"));
                    break;
                case 1:
                    if (!serverNameTextField.getText().isEmpty() && !serverPortTextField.getText().isEmpty() && DataTypeUtil.isInt(serverPortTextField.getText())) {
                        serverName = serverNameTextField.getText();
                        serverType = ServerType.getFromDisplayName(typeComboBox.getSelectionModel().getSelectedItem().toString()).getTypeName();
                        serverPort = Integer.parseInt(serverPortTextField.getText());

                        inputBox.getChildren().clear();
                        switch (ServerType.valueOf(serverType.toUpperCase())) {
                            case LOCAL:
                                inputBox.getChildren().addAll(serverFileComboBox, ramTextField, autostartCheckBox);
                                titleLabel.setText(Language.getText("addstep2localtitle"));
                                descriptionLabel.setText(Language.getText("repairstep2localdescription"));
                                break;
                            case EXTERNAL:
                                inputBox.getChildren().addAll(pluginPortTextField, serverIpTextField, usernameTextField, passwordTextField);
                                titleLabel.setText(Language.getText("addstep2externaltitle"));
                                descriptionLabel.setText(Language.getText("addstep2externaldescription"));
                                break;
                        }
                        cancelButton.setText(Language.getText("back"));
                        nextButton.setText(Language.getText("done"));
                    } else {
                        showError(Language.getText("mustfillallfields"));
                        this.stepIndex--;
                    }
                    break;
            }
        } else {
            switch (ServerType.valueOf(serverType.toUpperCase())) {
                case LOCAL:
                case BUNGEE:
                    if (!ramTextField.getText().isEmpty() && DataTypeUtil.isInt(ramTextField.getText())) {
                        ramInMB = Integer.parseInt(ramTextField.getText());
                        serverFile = serverFileComboBox.getSelectionModel().getSelectedItem().toString();

                        serverProps.setProp("server-port", serverPort + "");
                        settingsProps.setProp("type", "local");
                        settingsProps.setProp("ram", ramInMB + "");
                        settingsProps.setProp("serverfile", serverFile);
                        settingsProps.setProp("autostart", autostartCheckBox.isSelected());
                        settingsProps.setProp("customVmOptions", "");

                        close();
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
                        password = passwordTextField.getText();

                        settingsProps.setProp("pluginport", pluginPort + "");
                        settingsProps.setProp("type", "external");
                        settingsProps.setProp("address", serverIp);
                        settingsProps.setProp("username", username);
                        settingsProps.setProp("password", password);
                        settingsProps.setProp("port", serverPort + "");

                        close();
                    } else {
                        showError(Language.getText("mustfillallfields"));
                        this.stepIndex--;
                    }
                    break;
            }
        }
    }

    private void showError(String text) {
        if (!inputBox.getChildren().contains(errorLabel)) inputBox.getChildren().add(errorLabel);
        errorLabel.setText(text);
    }

    public void reset() {
        stepIndex = 0;
        setStepContent(stepIndex);

        serverNameTextField.clear();
        serverPortTextField.clear();
        ramTextField.clear();
        serverIpTextField.clear();
        pluginPortTextField.clear();
        usernameTextField.clear();
        passwordTextField.clear();
    }
}

