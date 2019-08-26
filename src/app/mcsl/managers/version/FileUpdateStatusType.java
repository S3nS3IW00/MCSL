package app.mcsl.managers.version;

import app.mcsl.windows.elements.dialog.types.AlertType;

public enum FileUpdateStatusType {

    UPDATED("fileupdated", AlertType.SUCCESS),
    RECREATED_WITH_COPY("filerecreatedwithcopy", AlertType.WARNING),
    CANNOT_UPDATE("filecannotupdate", AlertType.ERROR);

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
