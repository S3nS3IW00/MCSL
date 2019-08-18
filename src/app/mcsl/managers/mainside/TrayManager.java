package app.mcsl.managers.mainside;

import app.mcsl.MainClass;
import app.mcsl.managers.Language;
import app.mcsl.managers.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrayManager {

    private final static SystemTray tray = SystemTray.getSystemTray();
    private TrayIcon trayIcon;

    public TrayManager() {

        TrayIcon trayIcon = new TrayIcon(SwingFXUtils.fromFXImage(MainClass.getTemplate().getIcons().get(0), null), "");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Minecraft Server Launcher v"+MainClass.VERSION);
        this.trayIcon = trayIcon;
    }

    public void displayTray(String text, TrayIcon.MessageType mt) {
        if (SystemTray.isSupported()) {
            if (MainClass.getFileManager().getConfigProps().getBoolProp("notifications")) {
                TrayIcon[] trayicons = tray.getTrayIcons();
                trayicons[0].displayMessage("Minecraft Server Launcher", text, mt);
            }
        }
    }

    public void runTray() {
        if (SystemTray.isSupported()) {
            try {
                tray.add(getTrayIcon());
                getTrayIcon().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!MainClass.getTemplate().isShowing()) {
                            Platform.runLater(() -> {
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    if (MainClass.getTemplate().isShowing()) {
                                        MainClass.getTemplate().setIconified(false);
                                        MainClass.getTemplate().toFront();
                                    } else {
                                        Logger.info("Showing application...");
                                        MainClass.getTemplate().show();
                                    }
                                }
                            });
                        }
                    }
                });

                MenuItem exitItem = new MenuItem(Language.getText("quit"));
                exitItem.addActionListener(e -> Platform.runLater(() -> {
                    if (MainClass.getTemplate().isShowing()) {
                        MainClass.getTemplate().setIconified(false);
                        MainClass.getTemplate().toFront();
                    } else {
                        MainClass.getTemplate().show();
                    }
                    MainClass.getTemplate().getQuitDialog().quit();
                }));

                PopupMenu popupMenu = new PopupMenu();
                popupMenu.add(exitItem);

                getTrayIcon().setPopupMenu(popupMenu);
            } catch (AWTException ex) {
                Logger.exception(ex);
            }
        }
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }
}
