package app.mcsl.window.element.dialog.customdialog;

import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.file.PropertiesManager;
import app.mcsl.util.DataTypeUtil;
import app.mcsl.util.EnumUtil;
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
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class ImportServerDialog extends Dialog {

    private PropertiesManager settingsProps, serverProps;

    private VBox content, inputBox;

    private Label titleLabel, descriptionLabel, errorLabel;
    private Button nextButton, cancelButton;

    private int stepIndex = 0;

    private String location;

    //Step 1
    private ComboBox typeComboBox;
    private TextField serverNameTextField, serverPortTextField;

    private String serverName, serverType;
    private int serverPort;

    //Step 2 - Local
    private ComboBox serverFileComboBox;
    private TextField ramTextField;
    private CheckBox autostartCheckBox, eulaCheckBox;

    private String serverFile;
    private int ramInMB;

    public ImportServerDialog(String location, PropertiesManager settingsProps, PropertiesManager serverProps) {
        super(450, 400, Language.getText("importserver"), DialogType.CUSTOM, new VBox());

        this.location = location;
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
        typeComboBox.getSelectionModel().clearSelection(1);
        typeComboBox.getSelectionModel().select(settingsProps.hasProp("type") && !settingsProps.getProp("type").equalsIgnoreCase("external") && EnumUtil.isInEnum(settingsProps.getProp("type").toUpperCase(), ServerType.class) ? Language.getText(ServerType.valueOf(settingsProps.getProp("type").toUpperCase()).getTypeName()) : 0);
        typeComboBox.setPrefWidth(200);

        serverFileComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(FileManager.getServerFilesFolder().list())));
        serverFileComboBox.getSelectionModel().select(settingsProps.hasProp("serverfile") ? settingsProps.getProp("serverfile") : 0);
        serverFileComboBox.setPrefWidth(200);

        serverNameTextField = new TextField(InputType.LETTERS_AND_NUMBERS);
        serverNameTextField.setMaxWidth(200);
        serverNameTextField.setPromptText(Language.getText("servername"));

        serverPortTextField = new TextField(serverProps.hasProp("server-port") ? serverProps.getProp("server-port") : "", InputType.ONLY_NUMBERS);
        serverPortTextField.setMaxWidth(200);
        serverPortTextField.setPromptText("Port");

        ramTextField = new TextField(settingsProps.hasProp("ram") ? settingsProps.getProp("ram") : "", InputType.ONLY_NUMBERS);
        ramTextField.setMaxWidth(200);
        ramTextField.setPromptText(Language.getText("maxram"));

        autostartCheckBox = new CheckBox(Language.getText("autostart"));

        eulaCheckBox = new CheckBox(Language.getText("accepteula"));
        eulaCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                nextButton.setDisable(false);
                eulaCheckBox.setDisable(true);
            }
        });

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
                    nextButton.setDisable(false);
                    break;
                case 1:
                    if (!serverNameTextField.getText().isEmpty() && !serverPortTextField.getText().isEmpty() && DataTypeUtil.isInt(serverPortTextField.getText())) {
                        serverName = serverNameTextField.getText();
                        serverType = ServerType.getByDisplayName(typeComboBox.getSelectionModel().getSelectedItem().toString()).getTypeName();
                        serverPort = Integer.parseInt(serverPortTextField.getText());

                        inputBox.getChildren().clear();

                        inputBox.getChildren().addAll(serverFileComboBox, ramTextField, autostartCheckBox, eulaCheckBox);
                        titleLabel.setText(Language.getText("addstep2localtitle"));
                        descriptionLabel.setText(Language.getText("repairstep2localdescription"));

                        cancelButton.setText(Language.getText("back"));
                        nextButton.setText(Language.getText("done"));
                        nextButton.setDisable(true);
                    } else {
                        showError(Language.getText("mustfillallfields"));
                        this.stepIndex--;
                    }
                    break;
            }
        } else {
            if (!ramTextField.getText().isEmpty() && DataTypeUtil.isInt(ramTextField.getText())) {
                ramInMB = Integer.parseInt(ramTextField.getText());
                serverFile = serverFileComboBox.getSelectionModel().getSelectedItem().toString();

                serverProps.setProp("server-port", serverPort + "");
                settingsProps.setProp("type", serverType);
                settingsProps.setProp("ram", ramInMB + "");
                settingsProps.setProp("serverfile", serverFile);
                settingsProps.setProp("autostart", autostartCheckBox.isSelected());

                FileManager.importServer(serverName, location, ServerType.valueOf(serverType.toUpperCase()));

                close();
            } else {
                showError(Language.getText("mustfillallfields"));
                this.stepIndex--;
            }
        }
    }

    private void showError(String text) {
        if (!inputBox.getChildren().contains(errorLabel)) inputBox.getChildren().add(errorLabel);
        errorLabel.setText(text);
    }
}


