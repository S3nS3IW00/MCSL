package app.mcsl.windows.elements;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class TabMenu extends HBox {

    private Map<Label, Node> contents = new LinkedHashMap<>();
    private Label selectedItem;
    private StringProperty selectedPageName = new SimpleStringProperty();

    public TabMenu(int spacing) {
        setId("tabmenu");
        setSpacing(spacing);

        selectedPageName.addListener((observable, oldValue, newValue) -> {
            onPageChange(oldValue, newValue);
        });
    }

    public void addIem(String title, Node content) {
        Label itemLabel = new Label(title);
        itemLabel.setOnMouseClicked(e -> selectTab(itemLabel));
        if (content == null) itemLabel.setDisable(true);

        contents.put(itemLabel, content);
        getChildren().add(itemLabel);
    }

    public abstract void setContent(Node content);

    public abstract void onPageChange(String from, String to);

    private void selectTab(Label itemLabel) {
        if (selectedItem != null && selectedItem == itemLabel) return;
        if (selectedItem != null) selectedItem.setStyle(null);
        selectedItem = itemLabel;
        selectedItem.setStyle("-fx-border-width: 0px 0px 4px 0px;");

        setContent(contents.get(selectedItem));
        selectedPageName.set(selectedItem.getText());
    }

    public void selectTab(String title) {
        Label itemLabel;
        if ((itemLabel = getItemLabelByText(title)) == null) return;
        if (selectedItem != null && selectedItem == itemLabel) return;
        if (selectedItem != null) selectedItem.setStyle(null);
        selectedItem = itemLabel;
        selectedItem.setStyle("-fx-border-width: 0px 0px 4px 0px;");

        setContent(contents.get(selectedItem));
        selectedPageName.set(selectedItem.getText());
    }

    public void selectTab(int index) {
        if (index > contents.size() - 1) return;
        Label itemLabel = (Label) contents.keySet().toArray()[index];
        if (selectedItem != null && selectedItem == itemLabel) return;
        if (selectedItem != null) selectedItem.setStyle(null);
        selectedItem = itemLabel;
        selectedItem.setStyle("-fx-border-width: 0px 0px 4px 0px;");

        setContent(contents.get(selectedItem));
        selectedPageName.set(selectedItem.getText());
    }

    private Label getItemLabelByText(String text) {
        for (Label label : contents.keySet()) {
            if (label.getText().equalsIgnoreCase(text)) return label;
        }
        return null;
    }

}
