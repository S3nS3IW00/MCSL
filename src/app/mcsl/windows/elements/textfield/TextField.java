package app.mcsl.windows.elements.textfield;

import app.mcsl.windows.elements.tooltip.Tooltip;
import app.mcsl.windows.elements.tooltip.TooltipType;
import javafx.geometry.Point2D;

public class TextField extends javafx.scene.control.TextField {

    private InputType type = InputType.ANY;
    private String enteredText;

    private Tooltip tooltip = new Tooltip(TooltipType.ERROR);

    public TextField() {
    }

    public TextField(String text) {
        setText(text);
    }

    public TextField(String text, InputType type) {
        this.type = type;
        setText(text);
        init();
    }

    public TextField(InputType type) {
        this.type = type;
        init();
    }

    private void init() {
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && tooltip.isShowing()) {
                tooltip.hide();
            }
        });

        if (type == InputType.EMAIL) {
            textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.matches(type.getPattern())) {
                    tooltip.hide();
                } else {
                    showTooltip(type.getErrorString());
                }
            });
        } else {
            textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.length() == 0) {
                    enteredText = "";
                    return;
                }
                String enteredChar = newValue.substring(newValue.length() - 1);
                if (enteredChar.matches(type.getPattern())) {
                    tooltip.hide();
                    enteredText = newValue;
                } else {
                    setText(enteredText);
                    showTooltip(type.getErrorString());
                }
            });
        }
    }

    private void showTooltip(String text) {
        if (tooltip.isShowing()) return;
        tooltip.setText(text);
        Point2D p = localToScene(0.0, 0.0);
        tooltip.show(this,
                p.getX() + getScene().getX() + getScene().getWindow().getX() + getWidth(),
                p.getY() + getScene().getY() + getScene().getWindow().getY());
    }

}
