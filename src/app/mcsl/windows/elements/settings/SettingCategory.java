package app.mcsl.windows.elements.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SettingCategory extends VBox {

    private String title;

    private Label titleLabel;
    private GridPane settingBox;

    private List<Setting> settings = new ArrayList<>();

    public SettingCategory(String title){
        this.title = title;

        titleLabel = new Label(title);
        titleLabel.setId("settings-group-box-title");

        settingBox = new GridPane();
        settingBox.setPadding(new Insets(10));
        settingBox.setVgap(10);
        settingBox.setHgap(10);

        Pane pane = new Pane(settingBox);
        pane.setId("settings-group-box");

        setSpacing(10);
        getChildren().addAll(titleLabel, pane);
    }

    public void addSetting(Setting setting){
        int rowIndex = getRowCount(settingBox);
        settingBox.add(setting.getTitle(), 0, rowIndex);

        HBox componentBox = new HBox(setting.getComponent());
        componentBox.setAlignment(Pos.CENTER_LEFT);
        if(setting.getDesctiption() != null) componentBox.getChildren().add(setting.getDesctiption());

        settingBox.add(componentBox, 1, rowIndex);
        this.settings.add(setting);

        settings.add(setting);
    }

    public void addSetting(Setting... settings){
        for(Setting setting : settings){
            int rowIndex = getRowCount(settingBox);
            settingBox.add(setting.getTitle(), 0, rowIndex);

            HBox componentBox = new HBox(setting.getComponent());
            componentBox.setAlignment(Pos.CENTER_LEFT);
            if(setting.getDesctiption() != null) componentBox.getChildren().add(setting.getDesctiption());

            settingBox.add(componentBox, 1, rowIndex);
            this.settings.add(setting);
        }
    }

    public void removeSetting(Setting setting){
        settingBox.getChildren().remove(setting);
    }

    public GridPane getSettingBox() {
        return settingBox;
    }

    private int getRowCount(GridPane pane) {
        int numRows = pane.getRowConstraints().size();
        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node child = pane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if(rowIndex != null){
                    numRows = Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }

    public List<Setting> getSettings() {
        return settings;
    }
}
