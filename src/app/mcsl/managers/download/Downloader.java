package app.mcsl.managers.download;

import app.mcsl.managers.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Downloader {

    private String fileUrl, destinationDir;
    private int threadCount;
    private Thread[] threads;
    private DownloadTask[] downloadTasks;
    private boolean isStarted = false, isCancelled = false, isPaused = false;

    private File downloadedFile;

    private TimerTask timerTask;
    private Timer timer;
    private int elapsedSecond = 0, remainingSecond = 0;

    private int downloadedNRead = 0, currentNRead = 0, nReadPerSec = 0;
    private double downloadSpeedInMb = 0.0;

    private int contentLength;

    private DoubleProperty progress = new SimpleDoubleProperty(0.0);

    private DownloadStatus status;
    private DownloadFinishEvent finishEvent;

    public Downloader(String fileUrl, String destinationDir, int threadCount) {
        this.fileUrl = fileUrl;
        this.destinationDir = destinationDir;
        this.threadCount = threadCount;
        this.threads = new Thread[threadCount];
        this.downloadTasks = new DownloadTask[threadCount];

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(120000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            if (conn.getResponseCode() == 200) {
                contentLength = conn.getContentLength();
                int blocksize = contentLength / threadCount;
                downloadedFile = new File(destinationDir + File.separator + getFileName());
                RandomAccessFile randomAccessFile = new RandomAccessFile(downloadedFile, "rwd");
                randomAccessFile.setLength(contentLength);
                randomAccessFile.close();
                conn.disconnect();
                for (int i = 0; i < threadCount; i++) {
                    int startpos = i * blocksize;
                    int endpos = (i + 1) * blocksize - 1;
                    if (i == threadCount - 1) {
                        endpos = contentLength;
                    }
                    downloadTasks[i] = new DownloadTask(this, startpos, endpos);
                    threads[i] = new Thread(downloadTasks[i]);
                }
            } else {
                assert finishEvent != null;
                finishEvent.onFinished(status);
            }
        } catch (IOException e) {
            Logger.exception(e);
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                int allNread = 0;
                for (DownloadTask downloadTask : downloadTasks) {
                    allNread += downloadTask.getNread();
                }
                nReadPerSec = allNread - currentNRead;
                currentNRead = allNread;
                downloadSpeedInMb = (double) nReadPerSec / 1048576.0;
                elapsedSecond++;
                if (getDownloadSpeedInMb() > 0)
                    remainingSecond = (int) ((getFileSizeInMb() - getDownloadedInMb()) / getDownloadSpeedInMb());

                Platform.runLater(() -> checkRunning());
            }
        };
    }

    private void checkRunning() {
        for (DownloadTask downloadTask : downloadTasks) {
            if (downloadTask.isRunning()) return;
        }
        if (status == null) {
            for (DownloadTask downloadTask : downloadTasks) {
                if (downloadTask.getStatus() == DownloadStatus.ERROR) {
                    status = DownloadStatus.ERROR;
                    break;
                }
            }
        }
        if (status == null) status = DownloadStatus.SUCCESS;
        timer.cancel();
        if (status != DownloadStatus.SUCCESS) downloadedFile.delete();
        assert finishEvent != null;
        finishEvent.onFinished(status);
    }

    public void start() {
        if (isStarted) return;
        if (isPaused) {
            for (Thread thread : threads) notify();
            isPaused = false;
            return;
        }
        for (Thread thread : threads) thread.start();
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
        isStarted = true;
    }

    public void cancel() {
        if (!isStarted) return;
        isCancelled = true;
        status = DownloadStatus.CANCELLED;
        for (DownloadTask downloadTask : downloadTasks) downloadTask.cancel(true);
    }

    void error() {
        isCancelled = true;
        status = DownloadStatus.ERROR;
        for (DownloadTask downloadTask : downloadTasks) downloadTask.cancel(true);
    }

    /*not working yet
    public void pause(){
        if(!isStarted || isPaused) return;
        isPaused = true;
        for(Thread thread : threads) {
            try {
                thread.wait();
            } catch (InterruptedException e) {
                Logger.exception(e);
            }
        }
    }*/

    void updateProgress() {
        int allNread = 0;
        for (DownloadTask downloadTask : downloadTasks) {
            allNread += downloadTask.getNread();
        }
        downloadedNRead = allNread;
        progress.setValue((double) allNread / (double) contentLength);
    }

    //GETTERS
    public String getFileName() {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getDestinationDir() {
        return destinationDir;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public DoubleProperty getProgress() {
        return progress;
    }

    public double getProgressPercent() {
        return progress.getValue() * 100.0;
    }

    public double getProgressPercent(String decimalFormat) {
        return Double.parseDouble(new DecimalFormat(decimalFormat).format(progress.getValue() * 100.0).replaceAll(",", "."));
    }

    public double getDownloadSpeedInMb() {
        return downloadSpeedInMb;
    }

    public double getDownloadSpeedInMb(String decimalFormat) {
        return Double.parseDouble(new DecimalFormat(decimalFormat).format(downloadSpeedInMb).replaceAll(",", "."));
    }

    public double getFileSizeInMb() {
        return (double) contentLength / 1048576.0;
    }

    public double getFileSizeInMb(String decimalFormat) {
        return Double.parseDouble(new DecimalFormat(decimalFormat).format((double) contentLength / 1048576.0).replaceAll(",", "."));
    }

    public double getDownloadedInMb() {
        return (double) downloadedNRead / 1048576.0;
    }

    public double getDownloadedInMb(String decimalFormat) {
        return Double.parseDouble(new DecimalFormat(decimalFormat).format((double) downloadedNRead / 1048576.0).replaceAll(",", "."));
    }

    public int getElapsedSecond() {
        return elapsedSecond;
    }

    public int getRemainingSecond() {
        return remainingSecond;
    }

    public boolean isDone() {
        for (DownloadTask downloadTask : downloadTasks) {
            if (!downloadTask.isDone()) {
                return false;
            }
        }
        return true;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setOnFinished(DownloadFinishEvent finishEvent) {
        this.finishEvent = finishEvent;
    }
}