package app.mcsl.window.element.coloredtextflow;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ColoredTextFlow extends TextFlow {

    private int fontSize;
    private ChatColor defaultColor = ChatColor.WHITE;

    public ColoredTextFlow(int fontSize) {
        this.fontSize = fontSize;
    }

    public void appendLineSeparator() {
        append(System.lineSeparator());
    }

    public void append(Text text) {
        getChildren().add(text);
    }

    public void appendLine(Text text) {
        getChildren().addAll(text, new Text(System.lineSeparator()));
    }

    public void append(String sText) {
        for (Text text : new ColorParser(sText, fontSize, defaultColor).parse()) {
            if (text != null) getChildren().add(text);
        }
    }

    public void appendLine(String sText) {
        for (Text text : new ColorParser(sText, fontSize, defaultColor).parse()) {
            if (text != null) getChildren().add(text);
        }
        getChildren().add(new Text(System.lineSeparator()));
    }

    public ChatColor getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(ChatColor defaultColor) {
        this.defaultColor = defaultColor;
    }
}
