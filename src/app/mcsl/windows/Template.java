package app.mcsl.windows;

import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.tab.TabAction;
import app.mcsl.managers.tab.TabClass;
import app.mcsl.windows.contents.mainpage.FilesContent;
import app.mcsl.windows.contents.mainpage.MainContent;
import app.mcsl.windows.contents.mainpage.ServersContent;
import app.mcsl.windows.contents.mainpage.SettingsContent;
import app.mcsl.windows.elements.HamburgerMenuIcon;
import app.mcsl.windows.elements.Web;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.customdialogs.DragAndDropDialog;
import app.mcsl.windows.elements.dialog.customdialogs.QuitDialog;
import app.mcsl.windows.elements.dialog.types.AlertDialog;
import app.mcsl.windows.elements.dialog.types.AlertType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelColor;
import app.mcsl.windows.elements.label.LabelType;
import app.mcsl.windows.elements.notifications.Notification;
import app.mcsl.windows.elements.slide.SlideAlignment;
import app.mcsl.windows.elements.slide.SlideItem;
import app.mcsl.windows.elements.slide.SlideMenu;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Template {

    private static Stage stage;

    private static MainContent mainContent;
    private static ServersContent serversContent;
    private static FilesContent filesContent;
    private static SettingsContent settingsContent;

    private static TabClass currentTabClass;

    private static Tab currentDraggingTab;
    private static final AtomicLong idGenerator = new AtomicLong();
    private static final String draggingID = "DraggingTabPaneSupport-" + idGenerator.incrementAndGet();
    private static boolean outOfSceneDrag = false;

    private static Map<String, LabelColor> notifications = new HashMap<>();

    private static int newNotificationCount = 0;

    private static QuitDialog quitDialog;
    private static DragAndDropDialog dragAndDropDialog;

    private static app.mcsl.windows.elements.label.Label notificationLabel;
    private static Label detachTabDragLabel;
    private static Circle notificationCircle;
    private static StackPane notificationButtonStack, detachTabDragStack, headerStack;
    private static ImageView mcslImageView;
    private static StackPane tabSlide = new StackPane();
    private static app.mcsl.windows.elements.button.Button settingsButton;
    private static MenuButton notificationButton;
    private static MenuItem notificationItem;
    private static Web web;
    private static HamburgerMenuIcon hamburgerMenuIcon;
    private static Timeline settingsAnimation = new Timeline(), notificationAnimation = new Timeline(), webAnimartion = new Timeline();
    private static SlideMenu slideMenu;
    private static TabPane tabPane;
    private static Tab mainTab;
    private static VBox settingsBox, body, notificationBox;
    private static HBox header;
    private static StackPane dialogStack, settingsStack, webviewStack;
    private static boolean isSettingsOpen = false, canOpenSettings = true, isShowingNotification = false, isWebOpen = false, canOpenWeb = true;
    private static WritableValue<Double> settingsBoxHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return settingsBox.getMaxHeight();
        }

        @Override
        public void setValue(Double value) {
            settingsBox.setMaxHeight(value);
        }
    };
    private static WritableValue<Double> webBoxHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return web.getMaxHeight();
        }

        @Override
        public void setValue(Double value) {
            web.setMaxHeight(value);
        }
    };
    private static WritableValue<Double> mcslImageViewHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return mcslImageView.getFitHeight();
        }

        @Override
        public void setValue(Double value) {
            mcslImageView.setFitHeight(value);
        }
    };
    private static WritableValue<Double> notificationLabelHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return notificationLabel.getMinHeight();
        }

        @Override
        public void setValue(Double value) {
            notificationLabel.setMinHeight(value);
        }
    };

    public static void build() {
        stage = new Stage();

        stage.getIcons().add(new Image("/app/mcsl/resources/favicon.png"));
        stage.setTitle("Minecraft Server Launcher");

        //mainContent = new MainContent();
        serversContent = new ServersContent();
        filesContent = new FilesContent();
        settingsContent = new SettingsContent();

        hamburgerMenuIcon = new HamburgerMenuIcon(15, 3) {
            @Override
            public void onClick() {
                toggleMenu();
            }
        };

        slideMenu = new SlideMenu(tabSlide, Language.getText("menu"), 250);

        SlideItem serversItem = new SlideItem(Language.getText("servers"), FileManager.SERVER_ICON) {
            @Override
            public void onClick() {
                if (slideMenu.getSelectedItem() != null && slideMenu.getSelectedItem() != this)
                    TabAction.changeContent(currentTabClass, serversContent, this, null);
            }
        };
        slideMenu.add(serversItem);
        slideMenu.selectItem(serversItem);

        slideMenu.add(new SlideItem(Language.getText("filemanager"), FileManager.FILE_ICON_20) {
            @Override
            public void onClick() {
                if (slideMenu.getSelectedItem() != null && slideMenu.getSelectedItem() != this)
                    TabAction.changeContent(currentTabClass, filesContent, this, null);
            }
        });

        slideMenu.add(new SlideItem(Language.getText("website"), FileManager.WEBSITE_ICON_20) {
            @Override
            public void onClick() {
                try {
                    Desktop.getDesktop().browse(new URI("https://mcsl.app"));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                //openWeb("https://mcserverlauncher.tk");
            }
        }, SlideAlignment.BOTTOM);

        slideMenu.add(new SlideItem("Facebook", FileManager.FACEBOOK_ICON_20) {
            @Override
            public void onClick() {
                try {
                    Desktop.getDesktop().browse(new URI("https://fb.me/mcserverlauncher"));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                //openWeb("https://fb.me/mcserverlauncher");
            }
        }, SlideAlignment.BOTTOM);

        slideMenu.add(new SlideItem(Language.getText("reporterror"), FileManager.REPORT_ICON_20) {
            @Override
            public void onClick() {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/S3nS3IW00/mcserverlauncher/issues/new"));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                //openWeb("https://github.com/S3nS3IW00/mcserverlauncher/issues/new");
            }
        }, SlideAlignment.BOTTOM);

        settingsButton = new Button("", ButtonType.ROUNDED);
        settingsButton.setGraphic(new ImageView(FileManager.SETTINGS_ICON));
        settingsButton.setOnAction(e -> {
            toggleSettings();
            if (slideMenu.isOpened() && slideMenu.isCanToggle()) toggleMenu();
        });

        javafx.scene.control.Label notificationsLabel = new javafx.scene.control.Label(Language.getText("notifications"));
        notificationsLabel.setId("notifications-title");
        HBox.setHgrow(notificationsLabel, Priority.ALWAYS);

        notificationItem = new MenuItem();
        notificationItem.setGraphic(notificationsLabel);

        notificationButton = new MenuButton();
        notificationButton.getItems().add(notificationItem);
        notificationButton.setId("notification-button");
        notificationButton.setGraphic(new ImageView(FileManager.NOTIFICATION_ICON));
        notificationButton.setOnMouseClicked(e -> {
            notificationButtonStack.getChildren().remove(notificationCircle);
            newNotificationCount = 0;
        });

        notificationCircle = new Circle(6, Color.RED);

        notificationButtonStack = new StackPane(notificationButton);
        StackPane.setAlignment(notificationCircle, Pos.TOP_RIGHT);

        if (FileManager.getNotificationCount() > 0) {
            for (Notification notification : FileManager.getLatestNotifications(5)) {
                addNotification(notification, false);
            }
        }

        Region headerRegion1 = new Region();
        HBox.setHgrow(headerRegion1, Priority.ALWAYS);

        mcslImageView = new ImageView(FileManager.MCSL_IMAGE);
        mcslImageView.setPickOnBounds(false);
        mcslImageView.setFitHeight(30);

        notificationLabel = new Label("", LabelType.H2);
        notificationLabel.setPickOnBounds(false);
        notificationLabel.setMinHeight(0);
        notificationLabel.setMaxHeight(30);

        notificationBox = new VBox(notificationLabel, mcslImageView);
        notificationBox.setPickOnBounds(false);
        notificationBox.setMaxHeight(30);
        notificationBox.setAlignment(Pos.CENTER);

        header = new HBox(5, hamburgerMenuIcon, headerRegion1, notificationButtonStack, settingsButton);
        header.setId("header-box");

        detachTabDragLabel = new Label(Language.getText("droptodetach"));
        detachTabDragLabel.setAlignment(Pos.CENTER);
        detachTabDragLabel.setMaxHeight(30);
        detachTabDragLabel.setPrefWidth(600);
        detachTabDragLabel.setId("drag-label");
        detachTabDragLabel.setOnDragOver(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    currentDraggingTab != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        detachTabDragLabel.setOnDragDropped(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    currentDraggingTab != null) {
                TabAction.detach(currentDraggingTab);
                detachTabDragStack.getChildren().remove(detachTabDragLabel);
                notificationBox.setEffect(null);
            }
        });

        detachTabDragStack = new StackPane(notificationBox);
        HBox.setHgrow(detachTabDragStack, Priority.ALWAYS);
        detachTabDragStack.setPickOnBounds(false);

        headerStack = new StackPane(header, detachTabDragStack);
        headerStack.setPickOnBounds(false);
        StackPane.setAlignment(notificationBox, Pos.CENTER);
        StackPane.setAlignment(detachTabDragLabel, Pos.CENTER);

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        tabPane.setOnMouseClicked(e -> {
            if (slideMenu.isOpened() && slideMenu.isCanToggle()) toggleMenu();
        });
        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(t -> {
                        if (!t.equals(getMainTab())) {
                            addDragHandlers(t);
                        }
                    });
                }
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(t -> {
                        if (!t.equals(getMainTab())) {
                            removeDragHandlers(t);
                        }
                    });
                }
            }
        });
        tabPane.setOnDragOver(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    currentDraggingTab != null &&
                    currentDraggingTab.getTabPane() != tabPane) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });
        tabPane.setOnDragDropped(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    currentDraggingTab != null &&
                    currentDraggingTab.getTabPane() != tabPane) {

                currentDraggingTab.getTabPane().getTabs().remove(currentDraggingTab);
                tabPane.getTabs().add(currentDraggingTab);
                currentDraggingTab.getTabPane().getSelectionModel().select(currentDraggingTab);
            }
        });

        settingsBox = new VBox(settingsContent);
        settingsBox.setId("settings-box");
        settingsBox.setMaxHeight(0);
        settingsBox.setOnMouseClicked(e -> {
            if (slideMenu.isOpened() && slideMenu.isCanToggle()) toggleMenu();
        });

        settingsStack = new StackPane(tabPane, settingsBox);
        StackPane.setAlignment(settingsBox, Pos.TOP_CENTER);

        tabSlide.getChildren().addAll(settingsStack);
        VBox.setVgrow(tabSlide, Priority.ALWAYS);
        StackPane.setAlignment(slideMenu, Pos.CENTER_LEFT);
        StackPane.setAlignment(tabPane, Pos.BOTTOM_CENTER);

        body = new VBox(headerStack, tabSlide);

        /*web = new Web();
        web.setMaxHeight(0);*/

        webAnimartion.setOnFinished(e -> canOpenWeb = true);

        webviewStack = new StackPane(body/*, web*/);
        /*StackPane.setAlignment(web, Pos.BOTTOM_CENTER);*/

        dialogStack = new StackPane(webviewStack);

        Region opaqueLayer = new Region();
        opaqueLayer.setStyle("-fx-background-color: black;");
        opaqueLayer.setOpacity(0.7);

        javafx.scene.control.Label dropHere = new javafx.scene.control.Label(Language.getText("dropfilehere"));
        dropHere.setStyle("-fx-font-size: 50;");

        StackPane dropPane = new StackPane(opaqueLayer, dropHere);
        dropPane.setStyle("-fx-border-color: -fx-defcolor;"
                + "-fx-border-insets: 5;"
                + "-fx-border-width: 5;"
                + "-fx-border-style: dashed;");
        dropPane.setVisible(false);

        Scene scene = new Scene(new StackPane(dialogStack, dropPane), 1000, 700);
        scene.getStylesheets().add(Template.class.getResource("/app/mcsl/windows/styles/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        quitDialog = new QuitDialog();
        stage.setOnCloseRequest(e -> {
            stage.setIconified(false);
            stage.toFront();
            quitDialog.show();
            if (FileManager.getConfigProps().getBoolProp("hideonexit")) quitDialog.hide();
            e.consume();
        });

        dragAndDropDialog = new DragAndDropDialog();
        scene.setOnDragEntered(e -> {
            if (e.getDragboard().hasFiles()) {
                dropPane.setVisible(true);
                dialogStack.setEffect(new GaussianBlur(20));
            }
        });
        scene.setOnDragExited(e -> {
            if (e.getDragboard().hasFiles()) {
                dropPane.setVisible(false);
                dialogStack.setEffect(null);
            }
        });

        scene.setOnDragOver(event -> {
            if (event.getGestureSource() != scene
                    && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                if (db.getFiles().size() == 1) {
                    if (FileManager.getFileExtension(db.getFiles().get(0)).equalsIgnoreCase(".jar")) {
                        dragAndDropDialog.show(db.getFiles().get(0));
                    } else {
                        new AlertDialog(100, 300, Language.getText("error"), Language.getText("fileformatinvalid"), AlertType.ERROR).show();
                    }
                } else {
                    new AlertDialog(100, 300, Language.getText("error"), Language.getText("onlyonefile"), AlertType.ERROR).show();
                }
            }
            event.setDropCompleted(true);
            event.consume();
        });

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F1) {
                String fileName = "Screenshot_" + new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(new Date());
                Image screenShot = FileManager.screenShot(scene);
                File screenShotFile = FileManager.saveImage(screenShot, fileName);

                AlertDialog alertDialog = new AlertDialog(150, 400, Language.getText("screenshot"), Language.getText("imagesaved", fileName), AlertType.SUCCESS);
                alertDialog.keepDefaultButton(true);

                Button openButton = new Button(Language.getText("open"), ButtonType.DEFAULT);
                openButton.setOnAction(ev -> {
                    try {
                        Desktop.getDesktop().open(screenShotFile);
                    } catch (IOException ex) {
                        Logger.exception(ex);
                    }
                    alertDialog.close();
                });

                Button copyButton = new Button(Language.getText("copy"), ButtonType.DEFAULT);
                copyButton.setOnAction(ev -> {
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putImage(new Image(screenShotFile.toURI().toString()));
                    Clipboard.getSystemClipboard().setContent(clipboardContent);

                    showNotification(Language.getText("imagecopied"), LabelColor.ERROR);
                    alertDialog.close();
                });

                alertDialog.addButton(openButton, copyButton);
                alertDialog.show();
            }
        });

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (newNotificationCount > 0) {
                    showNotification(Language.getText("newnotifications", newNotificationCount) + " --->", LabelColor.ERROR);
                    newNotificationCount = 0;
                }
            }
        });

        //web.getWebView().prefHeightProperty().bind(heightProperty());
    }

    public static void show() {
        stage.show();
    }

    public static void toggleMenu() {
        if (slideMenu.isCanToggle()) {
            if (!hamburgerMenuIcon.isRotated()) {
                tabPane.setEffect(new GaussianBlur(5));
                settingsBox.setEffect(new GaussianBlur(5));
            } else {
                if (!isSettingsOpen) tabPane.setEffect(null);
                settingsBox.setEffect(null);
            }
            slideMenu.toggle();
            hamburgerMenuIcon.toggle();
        }
    }

    public static void toggleSettings() {
        if (canOpenSettings) {
            canOpenSettings = false;
            settingsAnimation.getKeyFrames().clear();
            if (isSettingsOpen) {
                settingsAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(settingsBoxHeight, 0.0)));
                isSettingsOpen = false;
                settingsButton.setStyle(null);
                settingsAnimation.setOnFinished(e -> {
                    canOpenSettings = true;
                    if (!slideMenu.isOpened()) tabPane.setEffect(null);
                });
                if (settingsContent.isNeedRestartShowing()) settingsContent.removeNeedRestartBox();
            } else {
                settingsAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(settingsBoxHeight, 1000.0)));
                isSettingsOpen = true;
                settingsButton.setStyle("-fx-background-color: -fx-defdarkcolor;");
                tabPane.setEffect(new GaussianBlur(5));
                settingsAnimation.setOnFinished(e -> canOpenSettings = true);
            }
            settingsAnimation.play();
        }
    }

    /*public void openWeb(String link) {
        if (canOpenWeb) {
            canOpenWeb = false;
            webAnimartion.getKeyFrames().clear();
            if (!isWebOpen) {
                webAnimartion.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(webBoxHeight, 1080.0)));
                webAnimartion.setOnFinished(e -> {
                    canOpenWeb = true;
                    web.loadLink(link);
                });
                isWebOpen = true;
            }
            webAnimartion.play();
        }
    }

    public void closeWeb() {
        if (canOpenWeb) {
            canOpenWeb = false;
            webAnimartion.getKeyFrames().clear();
            if (isWebOpen) {
                webAnimartion.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(webBoxHeight, 0.0)));
                webAnimartion.setOnFinished(e -> canOpenWeb = true);
                isWebOpen = false;
            }
            webAnimartion.play();
        }
    }*/

    public static void showNotification(String text, LabelColor color) {
        if (!isShowingNotification) {
            notifications.remove(text);
            isShowingNotification = true;

            notificationLabel.setText(text);
            notificationLabel.setLabelColor(color);

            notificationAnimation.getKeyFrames().clear();
            notificationAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(mcslImageViewHeight, 0.0)));
            notificationAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(notificationLabelHeight, 30.0)));

            notificationAnimation.play();
            notificationAnimation.setOnFinished(e -> {
                notificationBox.getChildren().remove(mcslImageView);
                notificationLabel.setMinHeight(29);
            });
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
                Platform.runLater(() -> {
                    mcslImageView.setFitHeight(0);
                    if (!notificationBox.getChildren().contains(mcslImageView))
                        notificationBox.getChildren().add(mcslImageView);

                    notificationAnimation.getKeyFrames().clear();
                    notificationAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(mcslImageViewHeight, 30.0)));
                    notificationAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(300), new KeyValue(notificationLabelHeight, 0.0)));

                    notificationAnimation.play();
                    notificationAnimation.setOnFinished(e -> {
                        isShowingNotification = false;
                        if (notifications.size() > 0) {
                            showNotification(notifications.keySet().toArray()[0].toString(), notifications.get(notifications.keySet().toArray()[0].toString()));
                        }
                    });
                });
            });
        } else {
            if (!notifications.containsKey(text) && !notificationLabel.getText().equalsIgnoreCase(text))
                notifications.put(text, color);
        }
    }

    public static void addNotification(Notification notification, boolean notify) {
        if (notificationButton.getItems().size() < 6) {
            if (notificationButton.getItems().size() > 1) {
                MenuItem[] notifications = new MenuItem[notificationButton.getItems().size()];
                notifications[0] = notification;
                for (int i = 1; i < notificationButton.getItems().size(); i++) {
                    notifications[i] = notificationButton.getItems().get(i);
                }
                for (int i = 0; i < notifications.length - 1; i++) {
                    notificationButton.getItems().set(i + 1, notifications[i]);
                }
                notificationButton.getItems().add(notifications[notifications.length - 1]);
            } else {
                notificationButton.getItems().add(notification);
            }
        } else {
            MenuItem[] notifications = new MenuItem[5];
            notifications[0] = notification;
            notifications[1] = notificationButton.getItems().get(1);
            notifications[2] = notificationButton.getItems().get(2);
            notifications[3] = notificationButton.getItems().get(3);
            notifications[4] = notificationButton.getItems().get(4);
            for (int i = 0; i < 5; i++) {
                notificationButton.getItems().set(i + 1, notifications[i]);
            }
        }
        if (!notificationButtonStack.getChildren().contains(notificationCircle) && notify) {
            notificationButtonStack.getChildren().add(notificationCircle);
        }
        if (notify) newNotificationCount++;
    }

    public static void addDragHandlers(Tab tab) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(tab.getText(), tab.getGraphic());
        if (tab.getText() != null && !tab.getText().isEmpty()) {
            tab.setText(null);
            tab.setGraphic(label);
        }

        Node graphic = tab.getGraphic();
        graphic.setOnDragDetected(e -> {
            Dragboard dragboard = graphic.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(draggingID);
            dragboard.setContent(content);
            dragboard.setDragView(graphic.snapshot(null, null));
            currentDraggingTab = tab;

            if (!detachTabDragStack.getChildren().contains(detachTabDragLabel)) {
                detachTabDragStack.getChildren().add(detachTabDragLabel);
                notificationBox.setEffect(new GaussianBlur(20));
            }
        });

        graphic.setOnDragOver(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    currentDraggingTab != null &&
                    currentDraggingTab.getGraphic() != graphic) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });

        graphic.setOnDragDropped(e -> {
            if (draggingID.equals(e.getDragboard().getString()) &&
                    currentDraggingTab != null &&
                    currentDraggingTab.getGraphic() != graphic) {
                int index = tab.getTabPane().getTabs().indexOf(tab);
                currentDraggingTab.getTabPane().getTabs().remove(currentDraggingTab);
                tab.getTabPane().getTabs().add(index, currentDraggingTab);
                currentDraggingTab.getTabPane().getSelectionModel().select(currentDraggingTab);
            }
        });

        graphic.setOnDragDone(e -> {
            currentDraggingTab = null;
            detachTabDragStack.getChildren().remove(detachTabDragLabel);
            notificationBox.setEffect(null);
        });
    }

    public static void removeDragHandlers(Tab tab) {
        tab.getGraphic().setOnDragDetected(null);
        tab.getGraphic().setOnDragOver(null);
        tab.getGraphic().setOnDragDropped(null);
        tab.getGraphic().setOnDragDone(null);
    }

    public static void setUpTabs() {
        TabAction.add(serversContent, new ImageView(FileManager.SERVER_ICON), true);
        currentTabClass = serversContent;
    }

    public static Stage getStage() {
        return stage;
    }

    public static Tab getMainTab() {
        return mainTab;
    }

    public static void setMainTab(Tab mainTab) {
        Template.mainTab = mainTab;
    }

    public static void setCurrentTabClass(TabClass currentTabClass) {
        Template.currentTabClass = currentTabClass;
    }

    public static TabPane getTabPane() {
        return tabPane;
    }

    public static SlideMenu getSlideMenu() {
        return slideMenu;
    }

    public static boolean isSettingsOpen() {
        return isSettingsOpen;
    }

    public static StackPane getDialogStack() {
        return dialogStack;
    }

    public static VBox getSettingsBox() {
        return settingsBox;
    }

    public static VBox getBody() {
        return body;
    }

    public static StackPane getWebviewStack() {
        return webviewStack;
    }

    public static ServersContent getServersContent() {
        return serversContent;
    }

    public static QuitDialog getQuitDialog() {
        return quitDialog;
    }
}
