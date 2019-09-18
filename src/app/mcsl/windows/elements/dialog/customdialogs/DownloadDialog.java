package app.mcsl.windows.elements.dialog.customdialogs;

import app.mcsl.managers.Language;
import app.mcsl.managers.download.Downloader;
import app.mcsl.utils.DateTimeUtils;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.Dialog;
import app.mcsl.windows.elements.dialog.DialogType;
import app.mcsl.windows.elements.dialog.types.AlertDialog;
import app.mcsl.windows.elements.dialog.types.AlertType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadDialog extends Dialog {

    public DownloadDialog(Downloader downloader) {
        super(200, 500, Language.getText("download"), DialogType.CUSTOM, new VBox());

        Label downloadTitle = new Label("File: " + downloader.getFileName() + " (" + downloader.getProgressPercent("#.##") + "%) (" + downloader.getDownloadedInMb("#.##") + "MB/" + downloader.getFileSizeInMb("#.##") + "MB) (" + downloader.getDownloadSpeedInMb("#.##") + "MB/s)", LabelType.H3);
        Label footerTitle = new Label("Elapsed time: " + DateTimeUtils.calculateTime(downloader.getElapsedSecond()) + " | Remaining: " + DateTimeUtils.calculateTime(downloader.getRemainingSecond()), LabelType.H3);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setMinSize(500, 20);
        progressBar.progressProperty().bind(downloader.getProgress());

        HBox progressBox = new HBox(progressBar);
        progressBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(progressBox, Priority.ALWAYS);

        VBox body = new VBox(5, downloadTitle, progressBox, footerTitle);

        Button startButton = new Button(Language.getText("start"), ButtonType.ERROR);
        startButton.setOnAction(e -> {
            /*if (!downloader.isStarted() || (downloader.isStarted() && downloader.isPaused())) {
                downloader.start();
                startButton.setText("Pause");
                return;
            }
            downloader.pause();
            startButton.setText("Start");*/
            downloader.start();
            startButton.setDisable(true);
        });

        Button cancelButton = new Button(Language.getText("cancel"), ButtonType.ERROR);
        cancelButton.setOnAction(e -> {
            downloader.cancel();
            cancelButton.setDisable(true);
        });

        setContent(body);
        addButton(startButton, cancelButton);

        build();

        downloader.getProgress().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                downloadTitle.setText("File: " + downloader.getFileName() + " (" + downloader.getProgressPercent("#.##") + "%) (" + downloader.getDownloadedInMb("#.##") + "MB/" + downloader.getFileSizeInMb("#.##") + "MB) (" + downloader.getDownloadSpeedInMb("#.##") + "MB/s)");
                footerTitle.setText("Elapsed time: " + DateTimeUtils.calculateTime(downloader.getElapsedSecond()) + " | Remaining: " + DateTimeUtils.calculateTime(downloader.getRemainingSecond()));
            });
        });

        downloader.setOnFinished(status -> {
            switch (status) {
                case SUCCESS:
                    System.out.println("Download successs");
                    close();
                    new AlertDialog(200, 400, Language.getText("download"), "File '" + downloader.getFileName() + "' downloaded successfully!", AlertType.SUCCESS).show();
                    break;
                case CANCELLED:
                    System.out.println("Download canceleed");
                    close();
                    new AlertDialog(200, 400, Language.getText("download"), "The download has been cancelled!", AlertType.WARNING).show();
                    break;
                case ERROR:
                    System.out.println("Download error");
                    close();
                    new AlertDialog(200, 400, Language.getText("download"), "An error occurred during the download!", AlertType.ERROR).show();
                    break;
            }
        });
    }
}
