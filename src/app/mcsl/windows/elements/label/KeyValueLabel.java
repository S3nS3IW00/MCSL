package app.mcsl.windows.elements.label;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class KeyValueLabel extends VBox {

    public KeyValueLabel(String key, String value){
        Label keyLabel = new Label(key, LabelType.H2);
        Label valueLabel = new Label(value, LabelType.H3);
        valueLabel.setPadding(new Insets(0, 0, 0, 20));

        getChildren().addAll(keyLabel, valueLabel);
        setSpacing(5);
    }

    public KeyValueLabel(String key, String value, LabelColor colors){
        Label keyLabel = new Label(key, LabelType.H2, colors);
        Label valueLabel = new Label(value, LabelType.H3, colors);
        valueLabel.setPadding(new Insets(0, 0, 0, 20));

        getChildren().addAll(keyLabel, valueLabel);
        setSpacing(5);
    }

    public KeyValueLabel(String key, LabelColor keyColor, String value, LabelColor valueColor){
        Label keyLabel = new Label(key, LabelType.H2, keyColor);
        Label valueLabel = new Label(value, LabelType.H3, valueColor);
        valueLabel.setPadding(new Insets(0, 0, 0, 20));

        getChildren().addAll(keyLabel, valueLabel);
        setSpacing(5);
    }

    public void setValue(String value){
        ((Label)getChildren().get(1)).setText(value);
    }

}
