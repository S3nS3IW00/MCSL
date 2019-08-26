package app.mcsl.windows.elements.dialog.customdialogs;

import app.mcsl.MainClass;
import app.mcsl.managers.Language;
import app.mcsl.windows.elements.coloredtextflow.ColoredTextFlow;
import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ChangelogDialog extends Dialog {

    public ChangelogDialog() {
        super(400, 400, Language.getText("update"), DialogType.CUSTOM, new VBox());

        Label textLabel = new Label(Language.getText("updated", MainClass.VERSION), LabelType.H1);
        textLabel.setMaxWidth(400);
        textLabel.setWrapText(true);

        ColoredTextFlow changeLogFlow = new ColoredTextFlow(13);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(MainClass.class.getResourceAsStream("/app/mcsl/resources/changelog_" + Language.LANGUAGE + ".txt"), StandardCharsets.UTF_8));
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                changeLogFlow.appendLine(line.replace("\u00A7", "§"));
            }
        } catch (IOException e) {
            //empty catch block
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("console");
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(changeLogFlow);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox content = new VBox(10, textLabel, scrollPane);

        setContent(content);

        build();
    }

}
