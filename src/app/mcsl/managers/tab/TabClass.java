package app.mcsl.managers.tab;

import javafx.scene.Node;

public interface TabClass {

    Node getContent();
    TabType getType();
    String getTitle();

}
