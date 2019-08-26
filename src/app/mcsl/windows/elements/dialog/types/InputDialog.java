package app.mcsl.windows.elements.dialog.types;

import app.mcsl.managers.Language;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public abstract class InputDialog extends Dialog {

    private Button cancelButton, doneButton;
    private TextField inputField;
    private Label textLabel;

    public InputDialog(String title, String text, boolean inputCanBeNull) {
        super(200, 400, title, DialogType.INPUT, new VBox());

        textLabel = new Label(text, LabelType.H1);
        textLabel.setMaxWidth(400);
        textLabel.setWrapText(true);

        inputField = new TextField();
        inputField.setPrefWidth(400);
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!inputCanBeNull) {
                if (newValue.length() > 0) {
                    doneButton.setDisable(false);
                } else {
                    doneButton.setDisable(true);
                }
            }
        });

        VBox content = new VBox(10, textLabel, inputField);

        cancelButton = new Button(Language.getText("cancel"), ButtonType.ERROR);
        cancelButton.setOnAction(e -> onCancel());

        doneButton = new Button(Language.getText("done"), ButtonType.APPLY);
        if (!inputCanBeNull) doneButton.setDisable(true);
        doneButton.setOnAction(e -> onDone(inputField.getText().length() == 0 ? null : inputField.getText()));

        addButton(doneButton, cancelButton);

        setContent(content);

        build();
    }

    public abstract void onDone(String input);

    public abstract void onCancel();

}
