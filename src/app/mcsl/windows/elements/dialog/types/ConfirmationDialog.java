package app.mcsl.windows.elements.dialog.types;

import app.mcsl.managers.Language;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;

public abstract class ConfirmationDialog extends Dialog {

    public ConfirmationDialog(int height, int width, String title, String text) {
        super(height, width, title, DialogType.CONFIRMATION, text);

        Button yesButton = new Button(Language.getText("yes"), ButtonType.APPLY);
        yesButton.setOnAction(e -> yesAction());

        Button noButton = new Button(Language.getText("no"), ButtonType.ERROR);
        noButton.setOnAction(e -> noAction());

        addButton(yesButton, noButton);

        build();
    }

    public abstract void yesAction();

    public abstract void noAction();
}
