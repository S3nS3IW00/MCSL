package app.mcsl.windows.elements;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GroupBox extends VBox {

    private String title;

    private Label titleLabel;
    private VBox vBox;

    public GroupBox(String title) {
        this.title = title;

        titleLabel = new Label(title);
        titleLabel.setId("group-box-title");

        vBox = new VBox();
        vBox.setId("group-box");
        HBox.setHgrow(vBox, Priority.ALWAYS);

        setSpacing(10);
        getChildren().addAll(titleLabel, vBox);
    }

    public void add(Node node) {
        vBox.getChildren().add(node);
    }

    public void addAll(Node... nodes) {
        vBox.getChildren().addAll(nodes);
    }

    public VBox getBody() {
        return vBox;
    }
}
