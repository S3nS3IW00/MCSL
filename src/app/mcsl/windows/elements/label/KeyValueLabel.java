package app.mcsl.windows.elements.label;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class KeyValueLabel extends VBox {

    private Label keyLabel, valueLabel;
    private LabelColor keyColor, valueColor;

    public KeyValueLabel(String key, String value) {
        keyColor = LabelColor.THIRDCOLOR;
        valueColor = LabelColor.THIRDCOLOR;
        keyLabel = new Label(key, LabelType.H2, LabelColor.THIRDCOLOR);
        valueLabel = new Label(value, LabelType.H3, LabelColor.THIRDCOLOR);
        valueLabel.setPadding(new Insets(0, 0, 0, 20));

        getChildren().addAll(keyLabel, valueLabel);
        setSpacing(5);
    }

    public KeyValueLabel(String key, String value, LabelColor colors) {
        keyColor = colors;
        valueColor = colors;
        keyLabel = new Label(key, LabelType.H2, colors);
        valueLabel = new Label(value, LabelType.H3, colors);
        valueLabel.setPadding(new Insets(0, 0, 0, 20));

        getChildren().addAll(keyLabel, valueLabel);
        setSpacing(5);
    }

    public KeyValueLabel(String key, LabelColor keyColor, String value, LabelColor valueColor) {
        this.keyColor = keyColor;
        this.valueColor = valueColor;
        keyLabel = new Label(key, LabelType.H2, keyColor);
        valueLabel = new Label(value, LabelType.H3, valueColor);
        valueLabel.setPadding(new Insets(0, 0, 0, 20));

        getChildren().addAll(keyLabel, valueLabel);
        setSpacing(5);
    }

    public void setOnValueClick(EventHandler<MouseEvent> event) {
        valueLabel.setOnMouseClicked(event);
        valueLabel.styleProperty().bind(
                Bindings
                        .when(valueLabel.hoverProperty())
                        .then(
                                new SimpleStringProperty("-fx-text-fill: -fx-defdarkcolor"))
                        .otherwise(
                                new SimpleStringProperty("-fx-text-fill: " + valueColor.getColor() + ";")
                        )
        );
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

}
