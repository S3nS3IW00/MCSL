package app.mcsl.windows.elements.notifications;

import app.mcsl.MainClass;
import app.mcsl.managers.Language;
import app.mcsl.managers.tab.TabClass;
import app.mcsl.windows.elements.label.LabelColor;

import java.awt.*;

public class Notifications {

    public static void push(TabClass from, Notification notification) {
        if (!MainClass.getTemplate().isFocused()) {
            MainClass.getTrayManager().displayTray(notification.getNotifText(), TrayIcon.MessageType.valueOf(notification.getType().name()));
        }
        if (from == null || from.getContent() != MainClass.getTemplate().getTabPane().getSelectionModel().getSelectedItem().getContent()) {
            MainClass.getTemplate().showNotification(Language.getText("newnotification"), LabelColor.ERROR);
        }
        MainClass.getFileManager().addNotification(notification);
        MainClass.getTemplate().addNotification(notification, true);
    }

}
