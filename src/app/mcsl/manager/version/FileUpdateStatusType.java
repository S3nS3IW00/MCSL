package app.mcsl.manager.version;

import app.mcsl.window.element.dialog.type.AlertType;

public enum FileUpdateStatusType {

    UPDATED("fileupdated", AlertType.SUCCESS),
    RECREATED_WITH_COPY("filerecreatedwithcopy", AlertType.WARNING),
    CANNOT_UPDATE("filecannotupdate", AlertType.ERROR),
    NOTHING("", AlertType.DEFAULT);

    private String langCode;
    AlertType alertType;

    FileUpdateStatusType(String langCode, AlertType alertType) {
        this.langCode = langCode;
        this.alertType = alertType;
    }

    public String getLangCode() {
        return langCode;
    }

    public AlertType getAlertType() {
        return alertType;
    }
}
