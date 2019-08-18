package app.mcsl.windows.elements.dialog.types;

import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;

public class AlertDialog extends Dialog {

    public AlertDialog(int height, int width, String title, String text, AlertType type) {
        super(height, width, title, DialogType.ALERT, text, type.getColor());

        build();
    }

}
