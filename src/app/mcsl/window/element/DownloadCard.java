package app.mcsl.window.element;

import app.mcsl.event.DirectoryChangeEvent;
import app.mcsl.manager.Language;
import app.mcsl.manager.download.DownloadItem;
import app.mcsl.manager.download.DownloadStatus;
import app.mcsl.manager.download.DownloadType;
import app.mcsl.manager.download.Downloader;
import app.mcsl.manager.file.DirectoryType;
import app.mcsl.manager.file.FileManager;
import app.mcsl.window.Template;
import app.mcsl.window.element.dialog.type.AlertDialog;
import app.mcsl.window.element.dialog.type.AlertType;
import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelColor;
import app.mcsl.window.element.label.LabelType;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;

public class DownloadCard extends StackPane {

    private DownloadItem item;
    private VBox overlayBox;
    private ImageView overlayIconView;

    private Timeline overlayInTimeline = new Timeline(), overlayOutTimeline = new Timeline();
    private WritableValue<Double> overlayHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return overlayBox.getMaxHeight();
        }

        @Override
        public void setValue(Double value) {
            overlayBox.setMaxHeight(value);
        }
    };

    public DownloadCard(DownloadItem item) {
        this.item = item;

        ImageView iconView = new ImageView(FileManager.FILE_ICON_80);
        iconView.setStyle("-fx-effect: innershadow(gaussian, -fx-defcolor, 7, 1, 1, 1);");

        VBox iconBox = new VBox(iconView);
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setMinSize(200, 100);
        iconBox.setPrefSize(200, 100);

        Label displayNameLabel = new Label(item.getDisplayName(), LabelType.H3, LabelColor.THIRDCOLOR);
        displayNameLabel.setAlignment(Pos.CENTER);
        displayNameLabel.setWrapText(true);
        displayNameLabel.setMaxWidth(180);

        ProgressBar downloadProgressBar = new ProgressBar();
        downloadProgressBar.setMinWidth(200);
        downloadProgressBar.setMinHeight(20);
        downloadProgressBar.setProgress(0.0);

        VBox footerBox = new VBox(5, displayNameLabel, downloadProgressBar);
        footerBox.setAlignment(Pos.BOTTOM_CENTER);
        footerBox.setMinSize(200, 100);
        footerBox.setPrefSize(200, 100);

        VBox bodyBox = new VBox(iconBox, footerBox);

        overlayIconView = new ImageView();
        overlayIconView.setPreserveRatio(false);
        overlayIconView.setStyle("-fx-effect: innershadow(gaussian, -fx-thirdcolor, 20, 1, 1, 1);");

        overlayBox = new VBox();
        overlayBox.setOpacity(0.7);
        overlayBox.setAlignment(Pos.CENTER);
        overlayBox.setMinSize(200, 0);
        overlayBox.setMaxSize(200, 0);
        overlayBox.setOnMouseClicked(e -> {
            overlayOutTimeline.play();
            String targetDir = null;
            if (item.getType() == DownloadType.SERVERFILE) {
                if (FileManager.getServerFile(item.getFileName()).exists()) {
                    new AlertDialog(200, 400, Language.getText("error"), Language.getText("filealreadydownloaded"), AlertType.ERROR).show();
                    return;
                } else {
                    targetDir = FileManager.getServerFilesFolder().getAbsolutePath();
                }
            } else if (item.getType() == DownloadType.PLUGIN) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(Language.getText("choose"));
                File directory = directoryChooser.showDialog(Template.getStage());
                if (directory != null) {
                    targetDir = directory.getAbsolutePath();
                }
            }

            if (targetDir == null) {
                new AlertDialog(200, 400, Language.getText("error"), Language.getText("targetdirnull"), AlertType.ERROR).show();
                return;
            }

            Downloader downloader = item.getDownloader();
            if (downloader == null || downloader.getStatus() != DownloadStatus.IN_PROGRESS) {
                downloader = new Downloader(item.getDownloadUrl(), targetDir, 20);
                downloader.setOnFinished(status -> {
                    downloadProgressBar.progressProperty().unbind();
                    downloadProgressBar.setProgress(0.0);
                    if (status == DownloadStatus.SUCCESS) {
                        item.setDownloader(null);
                        DirectoryChangeEvent.change(DirectoryType.SERVERFILE);
                    }
                });
                item.setDownloader(downloader);
                downloadProgressBar.progressProperty().bind(downloader.getProgress());
                downloader.start();
            } else {
                downloader.cancel();
            }
        });

        overlayInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(overlayHeight, 200.0)));
        overlayInTimeline.setOnFinished(e -> bodyBox.setEffect(new GaussianBlur(10)));
        overlayOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(overlayHeight, 0.0)));
        overlayOutTimeline.setOnFinished(e -> {
            bodyBox.setEffect(null);
            overlayBox.getChildren().remove(overlayIconView);
        });

        getChildren().addAll(bodyBox, overlayBox);
        setOnMouseEntered(e -> {
            if (item.getDownloader() == null || item.getDownloader().getStatus() != DownloadStatus.IN_PROGRESS) {
                overlayBox.setStyle("-fx-background-color: -fx-apply");
                overlayIconView.setImage(FileManager.DOWNLOAD_ICON_100);
            } else {
                overlayBox.setStyle("-fx-background-color: -fx-error");
                overlayIconView.setImage(FileManager.CANCEL_ICON_100);
            }
            if (!overlayBox.getChildren().contains(overlayIconView)) overlayBox.getChildren().add(overlayIconView);
            overlayInTimeline.play();
        });

        setOnMouseExited(e -> overlayOutTimeline.play());

        StackPane.setAlignment(overlayBox, Pos.BOTTOM_CENTER);

        setId("download-card");
        setMaxSize(200, 200);
        setMinSize(200, 200);
        setPrefSize(200, 200);
    }

}
