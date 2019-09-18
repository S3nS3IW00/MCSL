package app.mcsl.managers.download;

import app.mcsl.managers.logging.Logger;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadTask extends Task<Boolean> {

    private Downloader download;
    private int startpos, endpos, nread = 0;

    private DownloadStatus status;

    DownloadTask(Downloader download, int startpos, int endpos) {
        this.download = download;
        this.startpos = startpos;
        this.endpos = endpos;
    }

    @Override
    protected Boolean call() {
        try {
            URL url = new URL(download.getFileUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(120000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=" + startpos + "-" + endpos + "");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            if (conn.getResponseCode() == 206) {
                int partLength = conn.getContentLength();

                InputStream is = conn.getInputStream();
                RandomAccessFile randomAccessFile = new RandomAccessFile(new File(download.getDestinationDir() + File.separator + download.getFileName()), "rwd");
                randomAccessFile.seek(startpos);

                int len;
                byte[] buffer = new byte[8192];
                while ((len = is.read(buffer)) != -1 && !download.isCancelled()) {
                    nread += len;
                    randomAccessFile.write(buffer, 0, len);
                    download.updateProgress();
                }
                is.close();
                randomAccessFile.close();
                conn.disconnect();
                succeeded();
                return true;
            } else {
                failed();
                return false;
            }
        } catch (IOException e) {
            Logger.exception(e);
            failed();
            download.error();
            return false;
        }
    }

    int getNread() {
        return nread;
    }

    @Override
    protected void succeeded() {
        status = DownloadStatus.SUCCESS;
        super.succeeded();
    }

    @Override
    protected void cancelled() {
        status = DownloadStatus.CANCELLED;
        super.cancelled();
    }

    @Override
    protected void failed() {
        status = DownloadStatus.ERROR;
        super.failed();
    }

    public DownloadStatus getStatus() {
        return status;
    }
}
