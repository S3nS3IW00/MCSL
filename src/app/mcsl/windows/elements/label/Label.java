package app.mcsl.windows.elements.label;

public class Label extends javafx.scene.control.Label {

    public Label(String text) {
        setText(text);
        setId(LabelType.DEFAULT.getId());
    }

    public Label(String text, int fontSize) {
        setText(text);
        setId(LabelType.DEFAULT.getId());
        setStyle("-fx-font-size: " + fontSize);
    }

    public Label(String text, LabelType type) {
        setText(text);
        setId(type.getId());
    }

    public Label(String text, LabelType type, LabelColor color) {
        setText(text);
        setId(type.getId());
        setStyle("-fx-text-fill: " + color.getColor());
    }

    public Label(String text, LabelType type, int fontSize) {
        setText(text);
        setId(type.getId());
        setStyle("-fx-font-size: " + fontSize);
    }

    public Label(String text, LabelType type, int fontSize, LabelColor color) {
        setText(text);
        setId(type.getId());
        setStyle("-fx-text-fill: " + color.getColor() + ";-fx-font-size: " + fontSize);
    }

    public void setLabelColor(LabelColor color) {
        setStyle("-fx-text-fill: " + color.getColor());
    }
}
