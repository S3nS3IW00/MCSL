package app.mcsl.windows.elements;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class MouseTooltip {

    public static Tooltip install(String text, Node control){
        Tooltip tooltip = new Tooltip(text);

        control.setOnMouseMoved(event -> {
            Node node = (Node) event.getSource();
            tooltip.show(node, event.getScreenX() + 20, event.getScreenY()-20);
        });

        control.setOnMouseExited(e -> tooltip.hide());

        return tooltip;
    }

}
