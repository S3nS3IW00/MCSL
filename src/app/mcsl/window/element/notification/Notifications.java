package app.mcsl.window.element.notification;

import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.mainside.TrayManager;
import app.mcsl.manager.tab.TabClass;
import app.mcsl.window.Template;
import app.mcsl.window.element.label.LabelColor;

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
