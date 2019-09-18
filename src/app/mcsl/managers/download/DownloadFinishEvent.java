package app.mcsl.managers.download;

public interface DownloadFinishEvent {

    void onFinished(DownloadStatus status);

}
