package app.mcsl.manager.mainside;

import app.mcsl.MainClass;
import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.logging.Logger;
import app.mcsl.manager.tab.TabManager;
import app.mcsl.window.Template;
import app.mcsl.window.content.server.ServerStage;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TrayManager {

    private final static SystemTray tray = SystemTray.getSystemTray();
    private static TrayIcon trayIcon;

    public static void displayTray(String text, TrayIcon.MessageType mt) {
        if (SystemTray.isSupported()) {
            if (FileManager.getConfigProps().getBoolProp("notifications")) {
                TrayIcon[] trayicons = tray.getTrayIcons();
                trayicons[0].displayMessage("Minecraft Server Launcher", text, mt);
            }
        }
    }

    public static void runTray() {
        TrayIcon trayIcon = new TrayIcon(SwingFXUtils.fromFXImage(Template.getStage().getIcons().get(0), null), "");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Minecraft Server Launcher v" + MainClass.VERSION);
        TrayManager.trayIcon = trayIcon;

        Logger.info("Adding system tray...");
        if (SystemTray.isSupported()) {
            try {
                tray.add(getTrayIcon());
                getTrayIcon().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!Template.getStage().isShowing()) {
                            Platform.runLater(() -> {
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    if (Template.getStage().isShowing()) {
                                        Template.getStage().setIconified(false);
                                        Template.getStage().toFront();
                                        for (ServerStage serverStage : TabManager.getServerStages()) {
                                            serverStage.setIconified(false);
                                            serverStage.toFront();
                                        }
                                    } else {
                                        Logger.info("Showing application...");
                                        Template.getStage().show();
                                        for (ServerStage serverStage : TabManager.getServerStages()) {
                                            serverStage.show();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

                MenuItem exitItem = new MenuItem(Language.getText("quit"));
                exitItem.addActionListener(e -> Platform.runLater(() -> {
                    if (Template.getStage().isShowing()) {
                        Template.getStage().setIconified(false);
                        Template.getStage().toFront();
                    } else {
                        Template.getStage().show();
                    }
                    Template.getQuitDialog().quit();
                }));

                PopupMenu popupMenu = new PopupMenu();
                popupMenu.add(exitItem);

                getTrayIcon().setPopupMenu(popupMenu);
            } catch (AWTException ex) {
                Logger.exception(ex);
            }
        }
    }

    private static TrayIcon getTrayIcon() {
        return trayIcon;
    }
}
