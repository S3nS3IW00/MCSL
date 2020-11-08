package app.mcsl.window.content.server;

import app.mcsl.manager.Language;
import app.mcsl.manager.tab.TabAction;
import app.mcsl.manager.tab.TabManager;
import app.mcsl.manager.theme.ThemeManager;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

public class ServerStage extends Stage {

    private ServerContent serverContent;

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private VBox body;
    private HBox header;

    public ServerStage(ServerContent serverContent) {
        this.serverContent = serverContent;

        getIcons().add(new Image("/app/mcsl/resource/favicon.png"));
        setTitle(serverContent.getTitle());
        build();
    }

    public void build() {
        Label mcslLogoText = new Label("MINECRAFT SERVER LAUNCHER");
        mcslLogoText.setAlignment(Pos.CENTER);
        mcslLogoText.setMinHeight(30);
        mcslLogoText.setId("mcsl-logo-text");

        Button backToTabButton = new Button(Language.getText("backtotab"), ButtonType.ROUNDED);
        backToTabButton.setOnAction(e -> {
            Tab tab = TabManager.getTabByClass(serverContent);
            TabAction.attach(tab);
            TabAction.choose(tab);
        });

        StackPane headerStack = new StackPane(mcslLogoText, backToTabButton);
        HBox.setHgrow(headerStack, Priority.ALWAYS);
        StackPane.setAlignment(backToTabButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(mcslLogoText, Pos.CENTER);

        header = new HBox(5, headerStack);
        header.setAlignment(Pos.CENTER);
        header.setId("header-box");

        StackPane contentStack = new StackPane(serverContent.getContent());
        contentStack.setStyle("-fx-background-image: url(\"/app/mcsl/resource/bg_blur.jpg\");\n" +
                "-fx-background-size: cover;");

        body = new VBox(header, contentStack);

        Scene scene = new Scene(body, screenSize.getWidth() / 2, screenSize.getHeight() / 2);
        scene.getStylesheets().add(getClass().getResource("/app/mcsl/window/style/style.css").toExternalForm());

        serverContent.getContent().prefHeightProperty().bind(scene.heightProperty());
        serverContent.getContent().prefWidthProperty().bind(scene.widthProperty());

        ThemeManager.applyCss(scene);

        setScene(scene);
        setMinWidth(800);
        setMinHeight(600);

        setOnCloseRequest(e -> {
            TabAction.close(TabManager.getTabByClass(serverContent));
            e.consume();
        });
    }

    public VBox getBody() {
        return body;
    }
}

