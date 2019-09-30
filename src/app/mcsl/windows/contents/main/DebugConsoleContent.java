package app.mcsl.windows.contents.main;

import app.mcsl.events.LogEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.LogLevel;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.tab.TabClass;
import app.mcsl.managers.tab.TabType;
import app.mcsl.windows.Template;
import app.mcsl.windows.elements.IconCard;
import app.mcsl.windows.elements.coloredtextflow.ColoredTextFlow;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.regex.Matcher;

public class DebugConsoleContent implements TabClass {

    private VBox body = new VBox();
    private ColoredTextFlow logFlow;
    private ScrollPane consoleScroll;
    private Label warnCountLabel, errorCountLabel, exceptionCountLabel;
    private boolean isStackTrace = false;

    public void init() {
        warnCountLabel = new Label(Language.getText("warningcount") + ": " + Logger.getWarnCount());
        warnCountLabel.setStyle("-fx-text-fill: -fx-themetypecolor;\n" +
                "    -fx-font-size: 15px;\n" +
                "    -fx-font-weight: bold;");
        warnCountLabel.setWrapText(true);
        errorCountLabel = new Label(Language.getText("errorcount") + ": " + Logger.getErrorCount());
        errorCountLabel.setStyle("-fx-text-fill: -fx-themetypecolor;\n" +
                "    -fx-font-size: 15px;\n" +
                "    -fx-font-weight: bold;");
        errorCountLabel.setWrapText(true);
        exceptionCountLabel = new Label(Language.getText("exceptioncount") + ": " + Logger.getExceptionCount());
        exceptionCountLabel.setStyle("-fx-text-fill: -fx-themetypecolor;\n" +
                "    -fx-font-size: 15px;\n" +
                "    -fx-font-weight: bold;");
        exceptionCountLabel.setWrapText(true);

        IconCard warnCountIconCard = new IconCard(new ImageView(FileManager.WARNING_ICON), warnCountLabel, 200, 50);
        warnCountIconCard.prefWidthProperty().bind(Template.getStage().getScene().widthProperty());

        IconCard errorCountIconCard = new IconCard(new ImageView(FileManager.ERROR_ICON), errorCountLabel, 200, 50);
        errorCountIconCard.prefWidthProperty().bind(Template.getStage().getScene().widthProperty());

        IconCard exceptionCountIconCard = new IconCard(new ImageView(FileManager.EXCEPTION_ICON), exceptionCountLabel, 200, 50);
        exceptionCountIconCard.prefWidthProperty().bind(Template.getStage().getScene().widthProperty());

        HBox iconBox = new HBox(10, warnCountIconCard, errorCountIconCard, exceptionCountIconCard);

        logFlow = new ColoredTextFlow(13);
        logFlow.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        consoleScroll = new ScrollPane();
        consoleScroll.setId("console");
        consoleScroll.setContent(logFlow);
        VBox.setVgrow(consoleScroll, Priority.ALWAYS);
        HBox.setHgrow(consoleScroll, Priority.ALWAYS);

        body.setSpacing(10);
        body.setPadding(new Insets(10));
        body.getChildren().addAll(iconBox, consoleScroll);

        synchronized ((Object) initFileRead()) {
            LogEvent.addListener(((logLevel, line, text) -> Platform.runLater(() -> {
                log(line);
                consoleScroll.setVvalue(1.0);
            })));
            Logger.getWarnCountProperty().addListener((observable, oldValue, newValue) -> warnCountLabel.setText(Language.getText("warningcount") + ": " + newValue.intValue()));
            Logger.getErrorCountProperty().addListener((observable, oldValue, newValue) -> errorCountLabel.setText(Language.getText("errorcount") + ": " + newValue.intValue()));
            Logger.getExceptionCountProperty().addListener((observable, oldValue, newValue) -> exceptionCountLabel.setText(Language.getText("exceptioncount") + ": " + newValue.intValue()));
        }
    }

    private boolean initFileRead() {
        try {
            for (String line : Files.readAllLines(Logger.LOG_FILE.toPath(), Charset.defaultCharset())) {
                log(line);
            }
            return true;
        } catch (IOException e) {
            Logger.exception(e);
            return true;
        }
    }

    @Override
    public Node getContent() {
        return body;
    }

    @Override
    public TabType getType() {
        return TabType.MAIN;
    }

    @Override
    public String getTitle() {
        return Language.getText("log");
    }

    private void log(String line) {
        LogLevel level = getLogLevel(line);
        if (level == null) {
            if (isStackTrace) {
                logFlow.appendLine("§c" + line);
                return;
            }
            logFlow.appendLine(line);
            return;
        }

        isStackTrace = false;
        switch (level) {
            case INFO:
                line = line.replace(level.name(), "&b" + level.name() + "&r");
                break;
            case WARN:
                line = "&e" + line;
                break;
            case ERROR:
            case EXCEPTION:
                line = "&c" + line;
                isStackTrace = true;
                break;
            case DEBUG:
                line = line.replace(level.name(), "&3" + level.name() + "&r");
                break;
        }
        logFlow.appendLine(line);
    }

    private LogLevel getLogLevel(String text) {
        Matcher matcher = Logger.LOG_PATTERN.matcher(text);
        if (matcher.find()) {
            return LogLevel.valueOf(matcher.group("level"));
        }
        return null;
    }

}
