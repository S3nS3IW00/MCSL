package app.mcsl.windows.elements.coloredtextflow;

import app.mcsl.windows.elements.coloredtextflow.json.JsonParser;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.simple.parser.ParseException;

public class ColoredTextFlow extends TextFlow {

    private int fontSize;

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
        try {
            for (Text text : new JsonParser(sText).parse()) {
                if (text != null) getChildren().add(text);
            }
        } catch (ParseException e) {
            for (Text text : new ColorParser(sText, fontSize).parse()) {
                if (text != null) getChildren().add(text);
            }
        }
    }

    public void appendLine(String sText) {
        try {
            for (Text text : new JsonParser(sText).parse()) {
                if (text != null) getChildren().add(text);
            }
        } catch (ParseException e) {
            for (Text text : new ColorParser(sText, fontSize).parse()) {
                if (text != null) getChildren().add(text);
            }
        }
        getChildren().add(new Text(System.lineSeparator()));
    }

}
