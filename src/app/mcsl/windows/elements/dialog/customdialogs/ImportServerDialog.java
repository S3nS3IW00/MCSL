package app.mcsl.windows.elements.dialog.customdialogs;

import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.file.PropertiesManager;
import app.mcsl.utils.DataTypeUtil;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelColor;
import app.mcsl.windows.elements.label.LabelType;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
    private TextField serverNameTextField, serverPortTextField;

    private String serverName;
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

        serverFileComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(FileManager.getServerFilesFolder().list())));
        serverFileComboBox.getSelectionModel().select(settingsProps.hasProp("serverfile") ? settingsProps.getProp("serverfile") : 0);
        serverFileComboBox.setPrefWidth(200);
        serverFileComboBox.getSelectionModel().selectFirst();

        serverNameTextField = new TextField();
        serverNameTextField.setMaxWidth(200);
        serverNameTextField.setPromptText(Language.getText("servername"));

        serverPortTextField = new TextField(serverProps.hasProp("server-port") ? serverProps.getProp("server-port") : "");
        serverPortTextField.setMaxWidth(200);
        serverPortTextField.setPromptText("Port");

        ramTextField = new TextField(settingsProps.hasProp("ram") ? settingsProps.getProp("ram") : "");
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
                    inputBox.getChildren().addAll(serverNameTextField, serverPortTextField);

                    cancelButton.setText(Language.getText("cancel"));
                    nextButton.setText(Language.getText("next"));
                    break;
                case 1:
                    if (!serverNameTextField.getText().isEmpty() && !serverPortTextField.getText().isEmpty() && DataTypeUtil.isInt(serverPortTextField.getText())) {
                        serverName = serverNameTextField.getText();
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
                settingsProps.setProp("type", "local");
                settingsProps.setProp("ram", ramInMB + "");
                settingsProps.setProp("serverfile", serverFile);
                settingsProps.setBoolProp("autostart", autostartCheckBox.isSelected());

                FileManager.importServer(serverName, location);

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


