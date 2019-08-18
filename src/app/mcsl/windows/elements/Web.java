package app.mcsl.windows.elements;

import app.mcsl.MainClass;
import app.mcsl.managers.Language;
import app.mcsl.managers.mainside.OSManager;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelColor;
import app.mcsl.windows.elements.label.LabelType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static javafx.concurrent.Worker.State;

public class Web extends VBox {

    private static TextField linkField;
    private List<String> lastLinks = new ArrayList<>();
    private WebView webView = new WebView();
    private WebEngine webEngine = webView.getEngine();
    private Button backButton, closeButton;

    public Web() {
        Label titleLabel = new Label("", LabelType.H3);
        titleLabel.setStyle("-fx-text-fill: -fx-themetypetextcolor;");
        titleLabel.setPadding(new Insets(0, 0, 0, 10));

        closeButton = new Button("X", ButtonType.ERROR_ACTION_BUTTON, 13);
        closeButton.setOnAction(e -> {
            MainClass.getTemplate().closeWeb();
            webEngine.loadContent("");
        });

        Region titleRegion1 = new Region();
        HBox.setHgrow(titleRegion1, Priority.ALWAYS);

        Region titleRegion2 = new Region();
        HBox.setHgrow(titleRegion2, Priority.ALWAYS);

        HBox titleBox = new HBox(titleRegion1, titleLabel, titleRegion2, closeButton);
        titleBox.setMinHeight(30);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setStyle("-fx-background-color: -fx-themetypecolor;");

        linkField = new TextField();
        HBox.setHgrow(linkField, Priority.ALWAYS);
        linkField.setDisable(true);

        backButton = new Button("<", ButtonType.ACTION_BUTTON, 13);
        backButton.setMaxHeight(30);
        backButton.setDisable(true);
        backButton.setOnAction(e -> {
            backButton.setDisable(true);
            webEngine.load(lastLinks.get(lastLinks.size() - 2));
            lastLinks.remove(lastLinks.size() - 1);
        });

        HBox linkLine = new HBox();
        linkLine.setMinHeight(30);
        linkLine.setStyle("-fx-background-color: -fx-themetypecolor;");
        linkLine.getChildren().addAll(backButton, linkField);

        VBox top = new VBox();
        top.getChildren().addAll(titleBox, linkLine);

        ProgressBar loadingProgress = new ProgressBar();
        loadingProgress.setMinHeight(0);
        loadingProgress.setMaxWidth(Double.MAX_VALUE);
        loadingProgress.progressProperty().bind(webEngine.getLoadWorker().progressProperty());

        VBox loading = new VBox();
        loading.setAlignment(Pos.TOP_CENTER);
        loading.getChildren().addAll(loadingProgress);

        StackPane webViewPane = new StackPane();

        webEngine.setUserDataDirectory(new File(OSManager.getRoot() + File.separator + "web"));
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                if (webEngine.getLocation().equalsIgnoreCase("")) {
                    lastLinks.clear();
                    backButton.setDisable(true);
                    linkField.clear();
                    return;
                }
                if (lastLinks.size() > 0) {
                    if (!lastLinks.get(lastLinks.size() - 1).equalsIgnoreCase(webEngine.getLocation())) {
                        lastLinks.add(webEngine.getLocation());
                    }
                } else {
                    lastLinks.add(webEngine.getLocation());
                }
                //System.out.println("New state succeeded");
                titleLabel.setText(webEngine.getTitle());
                loading.setVisible(false);
                if (lastLinks.size() > 1) {
                    backButton.setDisable(false);
                } else {
                    backButton.setDisable(true);
                }
            } else if (newState == State.CANCELLED) {
                //System.out.println("New state cancelled");
            } else if (newState == State.FAILED) {
                System.out.println("New state failed");
            } else if (newState == State.READY) {
                //System.out.println("New state ready");
            } else if (newState == State.RUNNING) {
                //System.out.println("New state running");
            } else if (newState == State.SCHEDULED) {
                //System.out.println("New state scheduled");
                titleLabel.setText(Language.getText("loading") + "...");
                linkField.setText(webEngine.getLocation());
                loading.setVisible(true);
            }

            if (oldState == State.SUCCEEDED) {
                //System.out.println("Old state succeeded");
            } else if (oldState == State.CANCELLED) {
                //System.out.println("Old state cancelled");
            } else if (oldState == State.FAILED) {
                //System.out.println("Old state failed");
            } else if (oldState == State.READY) {
                //System.out.println("Old state ready");
            } else if (oldState == State.RUNNING) {
                //System.out.println("Old state running");
            } else if (oldState == State.SCHEDULED) {
                //System.out.println("Old state scheduled");
            }
        });

        webView.setContextMenuEnabled(false);
        webViewPane.getChildren().addAll(webView, loading);

        getChildren().addAll(top, webViewPane);
        setStyle("-fx-background-radius: 20px;-fx-border-radius: 20px;");
        setMinHeight(0);
    }

    public void loadLink(String link) {
        webEngine.load(link);
    }

    public WebView getWebView() {
        return webView;
    }
}
