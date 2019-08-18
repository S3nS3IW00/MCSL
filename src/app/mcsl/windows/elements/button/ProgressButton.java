package app.mcsl.windows.elements.button;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

public class ProgressButton extends Button {

    private DoubleProperty progressProperty = new SimpleDoubleProperty();
    private Task<Boolean> task;

    public ProgressButton(String text) {
        setText(text);
        progressProperty.addListener((observable, oldValue, newValue) -> {
            double percent = newValue.doubleValue() * 100;
            setStyle("-fx-background-color: linear-gradient(to right, -fx-defdarkcolor 0%, -fx-defdarkcolor " + (int) percent + "%, -fx-bgcolor " + (int) percent + "%, -fx-bgcolor 100%);" +
                    "-fx-border-color: linear-gradient(to right, -fx-defdarkcolor 0%, -fx-defdarkcolor " + (int) percent + "%, -fx-defcolor " + (int) percent + "%, -fx-defcolor 100%);" +
                    "-fx-text-fill: linear-gradient(to right, -fx-bgcolor 0%, -fx-bgcolor " + (int) percent + "%, -fx-defcolor " + (int) percent + "%, -fx-defcolor 100%);");
            if((int)percent == 100 || (int)percent == -100){
                setDisable(false);
            } else {
                setDisable(true);
            }
        });
    }

    public Task<Boolean> getTask() {
        return task;
    }

    public void setTask(Task<Boolean> task) {
        this.task = task;
    }

    public DoubleProperty progressProperty() {
        return progressProperty;
    }

    public void reset(){
        setStyle(null);
    }

}
