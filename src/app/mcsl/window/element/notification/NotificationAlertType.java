package app.mcsl.window.element.notification;

import app.mcsl.manager.file.FileManager;
import javafx.scene.image.Image;

public enum NotificationAlertType {

    INFO(FileManager.INFO_ICON), ERROR(FileManager.ERROR_ICON), WARNING(FileManager.WARNING_ICON);

    Image icon;

    NotificationAlertType(Image icon) {
        this.icon = icon;
    }

    public Image getIcon() {
        return icon;
    }
}
