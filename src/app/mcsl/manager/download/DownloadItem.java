package app.mcsl.manager.download;

public class DownloadItem {

    private String displayName, fileName, downloadUrl;
    private DownloadType type;
    private Downloader downloader;

    public DownloadItem(String displayName, String downloadUrl, DownloadType type) {
        this.displayName = displayName;
        fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        this.downloadUrl = downloadUrl;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public DownloadType getType() {
        return type;
    }

    public void setDownloader(Downloader downloader) {
        this.downloader = downloader;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public String getFileName() {
        return fileName;
    }
}
