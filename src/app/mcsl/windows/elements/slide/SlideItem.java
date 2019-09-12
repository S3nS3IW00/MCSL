package app.mcsl.windows.elements.slide;

import app.mcsl.managers.file.FileManager;
import app.mcsl.windows.Template;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public abstract class SlideItem extends VBox {

    private String itemText;
    private VBox subItemBox;

    private ImageView subIcon = new ImageView(FileManager.MENU_ICON);
    private Region titleRightRegion;

    private HBox titleBox;

    private Timeline subItemAnimation = new Timeline();
    private boolean isSubItemsOpened = false, canToggleSubItems = true;
    private WritableValue<Double> subItemsBoxHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return subItemBox.getPrefHeight();
        }

        @Override
        public void setValue(Double value) {
            subItemBox.setPrefHeight(value);
        }
    };
    private WritableValue<Double> subIconRotateValue = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return subIcon.getRotate();
        }

        @Override
        public void setValue(Double value) {
            subIcon.setRotate(value);
        }
    };

    private List<SlideItem> subItems = new ArrayList<>();

    public SlideItem(String itemText, Image image, SlideItem... subItems) {
        this.itemText = itemText;

        subItemBox = new VBox();
        subItemBox.setPrefHeight(0);
        subItemBox.setMaxHeight(Double.MAX_VALUE);

        ImageView graphic = new ImageView(image);
        graphic.setFitHeight(20);
        graphic.setFitWidth(20);

        Label title = new Label();
        title.setText(itemText);
        title.setGraphic(graphic);
        title.setMaxWidth(Double.MAX_VALUE);

        titleBox = new HBox(title);
        titleBox.setId("slide-item-box");
        titleBox.setPrefWidth(Double.MAX_VALUE);
        titleBox.setOnMouseClicked(e -> {
            if (!hasSubItems()) Template.toggleMenu();
            if (Template.isSettingsOpen()) {
                Template.toggleSettings();
            }
            if (hasSubItems()) toggleSubItem();
            onClick();
        });

        titleRightRegion = new Region();
        HBox.setHgrow(titleRightRegion, Priority.ALWAYS);

        for (SlideItem slideItem : subItems) {
            addSubItem(slideItem);
        }

        getChildren().add(titleBox);
        setPrefWidth(Double.MAX_VALUE);
    }

    public abstract void onClick();

    public void addSubItem(SlideItem slideItem) {
        if (!subItems.contains(slideItem)) {
            slideItem.setPadding(new Insets(0, 0, 0, 10));
            subItems.add(slideItem);
            subItemBox.getChildren().add(slideItem);
            if (!titleBox.getChildren().contains(subIcon)) {
                titleBox.getChildren().addAll(titleRightRegion, subIcon);
            }
        }
    }

    public void addSubItem(SlideItem... slideItems) {
        for (SlideItem slideItem : slideItems) {
            if (!subItems.contains(slideItem)) {
                slideItem.setPadding(new Insets(0, 0, 0, 10));
                subItems.add(slideItem);
                subItemBox.getChildren().add(slideItem);
                if (!titleBox.getChildren().contains(subIcon)) {
                    titleBox.getChildren().addAll(titleRightRegion, subIcon);
                }
            }
        }
    }

    public void removeSubItem(SlideItem slideItem) {
        if (subItems.contains(slideItem)) {
            subItems.remove(slideItem);
            subItemBox.getChildren().remove(slideItem);
        }
        if (subItems.size() == 0) titleBox.getChildren().removeAll(titleRightRegion, subIcon);
    }

    public void clearSubItems() {
        if (subItems.size() == 0) return;
        subItems.clear();
        subItemBox.getChildren().clear();
        titleBox.getChildren().removeAll(titleRightRegion, subIcon);
    }

    public void toggleSubItem() {
        subItemAnimation.getKeyFrames().clear();
        if (canToggleSubItems) {
            canToggleSubItems = false;
            if (isSubItemsOpened) {
                subItemAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50), new KeyValue(subItemsBoxHeight, 0.0)));
                subItemAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50), new KeyValue(subIconRotateValue, 0.0)));
                subItemAnimation.setOnFinished(e -> {
                    canToggleSubItems = true;
                    isSubItemsOpened = false;
                    this.getChildren().remove(subItemBox);
                });
            } else {
                this.getChildren().add(subItemBox);
                subItemAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50), new KeyValue(subItemsBoxHeight, subItems.size() * 41.0)));
                subItemAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50), new KeyValue(subIconRotateValue, 90.0)));
                subItemAnimation.setOnFinished(e -> {
                    canToggleSubItems = true;
                    isSubItemsOpened = true;
                });
            }
            subItemAnimation.play();
        }
    }

    public boolean hasSubItems() {
        return subItems.size() > 0;
    }

    public String getItemText() {
        return itemText;
    }

    public HBox getTitleBox() {
        return titleBox;
    }
}
