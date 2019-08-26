package app.mcsl.windows.elements.label;

public enum LabelColor {

    DEFAULT("-fx-defcolor"), SUCCESS("-fx-apply"), WARNING("-fx-warning"), ERROR("-fx-error"), THIRDCOLOR("-fx-thirdcolor");

    String color;

    LabelColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
