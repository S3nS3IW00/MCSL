package app.mcsl.windows.contents.server;

import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.tab.TabAction;
import app.mcsl.managers.tab.TabManager;
import app.mcsl.managers.theme.ThemeManager;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ServerStage extends Stage {

    private ServerContent serverContent;

    private ImageView mcslImageView;
    private VBox body;
    private HBox header;

    public ServerStage(ServerContent serverContent) {
        this.serverContent = serverContent;

        Font.loadFont(getClass().getResourceAsStream("Minecraftia.ttf"), 1);
        getIcons().add(new Image("/app/mcsl/resources/favicon.png"));
        setTitle(serverContent.getTitle());
        build();
    }

    public void build() {
        mcslImageView = new ImageView(FileManager.MCSL_IMAGE);
        mcslImageView.setPickOnBounds(false);
        mcslImageView.setFitHeight(30);

        Button backToTabButton = new Button(Language.getText("backtotab"), ButtonType.ROUNDED);
        backToTabButton.setOnAction(e -> {
            Tab tab = TabManager.getTabByClass(serverContent);
            TabAction.attach(tab);
            TabAction.choose(tab);
        });

        StackPane headerStack = new StackPane(mcslImageView, backToTabButton);
        HBox.setHgrow(headerStack, Priority.ALWAYS);
        StackPane.setAlignment(backToTabButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(mcslImageView, Pos.CENTER);

        header = new HBox(5, headerStack);
        header.setAlignment(Pos.CENTER);
        header.setId("header-box");

        body = new VBox(header, serverContent.getContent());

        Scene scene = new Scene(body, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/app/mcsl/windows/styles/style.css").toExternalForm());

        serverContent.getContent().prefHeightProperty().bind(scene.heightProperty());
        serverContent.getContent().prefWidthProperty().bind(scene.widthProperty());

        ThemeManager.applyCss(scene);

        setScene(scene);
        setMinWidth(1000);
        setMinHeight(700);

        setOnCloseRequest(e -> {
            TabAction.close(TabManager.getTabByClass(serverContent));
            e.consume();
        });
    }

    public VBox getBody() {
        return body;
    }
}

