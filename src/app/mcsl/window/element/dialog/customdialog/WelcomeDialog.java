package app.mcsl.window.element.dialog.customdialog;

import app.mcsl.manager.Language;
import app.mcsl.window.element.dialog.Dialog;
import app.mcsl.window.element.dialog.DialogType;
import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class WelcomeDialog extends Dialog {

    public WelcomeDialog() {
        super(300, 400, Language.getText("welcometitle"), DialogType.CUSTOM, new VBox());

        Label textLabel = new Label(Language.getText("welcometext"), LabelType.H1);
        textLabel.setMaxWidth(400);
        textLabel.setWrapText(true);

        ScrollPane contentScroll = new ScrollPane();
        contentScroll.setContent(textLabel);
        contentScroll.setFitToWidth(true);
        contentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(contentScroll, Priority.ALWAYS);

        VBox content = new VBox(10, contentScroll);

        setContent(content);

        build();
    }

}
