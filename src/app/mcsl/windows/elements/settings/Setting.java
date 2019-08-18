package app.mcsl.windows.elements.settings;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public abstract class Setting extends HBox {

    private String title;
    private String description;

    private Label titleLabel, descriptionLabel;
    private Node component;
    private boolean notifyChange;

    public Setting(String title, Node component, String description, boolean notifyChange) {
        this.title = title;
        this.component = component;
        this.description = description;
        this.notifyChange = notifyChange;

        titleLabel = new Label(title);
        titleLabel.setId("settings-box-title");

        getChildren().addAll(titleLabel, component);
        setSpacing(10);
        setId("settings-box");

        if (description != null) {
            descriptionLabel = new Label("(" + description + ")");
            descriptionLabel.setId("settings-description");
            getChildren().add(descriptionLabel);
        }
    }

    public abstract void onChange(Object object);

    public Label getTitle() {
        return titleLabel;
    }

    public Label getDesctiption() {
        return descriptionLabel;
    }

    public Node getComponent() {
        return component;
    }

    public boolean isNotifyChange() {
        return notifyChange;
    }
}
