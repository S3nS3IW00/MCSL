package app.mcsl.window.element.tooltip;

import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class Tooltip extends javafx.scene.control.Tooltip {

    public Tooltip(TooltipType type) {
        setType(type);
    }

    public Tooltip(String text, TooltipType type) {
        setText(text);
        setType(type);
    }

    public void setType(TooltipType type) {
        setId(type.getId());
        setGraphic(new ImageView(type.getIcon()));
        setAutoHide(true);
    }

    public void setFollowMouseOnControl(Node control) {
        control.setOnMouseMoved(event -> {
            Node node = (Node) event.getSource();
            show(node, event.getScreenX() + 20, event.getScreenY() - 20);
        });

        control.setOnMouseExited(e -> hide());
    }

}
