package app.mcsl.window.element.dialog.customdialog;

import app.mcsl.manager.Language;
import app.mcsl.manager.logging.Logger;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import app.mcsl.window.element.coloredtextflow.ColoredTextFlow;
import app.mcsl.window.element.dialog.Dialog;
import app.mcsl.window.element.dialog.DialogType;
import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ExceptionDialog extends Dialog {

    public ExceptionDialog(String exception) {
        super(500, 500, Language.getText("error"), DialogType.CUSTOM, new VBox());

        Label textLabel = new Label(Language.getText("errorthrown"), LabelType.H1);
        textLabel.setMaxWidth(500);
        textLabel.setWrapText(true);

        ColoredTextFlow exceptionFlow = new ColoredTextFlow(13);
        exceptionFlow.append(exception);

        Label reportLabel = new Label(Language.getText("clickreport"), LabelType.H1);
        reportLabel.setMaxWidth(500);
        reportLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("console");
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(exceptionFlow);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox content = new VBox(10, textLabel, scrollPane, reportLabel);

        Button reportButton = new Button(Language.getText("reporterror"), ButtonType.APPLY);
        reportButton.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(Logger.LOGS_FOLDER);
                Desktop.getDesktop().browse(new URI("https://github.com/S3nS3IW00/mcserverlauncher/issues/new"));
            } catch (IOException | URISyntaxException ex) {
                Logger.exception(ex);
            }
            close();
        });

        setContent(content);
        build();

        keepDefaultButton(true);
        addButton(reportButton);
    }

}

