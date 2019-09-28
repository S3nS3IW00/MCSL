package app.mcsl.windows.elements.dialog;

import app.mcsl.managers.Language;
import app.mcsl.windows.Template;
import app.mcsl.windows.elements.label.LabelColor;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.LinkedList;

public class Dialog extends VBox {

    private String title, text;

    private Pane content;
    private HBox buttonBox;
    private Label titleLabel, textLabel;
    private LinkedList<Button> buttons = new LinkedList<>();
    private int height, width;
    private Button okButton;

    private FadeTransition inFade, outFade;

    private boolean isShowing = false, keepDefButton = false;

    public Dialog(int height, int width, String title, DialogType type, String text) {
        this.title = title;
        this.text = text;
        this.height = height;
        this.width = width;

        titleLabel = new Label(title);
        titleLabel.setId("dialog-title");

        textLabel = new Label(text);
        textLabel.setMaxWidth(width);
        textLabel.setWrapText(true);

        content = new VBox(textLabel);
    }

    public Dialog(int height, int width, String title, DialogType type, String text, LabelColor color) {
        this.title = title;
        this.text = text;
        this.height = height;
        this.width = width;

        titleLabel = new Label(title);
        titleLabel.setId("dialog-title");

        textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill: " + color.getColor());
        textLabel.setMaxWidth(width);
        textLabel.setWrapText(true);

        content = new VBox(textLabel);
    }

    public Dialog(int height, int width, String title, DialogType type, Pane content) {
        this.title = title;
        this.height = height;
        this.width = width;

        titleLabel = new Label(title);
        titleLabel.setId("dialog-title");

        this.content = content;
    }

    public void build() {
        inFade = new FadeTransition(Duration.millis(200), this);
        inFade.setCycleCount(1);
        inFade.setFromValue(0);
        inFade.setToValue(1);

        outFade = new FadeTransition(Duration.millis(200), this);
        outFade.setCycleCount(1);
        outFade.setFromValue(1);
        outFade.setToValue(0);
        outFade.setOnFinished(e -> {
            Template.getDialogStack().getChildren().remove(this);
            Template.getBody().setEffect(null);
            Template.getBody().setMouseTransparent(false);
            isShowing = false;
            if (Dialogs.hasNext()) {
                Dialogs.showNext();
            }
        });

        titleLabel = new Label(title);
        titleLabel.setId("dialog-title");

        content.setId("dialog-content-box");
        content.setPrefHeight(height - 60);

        okButton = new Button(Language.getText("ok"));
        okButton.setPrefWidth(width - 20);
        okButton.setOnAction(e -> close());
        buttons.add(okButton);

        buttonBox = new HBox(5);
        buttonBox.setId("dialog-button-box");
        refreshButtonBox();

        Region dialogBottomRegion = new Region();
        VBox.setVgrow(dialogBottomRegion, Priority.ALWAYS);

        setSpacing(20);
        getChildren().addAll(titleLabel, content, buttonBox);
        setId("dialog");
        setOpacity(0);
        setMaxHeight(height);
        setMaxWidth(width);
    }

    public void addButton(app.mcsl.windows.elements.button.Button... buttons) {
        for (Button button : buttons) {
            this.buttons.add(button);
            button.setMaxWidth(Double.MAX_VALUE);
        }

        if (buttonBox != null) {
            refreshButtonBox();
        }
    }

    public void removeButton(int index) {
        if (buttonBox == null || index <= buttonBox.getChildren().size())
            buttons.remove(index);
        refreshButtonBox();
    }

    private void refreshButtonBox() {
        buttonBox.getChildren().clear();
        int size = buttons.size() - (buttons.size() > 1 && !keepDefButton ? 1 : 0);
        for (Button button : this.buttons) {
            if (buttons.size() > 1 && !keepDefButton && button == okButton) continue;
            button.setPrefWidth((width - 20) / size - 5);
            buttonBox.getChildren().add(button);
        }
    }

    public void show() {
        if (Template.getStage() == null || Template.getStage().getScene() == null) {
            Dialogs.addDialog(this);
            return;
        }

        if (Template.getDialogStack().getChildren().size() == 1 || Template.getDialogStack().getChildren().get(1) != this) {
            if (Dialogs.canShow() || Dialogs.next() == this) {
                isShowing = true;
                Template.getBody().setMouseTransparent(true);
                if (Template.getDialogStack().getChildren().size() > 1) {
                    Dialogs.addDialog(this);
                } else {
                    Template.getDialogStack().getChildren().add(this);
                    Dialogs.setCurrentDialog(this);
                }
                Template.getBody().setEffect(new GaussianBlur(10));
                inFade.play();
            } else {
                Dialogs.addDialog(this);
            }
        }
    }

    public void showAndOverlay() {
        isShowing = true;
        Template.getBody().setMouseTransparent(true);
        if (Dialogs.getCurrentDialog() != null) {
            Dialogs.getCurrentDialog().close();
            Dialogs.addDialog(Dialogs.getCurrentDialog(), 0);
        }
        Template.getDialogStack().getChildren().add(this);
        Template.getBody().setEffect(new GaussianBlur(10));
        inFade.play();
    }

    public void close() {
        if (isShowing) {
            outFade.play();
        } else {
            Dialogs.removeDialog(this);
            if (Dialogs.hasNext()) {
                Dialogs.showNext();
            }
        }
    }

    public void setContent(Pane content) {
        this.content = content;
    }

    public void keepDefaultButton(boolean keep) {
        keepDefButton = keep;
        refreshButtonBox();
    }

    public boolean isShowing() {
        return isShowing;
    }

    public String getTitle() {
        return title;
    }
}
