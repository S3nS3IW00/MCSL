package app.mcsl;

import app.mcsl.managers.Language;
import app.mcsl.managers.UpdateManager;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.mainside.OSManager;
import app.mcsl.managers.mainside.TrayManager;
import app.mcsl.managers.mainside.timedtasks.TimedTasksTimer;
import app.mcsl.managers.tab.TabManager;
import app.mcsl.managers.theme.ThemeColor;
import app.mcsl.managers.theme.ThemeManager;
import app.mcsl.managers.theme.ThemeType;
import app.mcsl.managers.version.FileUpdateStatusType;
import app.mcsl.managers.version.FileUpdater;
import app.mcsl.windows.Splash;
import app.mcsl.windows.Template;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.dialog.customdialogs.ChangelogDialog;
import app.mcsl.windows.elements.dialog.customdialogs.WelcomeDialog;
import app.mcsl.windows.elements.dialog.types.AlertDialog;
import app.mcsl.windows.elements.dialog.types.AlertType;
import com.dosse.upnp.UPnP;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author S3nS3IW00
 * @version 2.0
 */
public class MainClass extends Application {

    public static final String VERSION = "2.0";

    private static Template template;
    private static TabManager tabManager;
    private static FileManager fileManager;
    private static TrayManager trayManager;

    private File sessionFile;
    private FileChannel sessionChannel;
    private FileLock sessionLock;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> timedTasksTask;

    public static boolean SHOW_WELCOME = false;
    public static boolean SHOW_FILE_UPDATE = false;
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
            SHOW_FILE_UPDATE = true;
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
                Logger.error("Session is already in use! Check for the running application!");
                sessionChannel.close();
                System.exit(0);
                return;
            }
            Logger.info("Session is no longer in use. New session is created successfully!");

            Platform.runLater(new Thread(() -> {
                try {
                    fileManager = new FileManager();
                } catch (IOException e) {
                    Logger.error("Can't verify the necessary files for the following reason:");
                    Logger.exception(e);
                }

                Logger.info("Loading language...");
                Language.loadLanguage(getFileManager().getConfigProps().getProp("language"));

                Logger.info("Initiating application's stage...");
                template = new Template();

                Logger.info("Initiating and setting up tabs...");
                tabManager = new TabManager();
                template.setUpTabs();

                Logger.info("Adding system tray...");
                trayManager = new TrayManager();
                trayManager.runTray();

                getFileManager().loadServers();

                Logger.info("Applying theme...");
                ThemeManager.changeThemeColor(ThemeColor.valueOf(MainClass.getFileManager().getConfigProps().getProp("themecolor").toUpperCase()));
                ThemeManager.changeThemeType(ThemeType.valueOf(MainClass.getFileManager().getConfigProps().getProp("themetype").toUpperCase()));

                Logger.info("Initiating UPnP...");
                UPnP.waitInit();

                Logger.info("Starting timed tasks's timer...");
                timedTasksTask = scheduledExecutorService.scheduleAtFixedRate(new TimedTasksTimer(), 0, 1, TimeUnit.SECONDS);

                Logger.info("Verifying updates...");
                if (UpdateManager.needUpdate()) {
                    AlertDialog updateAlert = new AlertDialog(150, 400, Language.getText("update"), Language.getText("updateavailable"), AlertType.SUCCESS);
                    Button downloadButton = new Button(Language.getText("download"), ButtonType.APPLY);
                    updateAlert.addButton(downloadButton);
                    updateAlert.keepDefaultButton(true);
                    updateAlert.show();

                    Logger.warn("Update found!");
                }

                Logger.info("Showing application's stage...");
                template.show();
                splash.close();

                if (SHOW_WELCOME) new WelcomeDialog().show();
                if (!testedOpSys)
                    new AlertDialog(300, 400, Language.getText("warning"), Language.getText("nottestedopsys"), AlertType.WARNING).show();
                if (SHOW_UPDATED) new ChangelogDialog().show();
                if (SHOW_FILE_UPDATE)
                    new AlertDialog(250, 400, Language.getText("fileupdate"), Language.getText(fileUpdateStatusType.getLangCode()), fileUpdateStatusType.getAlertType()).show();
                if (fileUpdateStatusType == FileUpdateStatusType.RECREATED_WITH_COPY)
                    getFileManager().deleteFile(new File(getFileManager().getRoot() + "_old"));

                Logger.info("Startup done! (" + Logger.getWarnCount() + " warning, " + Logger.getErrorCount() + " error, " + Logger.getExceptionCount() + " exception)");
            }));
        }
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static Template getTemplate() {
        return template;
    }

    public static TabManager getTabManager() {
        return tabManager;
    }

    public static TrayManager getTrayManager() {
        return trayManager;
    }
}