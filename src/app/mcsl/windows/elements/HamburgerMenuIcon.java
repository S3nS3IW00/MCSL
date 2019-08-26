package app.mcsl.windows.elements;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public abstract class HamburgerMenuIcon extends VBox {

    private boolean isRotated = false;

    private Line line1, line2, line3;
    private int width, lineStroke;
    private Timeline rotateIn, rotateOut;
    private WritableValue<Double> line1RotateValue = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return line1.getRotate();
        }

        @Override
        public void setValue(Double value) {
            line1.setRotate(value);
        }
    };
    private WritableValue<Double> line3RotateValue = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return line3.getRotate();
        }

        @Override
        public void setValue(Double value) {
            line3.setRotate(value);
        }
    };


    public HamburgerMenuIcon(int width, int lineStroke) {
        this.width = width;
        this.lineStroke = lineStroke;

        line1 = new Line();
        line1.getStyleClass().add("line");
        line1.setStrokeWidth(lineStroke);
        line2 = new Line();
        line2.getStyleClass().add("line");
        line2.setStrokeWidth(lineStroke);
        line3 = new Line();
        line3.getStyleClass().add("line");
        line3.setStrokeWidth(lineStroke);

        rotateIn = new Timeline();
        rotateIn.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(line1RotateValue, 45.0)));
        rotateIn.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(line3RotateValue, -45.0)));

        rotateOut = new Timeline();
        rotateOut.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(line1RotateValue, 0.0)));
        rotateOut.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(line3RotateValue, 0.0)));
        rotateOut.setOnFinished(event -> {
            setSpacing(2);
            getChildren().add(1, line2);
        });

        setSpacing(2);
        getChildren().addAll(line1, line2, line3);
        setOnMouseClicked(event -> onClick());
        setAlignment(Pos.CENTER);
        setId("hamburger-icon");

        line1.setStartX(getLayoutX());
        line1.setEndX(getLayoutX() + width);
        line2.setStartX(getLayoutX());
        line2.setEndX(getLayoutX() + width);
        line3.setStartX(getLayoutX());
        line3.setEndX(getLayoutX() + width);
    }

    public void toggle() {
        if (isRotated) {
            rotateOut.play();
            isRotated = false;
        } else {
            setSpacing(-lineStroke);
            getChildren().remove(line2);
            rotateIn.play();
            isRotated = true;
        }
    }

    public boolean isRotated() {
        return isRotated;
    }

    public abstract void onClick();
}