package app.mcsl;

import app.mcsl.manager.Language;
import app.mcsl.manager.UpdateManager;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.logging.Logger;
import app.mcsl.manager.mainside.OSManager;
import app.mcsl.manager.mainside.TrayManager;
import app.mcsl.manager.mainside.timedtask.TimedTasksTimer;
import app.mcsl.manager.theme.FontType;
import app.mcsl.manager.theme.ThemeColor;
import app.mcsl.manager.theme.ThemeManager;
import app.mcsl.manager.theme.ThemeType;
import app.mcsl.manager.version.FileUpdateStatusType;
import app.mcsl.manager.version.FileUpdater;
import app.mcsl.window.Splash;
import app.mcsl.window.Template;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import app.mcsl.window.element.dialog.customdialog.ChangelogDialog;
import app.mcsl.window.element.dialog.customdialog.WelcomeDialog;
import app.mcsl.window.element.dialog.type.AlertDialog;
import app.mcsl.window.element.dialog.type.AlertType;
import com.dosse.upnp.UPnP;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author S3nS3IW00
 */
public class MainClass extends Application {

    public static final String VERSION = "0.1.2-beta";

    private File sessionFile;
    private FileChannel sessionChannel;
    private FileLock sessionLock;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> timedTasksTask;

    public static boolean SHOW_WELCOME = false;
    public static boolean SHOW_UPDATED = false;

    private boolean testedOpSys = false;

    private FileUpdateStatusType fileUpdateStatusType;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Splash splash = new Splash();
        splash.show();

        if (OSManager.getOs() == OSManager.OS.WINDOWS || OSManager.getOs() == OSManager.OS.UNIX) testedOpSys = true;

        FileUpdater fileUpdater = new FileUpdater();
        try {
            fileUpdateStatusType = fileUpdater.updateFiles();
        } catch (IOException e) {
            fileUpdateStatusType = FileUpdateStatusType.CANNOT_UPDATE;
        }

        synchronized (fileUpdateStatusType) {
            Logger.init();

            //Print system info
            Logger.debug("MCSL version: " + VERSION);
            Logger.debug("MCSL home: " + OSManager.getRoot().getAbsolutePath());
            Logger.debug("OS name: " + System.getProperty("os.name"));
            Logger.debug("OS version: " + System.getProperty("os.version"));
            Logger.debug("OS architecture: " + System.getProperty("os.arch"));
            Logger.debug("Java version: " + System.getProperty("java.version"));
            Logger.debug("Java home: " + System.getProperty("java.home"));
            Logger.emptyLine();

            if (!testedOpSys) Logger.warn("Operating system not supported!");

            Logger.info("Verifying session...");
            sessionFile = new File(OSManager.getRoot() + File.separator + "session");
            if (sessionFile.exists()) {
                sessionFile.delete();
            }
            sessionChannel = new RandomAccessFile(sessionFile, "rw").getChannel();
            sessionLock = sessionChannel.tryLock();
            if (sessionLock == null) {
                Logger.error("Session is already in use! Look for the running application!");
                sessionChannel.close();
                System.exit(0);
                return;
            }
            Logger.info("Session is no longer in use. New session is created successfully!");

            Platform.runLater(new Thread(() -> {
                try {
                    FileManager.checkFiles();
                } catch (IOException e) {
                    Logger.error("Can't verify the necessary files for the following reason:");
                    Logger.exception(e);
                }

                Language.loadLanguage(FileManager.getConfigProps().getProp("language"));

                Logger.info("Initiating application's stage...");
                Template.build();
                Template.setUpTabs();

                TrayManager.runTray();

                FileManager.loadServers();

                ThemeManager.changeThemeColor(ThemeColor.valueOf(FileManager.getConfigProps().getProp("themecolor").toUpperCase()));
                ThemeManager.changeThemeType(ThemeType.valueOf(FileManager.getConfigProps().getProp("themetype").toUpperCase()));
                if (FileManager.getConfigProps().getBoolProp("fancyfont")) ThemeManager.changeFontType(FontType.FANCY);

                Logger.info("Initiating UPnP...");
                UPnP.waitInit();

                Logger.info("Starting timed tasks's timer...");
                timedTasksTask = scheduledExecutorService.scheduleAtFixedRate(new TimedTasksTimer(), 0, 1, TimeUnit.SECONDS);

                Logger.info("Verifying updates...");
                if (UpdateManager.needUpdate()) {
                    AlertDialog updateAlert = new AlertDialog(250, 400, Language.getText("update"), Language.getText("updateavailable", VERSION, UpdateManager.getLatestVersion()), AlertType.SUCCESS);
                    Button downloadButton = new Button(Language.getText("download"), ButtonType.APPLY);
                    downloadButton.setOnAction(e -> {
                        try {
                            Desktop.getDesktop().browse(new URI(UpdateManager.getHtmlUrl()));
                        } catch (IOException | URISyntaxException ex) {
                            Logger.exception(ex);
                        }
                        updateAlert.close();
                    });
                    updateAlert.addButton(downloadButton);
                    updateAlert.keepDefaultButton(true);
                    updateAlert.show();

                    Logger.warn("Update found!");
                }

                Logger.info("Showing application's window...");
                Template.show();
                splash.close();

                if (SHOW_WELCOME) new WelcomeDialog().show();
                if (!testedOpSys)
                    new AlertDialog(300, 400, Language.getText("warning"), Language.getText("nottestedopsys"), AlertType.WARNING).show();
                if (SHOW_UPDATED) new ChangelogDialog().show();
                if (fileUpdateStatusType != FileUpdateStatusType.NOTHING) {
                    new AlertDialog(250, 400, Language.getText("fileupdate"), Language.getText(fileUpdateStatusType.getLangCode()), fileUpdateStatusType.getAlertType()).show();
                    if (fileUpdateStatusType == FileUpdateStatusType.RECREATED_WITH_COPY)
                        FileManager.deleteFile(new File(FileManager.getRoot() + "_old"));
                }

                Logger.info("Startup done! (" + Logger.getWarnCount() + " warning, " + Logger.getErrorCount() + " error, " + Logger.getExceptionCount() + " exception)");
                Template.DEBUG_CONSOLE.init();
            }));
        }
    }
}