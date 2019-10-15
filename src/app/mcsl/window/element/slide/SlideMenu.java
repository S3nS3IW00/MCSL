package app.mcsl.window.element.slide;

import app.mcsl.MainClass;
import app.mcsl.window.element.label.LabelType;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SlideMenu extends VBox {

    private boolean isOpened = false, canToggle = true;
    private String title;
    private double width;
    private VBox topItemBox, bottomItemBox;
    private Pane parent;
    private Timeline animation;
    private WritableValue<Double> writableWidth = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return getMaxWidth();
        }

        @Override
        public void setValue(Double value) {
            setMaxWidth(value);
        }
    };

    private List<SlideItem> items = new ArrayList<>();
    private SlideItem selectedItem;

    public SlideMenu(Pane parent, String title, double width) {
        this.parent = parent;
        this.title = title;
        this.width = width;

        setMaxWidth(0);
        setMaxHeight(Double.MAX_VALUE);
        setId("slide-box");

        animation = new Timeline();

        app.mcsl.window.element.label.Label titleLabel = new app.mcsl.window.element.label.Label(title, LabelType.H2);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPrefHeight(30);
        titleLabel.setStyle("-fx-border-color: black;-fx-border-width: 0px 0px 2px 0px;-fx-text-fill: -fx-defcolor;");

        topItemBox = new VBox();
        VBox.setVgrow(topItemBox, Priority.ALWAYS);

        bottomItemBox = new VBox();
        bottomItemBox.setStyle("-fx-border-color: black;-fx-border-width: 1px 0px 0px 0px;");

        Region downRegion = new Region();
        VBox.setVgrow(downRegion, Priority.ALWAYS);

        Region rightRegion = new Region();
        HBox.setHgrow(rightRegion, Priority.ALWAYS);

        HBox downBox = new HBox(new Label("MCSL \u00A9 " + new SimpleDateFormat("YYYY").format(new Date())), rightRegion, new Label("v" + MainClass.VERSION));
        downBox.setStyle("-fx-text-fill: black;");

        this.getChildren().addAll(titleLabel, topItemBox, bottomItemBox, downRegion, downBox);
    }

    public void toggle() {
        animation.getKeyFrames().clear();
        if (canToggle) {
            canToggle = false;
            if (isOpened) {
                animation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(writableWidth, 0.0)));
                isOpened = false;
            } else {
                setMaxWidth(0);
                if (!parent.getChildren().contains(this)) parent.getChildren().add(this);
                animation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(writableWidth, width)));
                isOpened = true;
            }
            animation.play();
            animation.setOnFinished(e -> {
                if (!isOpened) {
                    parent.getChildren().remove(this);
                }
                canToggle = true;
            });
        }
    }

    public void selectItem(SlideItem slideItem) {
        if (selectedItem != null) selectedItem.getTitleBox().setId("slide-item-box");
        if (slideItem == null) {
            selectedItem.getTitleBox().setId("slide-item-box");
            selectedItem = null;
            return;
        }
        selectedItem = slideItem;
        selectedItem.getTitleBox().setId("selected-slide-item-box");
    }

    public boolean isCanToggle() {
        return canToggle;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void add(SlideItem slideItem) {
        topItemBox.getChildren().add(slideItem);

        items.add(slideItem);
    }

    public void add(SlideItem slideItem, SlideAlignment alignment) {
        switch (alignment) {
            case TOP:
                topItemBox.getChildren().add(slideItem);
                break;
            case BOTTOM:
                bottomItemBox.getChildren().add(slideItem);
                break;
        }

        items.add(slideItem);
    }

    public SlideItem getSelectedItem() {
        return selectedItem;
    }

    public double getSlideMenuWidth() {
        return width;
    }
}
