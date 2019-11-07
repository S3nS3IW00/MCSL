package app.mcsl.event;

import app.mcsl.manager.download.DownloadStatus;

public interface DownloadFinishEvent {

    void onFinished(DownloadStatus status);

}
