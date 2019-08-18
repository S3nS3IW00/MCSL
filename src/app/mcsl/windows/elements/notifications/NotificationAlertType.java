package app.mcsl.windows.elements.notifications;

import app.mcsl.managers.file.FileManager;
import javafx.scene.image.Image;

public enum NotificationAlertType {

    INFO(FileManager.INFO_ICON), ERROR(FileManager.ERROR_ICON), WARNING(FileManager.WARNING_ICON);

    Image icon;
    NotificationAlertType(Image icon){
        this.icon = icon;
    }

    public Image getIcon() {
        return icon;
    }
}
