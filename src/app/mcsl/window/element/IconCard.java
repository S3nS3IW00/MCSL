package app.mcsl.window.element;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class IconCard extends HBox {

    private ImageView icon;
    private Node content;

    private int width, height;

    public IconCard(ImageView icon, Node content, int width, int height) {
        this.icon = icon;
        this.content = content;
        this.width = width;
        this.height = height;

        content.setId("icon-card-content");

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);

        setId("icon-card");
        setSpacing(10);
        getChildren().addAll(icon, region, content, region2);
        setPrefSize(width, height);
    }

}
