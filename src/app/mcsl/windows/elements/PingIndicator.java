package app.mcsl.windows.elements;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;

public class PingIndicator extends HBox {

    private Label pingLabel;
    private Line line1, line2, line3, line4;
    private int lineStroke, maxSize, maxValue;

    private IntegerProperty value = new SimpleIntegerProperty(0);

    public PingIndicator(int lineStroke, int maxSize, int maxValue){
        this.lineStroke = lineStroke;
        this.maxSize = maxSize;
        this.maxValue = maxValue;

        pingLabel = new Label("");
        pingLabel.setStyle("-fx-text-fill: -fx-thirdcolor; -fx-font-size: 13px; -fx-font-weight: bold;");

        line1 = new Line();
        line1.getStyleClass().add("line");
        line1.setStrokeWidth(lineStroke);
        line1.setId("disabled-level-indicator");

        line2 = new Line();
        line2.getStyleClass().add("line");
        line2.setStrokeWidth(lineStroke);
        line2.setId("disabled-level-indicator");

        line3 = new Line();
        line3.getStyleClass().add("line");
        line3.setStrokeWidth(lineStroke);
        line3.setId("disabled-level-indicator");

        line4 = new Line();
        line4.getStyleClass().add("line");
        line4.setStrokeWidth(lineStroke);
        line4.setId("disabled-level-indicator");

        HBox lineBox = new HBox(2, line1, line2, line3, line4);
        lineBox.setAlignment(Pos.BOTTOM_RIGHT);

        setSpacing(5);
        getChildren().addAll(pingLabel, lineBox);
        setAlignment(Pos.BOTTOM_RIGHT);

        line1.setStartY(getLayoutY()-(maxSize-12));
        line1.setEndY(getLayoutY());
        line2.setStartY(getLayoutY()-(maxSize-6));
        line2.setEndY(getLayoutY());
        line3.setStartY(getLayoutY()-(maxSize-3));
        line3.setEndY(getLayoutY());
        line4.setStartY(getLayoutY()-maxSize);
        line4.setEndY(getLayoutY());

        value.addListener((observable, oldValue, newValue) -> {
            pingLabel.setText(newValue+"ms");
            double percent = maxValue-((newValue.doubleValue()/maxValue)*100);
            int activeCount = 0;
            if(percent < 25){
                activeCount = 1;
            } else if(percent >= 25 && percent < 50){
                activeCount = 2;
            } else if(percent >= 50 && percent < 75){
                activeCount = 3;
            } else if (percent > 75){
                activeCount = 4;
            }

            switch (activeCount){
                case 0:
                    line1.setId("inactive-level-indicator");
                    line2.setId("inactive-level-indicator");
                    line3.setId("inactive-level-indicator");
                    line4.setId("inactive-level-indicator");
                    break;
                case 1:
                    line1.setId("active-level-indicator");
                    line2.setId("inactive-level-indicator");
                    line3.setId("inactive-level-indicator");
                    line4.setId("inactive-level-indicator");
                    break;
                case 2:
                    line1.setId("active-level-indicator");
                    line2.setId("active-level-indicator");
                    line3.setId("inactive-level-indicator");
                    line4.setId("inactive-level-indicator");
                    break;
                case 3:
                    line1.setId("active-level-indicator");
                    line2.setId("active-level-indicator");
                    line3.setId("active-level-indicator");
                    line4.setId("inactive-level-indicator");
                    break;
                case 4:
                    line1.setId("active-level-indicator");
                    line2.setId("active-level-indicator");
                    line3.setId("active-level-indicator");
                    line4.setId("active-level-indicator");
                    break;
            }
        });
    }

    public void setValue(int value){
        this.value.setValue(value);
    }

    public void disable(){
        line1.setId("disabled-level-indicator");
        line2.setId("disabled-level-indicator");
        line3.setId("disabled-level-indicator");
        line4.setId("disabled-level-indicator");
        pingLabel.setText("");
    }

}
