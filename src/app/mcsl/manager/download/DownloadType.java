package app.mcsl.manager.download;

import app.mcsl.manager.Language;

public enum DownloadType {

    SERVERFILE(Language.getText("serverfile")), PLUGIN("Plugin");

    private String displayName;

    DownloadType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
