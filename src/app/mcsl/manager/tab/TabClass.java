package app.mcsl.manager.tab;

import javafx.scene.Node;
import javafx.scene.image.Image;

public interface TabClass {

    Node getContent();

    TabType getType();

    String getTitle();

    Image getIcon();

}
