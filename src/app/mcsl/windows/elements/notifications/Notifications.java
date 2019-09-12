package app.mcsl.windows.elements.notifications;

import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.mainside.TrayManager;
import app.mcsl.managers.tab.TabClass;
import app.mcsl.windows.Template;
import app.mcsl.windows.elements.label.LabelColor;

import java.awt.*;

public class Notifications {

    public static void push(TabClass from, Notification notification) {
        if (!Template.getStage().isFocused()) {
            TrayManager.displayTray(notification.getNotifText(), TrayIcon.MessageType.valueOf(notification.getType().name()));
        }
        if (from == null || from.getContent() != Template.getTabPane().getSelectionModel().getSelectedItem().getContent()) {
            Template.showNotification(Language.getText("newnotification"), LabelColor.ERROR);
            FileManager.addNotification(notification);
            Template.addNotification(notification, true);
        }
    }

}
