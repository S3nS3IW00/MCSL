package app.mcsl.window.element.button;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SwitchButton extends HBox {

    Label label = new Label();
    private final Button button = new Button();
    String on, off;

    SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty switchOnProperty() {
        return switchedOn;
    }

    private void init() {

        label.setText("OFF");

        getChildren().addAll(label, button);
        button.setOnAction((e) -> switchedOn.set(!switchedOn.get()));
        label.setOnMouseClicked((e) -> switchedOn.set(!switchedOn.get()));
        setStyle();
        bindProperties();
    }

    private void setStyle() {
        //Default Width
        button.setStyle("-fx-background-radius: 10;");
        setWidth(70);
        setHeight(20);
        label.setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: grey;-fx-background-radius: 10;");
        setAlignment(Pos.CENTER_LEFT);
    }

    private void bindProperties() {
        label.prefWidthProperty().bind(widthProperty().divide(2));
        label.prefHeightProperty().bind(heightProperty());
        button.prefWidthProperty().bind(widthProperty().divide(2));
        button.prefHeightProperty().bind(heightProperty());
    }

    public SwitchButton(String on, String off) {
        init();
        this.on = on;
        this.off = off;
        switchedOn.addListener((a, b, c) -> {
            if (c) {
                label.setText(getOn());
                setStyle("-fx-background-color: green;-fx-background-radius: 10;");
                label.toFront();
            } else {
                label.setText(getOff());
                setStyle("-fx-background-color: grey;-fx-background-radius: 10;");
                button.toFront();
            }
        });
    }

    public void setSwitched(boolean switched) {
        switchedOn.set(switched);
    }

    public SimpleBooleanProperty getSwitchedOn() {
        return switchedOn;
    }

    public String getOff() {
        return off;
    }

    public String getOn() {
        return on;
    }

    public void setTexts(String on, String off) {
        this.on = on;
        this.off = off;
        if (getSwitchedOn().get()) {
            label.setText(on);
        } else {
            label.setText(off);
        }
    }
}
