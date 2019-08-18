package app.mcsl.windows.elements.dialog.customdialogs;

import app.mcsl.managers.Language;
import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelType;
import javafx.scene.layout.VBox;

public class WelcomeDialog extends Dialog {

    public WelcomeDialog() {
        super(300, 400, Language.getText("welcometitle"), DialogType.CUSTOM, new VBox());

        Label textLabel = new Label(Language.getText("welcometext"), LabelType.H1);
        textLabel.setMaxWidth(400);
        textLabel.setWrapText(true);

        VBox content = new VBox(10, textLabel);
        setContent(content);

        build();
    }

}
