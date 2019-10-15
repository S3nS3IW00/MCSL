package app.mcsl.window.element.dialog.type;

import app.mcsl.window.element.dialog.Dialog;
import app.mcsl.window.element.dialog.DialogType;

public class AlertDialog extends Dialog {

    public AlertDialog(int height, int width, String title, String text, AlertType type) {
        super(height, width, title, DialogType.ALERT, text, type.getColor());

        build();
    }

}
