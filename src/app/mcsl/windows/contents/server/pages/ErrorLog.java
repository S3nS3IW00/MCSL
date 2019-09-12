package app.mcsl.windows.contents.server.pages;

import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.windows.Template;
import app.mcsl.windows.contents.server.StatusType;
import app.mcsl.windows.elements.IconCard;
import app.mcsl.windows.elements.coloredtextflow.ColoredTextFlow;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ErrorLog extends VBox {

    private ColoredTextFlow startUpLog, runtimeLog;
    private ScrollPane consoleScroll;
    private Label startUpProblemsLabel, runProblemsLabel, logTitleLabel;
    private int startUpProblemsCount = 0, runProblemsCount = 0;

    public ErrorLog() {
        startUpProblemsLabel = new Label(Language.getText("startupproblems") + ": 0");
        startUpProblemsLabel.setStyle("-fx-text-fill: -fx-themetypecolor;\n" +
                "    -fx-font-size: 20px;\n" +
                "    -fx-font-weight: bold;");
        runProblemsLabel = new Label(Language.getText("runtimeproblems") + ": 0");
        runProblemsLabel.setStyle("-fx-text-fill: -fx-themetypecolor;\n" +
                "    -fx-font-size: 20px;\n" +
                "    -fx-font-weight: bold;");

        logTitleLabel = new Label(Language.getText("startupproblems"));
        logTitleLabel.setStyle("-fx-text-fill: -fx-defcolor;\n" +
                "    -fx-font-size: 17px;\n" +
                "    -fx-font-weight: bold;");

        IconCard startUpErrorIconCard = new IconCard(new ImageView(FileManager.ERROR_ICON), startUpProblemsLabel, 200, 50);
        startUpErrorIconCard.prefWidthProperty().bind(Template.getStage().getScene().widthProperty());
        startUpErrorIconCard.setOnMouseClicked(e -> {
            consoleScroll.setContent(startUpLog);
            logTitleLabel.setText(Language.getText("startupproblems"));
        });

        IconCard runtimeErrorIconCard = new IconCard(new ImageView(FileManager.ERROR_ICON), runProblemsLabel, 200, 50);
        runtimeErrorIconCard.prefWidthProperty().bind(Template.getStage().getScene().widthProperty());
        runtimeErrorIconCard.setOnMouseClicked(e -> {
            consoleScroll.setContent(runtimeLog);
            logTitleLabel.setText(Language.getText("runtimeproblems"));
        });

        HBox iconBox = new HBox(10, startUpErrorIconCard, runtimeErrorIconCard);

        startUpLog = new ColoredTextFlow(13);
        startUpLog.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        runtimeLog = new ColoredTextFlow(13);
        runtimeLog.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        consoleScroll = new ScrollPane();
        consoleScroll.setId("console");
        consoleScroll.setContent(startUpLog);
        VBox.setVgrow(consoleScroll, Priority.ALWAYS);
        HBox.setHgrow(consoleScroll, Priority.ALWAYS);

        setSpacing(10);
        getChildren().addAll(iconBox, logTitleLabel, consoleScroll);
    }

    public void log(String line, StatusType type, boolean newMessage) {
        switch (type) {
            case STARTING:
                startUpLog.appendLine(line);
                if (newMessage) {
                    startUpProblemsCount++;
                    startUpProblemsLabel.setText(Language.getText("startupproblems") + ": " + startUpProblemsCount);
                }
                break;
            case RUNNING:
                runtimeLog.appendLine(line);
                if (newMessage) {
                    runProblemsCount++;
                    runProblemsLabel.setText(Language.getText("runtimeproblems") + ": " + runProblemsCount);
                }
        }
    }

    public void clear() {
        startUpLog.getChildren().clear();
        runtimeLog.getChildren().clear();
        startUpProblemsCount = 0;
        runProblemsCount = 0;
        startUpProblemsLabel.setText(Language.getText("startupproblems") + ": 0");
        runProblemsLabel.setText(Language.getText("runtimeproblems") + ": 0");
        logTitleLabel.setText(Language.getText("startupproblems"));
        consoleScroll.setContent(startUpLog);
    }
}
