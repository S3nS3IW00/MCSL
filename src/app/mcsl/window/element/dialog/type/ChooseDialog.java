package app.mcsl.window.element.dialog.type;

import app.mcsl.manager.Language;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import app.mcsl.window.element.dialog.Dialog;
import app.mcsl.window.element.dialog.DialogType;
import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelType;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public abstract class ChooseDialog extends Dialog {

    private Button cancelButton, doneButton;
    private ComboBox comboBox;
    private Label textLabel;

    public ChooseDialog(String title, String text, ObservableList values) {
        super(200, 400, title, DialogType.INPUT, new VBox());

        textLabel = new Label(text, LabelType.H1);
        textLabel.setMaxWidth(400);
        textLabel.setWrapText(true);

        comboBox = new ComboBox(values);
        comboBox.getSelectionModel().selectFirst();
        comboBox.setPrefWidth(200);

        VBox content = new VBox(10, textLabel, comboBox);

        cancelButton = new Button(Language.getText("cancel"), ButtonType.ERROR);
        cancelButton.setOnAction(e -> onCancel());

        doneButton = new Button(Language.getText("done"), ButtonType.APPLY);
        doneButton.setOnAction(e -> onDone(comboBox.getSelectionModel().getSelectedItem().toString()));

        addButton(doneButton, cancelButton);

        setContent(content);

        build();
    }

    public abstract void onDone(String item);

    public abstract void onCancel();

}
