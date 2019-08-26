package app.mcsl.windows.elements.button;

public class Button extends javafx.scene.control.Button {

    public Button(String text, ButtonType type) {
        setText(text);
        setId(type.getId());
    }

    public Button(String text, ButtonType type, int fontSize) {
        setText(text);
        setId(type.getId());
        setStyle("-fx-font-size: " + fontSize);
    }

    public void setType(ButtonType type) {
        setId(type.getId());
    }


}
