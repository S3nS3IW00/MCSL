package app.mcsl.manager.file;

import app.mcsl.MainClass;
import app.mcsl.event.DirectoryChangeEvent;
import app.mcsl.manager.Language;
import app.mcsl.manager.logging.Logger;
import app.mcsl.manager.mainside.OSManager;
import app.mcsl.manager.mainside.timedtask.TimedTask;
import app.mcsl.manager.mainside.timedtask.TimedTasksManager;
import app.mcsl.manager.server.ServerAction;
import app.mcsl.manager.theme.ThemeColor;
import app.mcsl.manager.theme.ThemeType;
import app.mcsl.util.DataTypeUtil;
import app.mcsl.util.DateTimeUtils;
import app.mcsl.util.EnumUtil;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.content.server.ServerType;
import app.mcsl.window.content.server.type.external.ExternalServer;
import app.mcsl.window.content.server.type.local.LocalServer;
import app.mcsl.window.element.dialog.customdialog.ImportServerDialog;
import app.mcsl.window.element.dialog.customdialog.RepairServerDialog;
import app.mcsl.window.element.dialog.type.AlertDialog;
import app.mcsl.window.element.dialog.type.AlertType;
import app.mcsl.window.element.dialog.type.ConfirmationDialog;
import app.mcsl.window.element.notification.Notification;
import app.mcsl.window.element.notification.NotificationAlertType;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {

    //Fields
    private static File root;
    private static PropertiesManager configProps;
    private static JsonManager timedTasksJson, notificationsJson, locationsJson;

    //Images
    public static Image MENU_ICON = new Image("app/mcsl/resource/arrow_icon.png");
    public static Image SETTINGS_ICON = new Image("app/mcsl/resource/settings_icon.png");
    public static Image NOTIFICATION_ICON = new Image("app/mcsl/resource/notification_icon.png");
    public static Image MCSL_IMAGE = new Image("app/mcsl/resource/MinecraftServerLauncher.png", 500, 30, false, true);
    public static Image FILE_ICON = new Image("app/mcsl/resource/file_icon.png", 50, 50, false, true);
    public static Image FILE_ICON_20 = new Image("app/mcsl/resource/file_icon.png", 20, 20, false, true);
    public static Image FILE_ICON_100 = new Image("app/mcsl/resource/file_icon.png", 100, 100, false, true);
    public static Image SERVER_ICON = new Image("app/mcsl/resource/server_icon.png");
    public static Image EXTERNAL_SERVER_ICON = new Image("app/mcsl/resource/connect_icon.png", 20, 20, false, true);
    public static Image HOUSE_ICON = new Image("app/mcsl/resource/house_icon.png");
    public static Image CHOOSE_ICON = new Image("app/mcsl/resource/choose_icon_mini.png");
    public static Image RENAME_ICON = new Image("app/mcsl/resource/rename_icon_mini.png");
    public static Image DELETE_ICON = new Image("app/mcsl/resource/delete_icon_mini.png");
    public static Image START_ICON = new Image("app/mcsl/resource/start_icon_mini.png");
    public static Image STOP_ICON = new Image("app/mcsl/resource/stop_icon_mini.png");
    public static Image STEVE_ICON = new Image("app/mcsl/resource/steve_icon.jpg", 18, 18, false, true);
    public static Image ERROR_ICON = new Image("app/mcsl/resource/error_icon.png", 50, 50, false, true);
    public static Image INFO_ICON = new Image("app/mcsl/resource/information_icon.png", 50, 50, false, true);
    public static Image WARNING_ICON = new Image("app/mcsl/resource/warning_icon.png", 50, 50, false, true);
    public static Image SUCCESS_ICON = new Image("app/mcsl/resource/success_icon.jpg", 50, 50, false, true);
    public static Image EXCEPTION_ICON = new Image("app/mcsl/resource/exception_icon.png", 50, 50, false, true);
    public static Image LOG_ICON = new Image("app/mcsl/resource/log_icon.png", 50, 50, false, true);
    public static Image ERROR_ICON_20 = new Image("app/mcsl/resource/error_icon.png", 20, 20, false, true);
    public static Image INFO_ICON_20 = new Image("app/mcsl/resource/information_icon.png", 20, 20, false, true);
    public static Image WARNING_ICON_20 = new Image("app/mcsl/resource/warning_icon.png", 20, 20, false, true);
    public static Image SUCCESS_ICON_20 = new Image("app/mcsl/resource/success_icon.jpg", 20, 20, false, true);
    public static Image EXCEPTION_ICON_20 = new Image("app/mcsl/resource/exception_icon.png", 20, 20, false, true);
    public static Image LOG_ICON_20 = new Image("app/mcsl/resource/log_icon.png", 20, 20, false, true);
    public static Image TIP_ICON = new Image("app/mcsl/resource/info_icon.png", 50, 50, false, true);

    public static Image FACEBOOK_ICON_20 = new Image("app/mcsl/resource/facebook_icon.png", 20, 20, false, true);
    public static Image GITHUB_ICON_20 = new Image("app/mcsl/resource/github_icon.png", 20, 20, false, true);
    public static Image WEBSITE_ICON_20 = new Image("app/mcsl/resource/website_icon.png", 20, 20, false, true);
    public static Image REPORT_ICON_20 = new Image("app/mcsl/resource/report_icon.png", 20, 20, false, true);

    //Folders
    private static File logsFolder, imagesFolder, serversFolder, serverFilesFolder;

    //Files
    private static File configFile, timedtasksFile, notificationsFile, locationsJsonFile;

    public static void checkFiles() throws IOException {
        root = OSManager.getRoot();

        //Folders
        logsFolder = new File(root + File.separator + "logs");
        imagesFolder = new File(root + File.separator + "images");
        serversFolder = new File(root + File.separator + "servers");
        serverFilesFolder = new File(root + File.separator + "serverfiles");

        //Files
        configFile = new File(root + File.separator + "config.properties");
        timedtasksFile = new File(root + File.separator + "timedtasks.json");
        notificationsFile = new File(root + File.separator + "notifications.json");
        locationsJsonFile = new File(root + File.separator + "servers" + File.separator + "locations.json");

        Logger.info("Verifying the existence of the required files...");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
            Logger.info("Folder created: images");
        }

        if (!serversFolder.exists()) {
            serversFolder.mkdirs();
            Logger.info("Folder created: servers");
        }

        if (!serverFilesFolder.exists()) {
            serverFilesFolder.mkdirs();
            Logger.info("Folder created: serverfiles");
        }

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
            MainClass.SHOW_WELCOME = true;
            Logger.info("File created: config.properties");
        }

        if (!timedtasksFile.exists()) {
            timedtasksFile.getParentFile().mkdirs();
            timedtasksFile.createNewFile();
            Logger.info("File created: timedtasks.json");
        }

        if (!notificationsFile.exists()) {
            notificationsFile.getParentFile().mkdirs();
            notificationsFile.createNewFile();
            Logger.info("File created: notifications.json");
        }

        if (!locationsJsonFile.exists()) {
            locationsJsonFile.getParentFile().mkdirs();
            locationsJsonFile.createNewFile();
            Logger.info("File created: servers/locations.json");
        }

        Logger.info("Verifying file contents...");
        configProps = new PropertiesManager(configFile);
        if (!configProps.hasProp("language") || !getFileNamesInJarPath("app/mcsl/resource/language").contains(configProps.getProp("language") + ".properties")) {
            configProps.setProp("language", (Locale.getDefault().getLanguage().equalsIgnoreCase("hu") || Locale.getDefault().getLanguage().equalsIgnoreCase("en") ? Locale.getDefault().getLanguage() : "en"));
        }
        if (!configProps.hasProp("version")) {
            configProps.setProp("version", MainClass.VERSION);
        } else {
            if (!configProps.getProp("version").equalsIgnoreCase(MainClass.VERSION)) {
                MainClass.SHOW_UPDATED = true;
                configProps.setProp("version", MainClass.VERSION);
            }
        }
        if (!configProps.hasProp("notifications")) {
            configProps.setProp("notifications", true);
        }
        if (!configProps.hasProp("hideonexit")) {
            configProps.setProp("hideonexit", true);
        }
        if (!configProps.hasProp("licence")) {
            configProps.setProp("licence", false);
        }
        if (!configProps.hasProp("debug")) {
            configProps.setProp("debug", false);
        }
        if (!configProps.hasProp("autoupdate")) {
            configProps.setProp("autoupdate", true);
        }
        if (!configProps.hasProp("themecolor") || !EnumUtil.isInEnum(configProps.getProp("themecolor"), ThemeColor.class)) {
            configProps.setProp("themecolor", "default");
        }
        if (!configProps.hasProp("themetype") || !EnumUtil.isInEnum(configProps.getProp("themetype"), ThemeType.class)) {
            configProps.setProp("themetype", "light");
        }
        if (!configProps.hasProp("fancyfont")) {
            configProps.setProp("fancyfont", false);
        }

        Logger.info("Loading timed tasks...");
        timedTasksJson = new JsonManager(timedtasksFile);
        loadTimedTasks();

        Logger.info("Loading notifications...");
        notificationsJson = new JsonManager(notificationsFile);

        locationsJson = new JsonManager(locationsJsonFile);
    }

    public static void loadServers() {
        File[] files = serversFolder.listFiles();
        for (File serverFolder : files) {
            if (serverFolder != null && serverFolder.isDirectory()) {
                Logger.info("Loading server '" + serverFolder.getName() + "'...");
                File settingsProps = new File(serverFolder + File.separator + "settings.properties");
                PropertiesManager settingsPropsManager = new PropertiesManager(settingsProps);
                Server server;
                switch (ServerType.valueOf(settingsPropsManager.getProp("type").toUpperCase())) {
                    case LOCAL:
                        server = new LocalServer(serverFolder.getName());
                        break;
                    case EXTERNAL:
                        server = new ExternalServer(serverFolder.getName());
                        break;
                    default:
                        server = null;
                }
                ServerAction.add(server);
                if (settingsPropsManager.hasProp("autostart") && settingsPropsManager.getBoolProp("autostart"))
                    server.start();
            }
        }
        if (locationsJson.getDefaults().size() > 0) {
            for (Object o : locationsJson.getDefaults().keySet()) {
                String name = o.toString();
                Logger.info("Loading server '" + name + "'...");
                JSONObject locationObject = (JSONObject) locationsJson.getDefaults().get(name);
                File directory = new File(locationObject.get("location").toString());
                if (directory.exists()) {
                    File settingsProps = new File(directory + File.separator + "settings.properties");
                    if (settingsProps.exists()) {
                        PropertiesManager settingsPropsManager = new PropertiesManager(settingsProps);
                        Server server;
                        switch (ServerType.valueOf(settingsPropsManager.getProp("type").toUpperCase())) {
                            case LOCAL:
                                server = new LocalServer(name);
                                break;
                            case EXTERNAL:
                                server = new ExternalServer(name);
                                break;
                            default:
                                server = null;
                        }
                        ServerAction.add(server);
                        if (settingsPropsManager.hasProp("autostart") && settingsPropsManager.getBoolProp("autostart"))
                            server.start();
                    } else {
                        locationsJson.getDefaults().remove(name);
                        locationsJson.save();
                        Logger.warn("Server with name '" + name + "' not contains settings file! Server removed.");
                    }
                } else {
                    locationsJson.getDefaults().remove(name);
                    locationsJson.save();
                    Logger.warn("Server with name '" + name + "' is not exists! Server removed.");
                }
            }
        }
    }

    public static boolean checkServerFiles(String serverName) {
        Logger.info("Verifying files for server '" + serverName + "'...");

        File settingsProps = new File(getServerFolder(serverName) + File.separator + "settings.properties");
        File serverProps = new File(getServerFolder(serverName) + File.separator + "server.properties");

        if (!settingsProps.exists()) return false;
        PropertiesManager settings = new PropertiesManager(settingsProps), serverSettings;
        if (!settings.hasProp("type")) return false;
        if (settings.getProp("type").equalsIgnoreCase("local")) {
            if (!serverProps.exists()) return false;
            serverSettings = new PropertiesManager(serverProps);
            if (!settings.hasProp("ram") || !settings.hasProp("autostart") || !settings.hasProp("serverfile") ||
                    !(serverSettings.hasProp("server-port") && DataTypeUtil.isInt(serverSettings.getProp("server-port"))))
                return false;
        }
        if (settings.getProp("type").equalsIgnoreCase("external") &&
                (!settings.hasProp("username") || !settings.hasProp("password") ||
                        !(settings.hasProp("port") && DataTypeUtil.isInt(settings.getProp("port"))) || !settings.hasProp("address") ||
                        (!settings.hasProp("pluginport") && DataTypeUtil.isInt(settings.getProp("pluginport")))))
            return false;
        return true;
    }

    public static boolean checkImportServerFiles(File directory) {
        Logger.info("Verifying import server's files in directory '" + directory + "'...");

        File settingsProps = new File(directory + File.separator + "settings.properties");
        File serverProps = new File(directory + File.separator + "server.properties");

        if (!settingsProps.exists()) {
            try {
                settingsProps.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        if (!serverProps.exists()) {
            try {
                serverProps.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        new ImportServerDialog(directory.getAbsolutePath(), new PropertiesManager(settingsProps), new PropertiesManager(serverProps)).show();
        return true;
    }

    public static void repairServerFiles(String serverName) {
        Logger.info("Preparing for repair server '" + serverName + "'...");

        File settingsProps = new File(getServerFolder(serverName) + File.separator + "settings.properties");
        File serverProps = new File(getServerFolder(serverName) + File.separator + "server.properties");

        if (!settingsProps.exists()) {
            try {
                settingsProps.createNewFile();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }
        if (!serverProps.exists()) {
            try {
                serverProps.createNewFile();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }
        new RepairServerDialog(serverName, new PropertiesManager(settingsProps), new PropertiesManager(serverProps)).show();
    }

    public static File getServerFolder(String name) {
        if (new File(serversFolder + File.separator + name).exists() && new File(serversFolder + File.separator + name).isDirectory()) {
            return new File(serversFolder + File.separator + name);
        } else if (locationsJson.getDefaults().get(name) != null) {
            return new File(((JSONObject) locationsJson.getDefaults().get(name)).get("location").toString());
        }
        return null;
    }

    public static File getServerFolder(Server server) {
        if (new File(serversFolder + File.separator + server.getName()).exists() && new File(serversFolder + File.separator + server.getName()).isDirectory()) {
            return new File(serversFolder + File.separator + server.getName());
        } else if (locationsJson.getDefaults().get(server.getName()) != null) {
            return new File(((JSONObject) locationsJson.getDefaults().get(server.getName())).get("location").toString());
        }
        return null;
    }

    public static boolean isExternalServerRoot(String name) {
        return !new File(serversFolder + File.separator + name).exists() && locationsJson.getDefaults().containsKey(name);
    }

    public static boolean isExternalServerRoot(Server server) {
        if (!new File(serversFolder + File.separator + server.getName()).exists() && locationsJson.getDefaults().get(server.getName()) != null) {
            return true;
        }
        return false;
    }

    public static File getServerResource(Server server, ResourceType type) {
        return new File(getServerFolder(server.getName()) + File.separator + type.getFileName());
    }

    public static File getServerPluginsFolder(String serverName) {
        return new File(getServerFolder(serverName) + File.separator + "plugins");
    }

    public static File getServerFile(String name) {
        return new File(serverFilesFolder + File.separator + name);
    }

    public static Server createServer(String name, ServerType type, String[] settings, String customLocation) throws IOException {
        Logger.info("Creating server with name '" + name + "'...");

        File folder, settingsProps, serverProps, eula;
        if (customLocation == null) {
            folder = new File(serversFolder + File.separator + name);
            settingsProps = new File(folder + File.separator + "settings.properties");
            serverProps = new File(folder + File.separator + "server.properties");
            eula = new File(folder + File.separator + "eula.txt");
        } else {
            folder = new File(customLocation);
            settingsProps = new File(folder + File.separator + "settings.properties");
            serverProps = new File(folder + File.separator + "server.properties");
            eula = new File(folder + File.separator + "eula.txt");

            JSONObject locationObject = new JSONObject();
            locationObject.put("location", customLocation);
            locationsJson.addRawData(name, locationObject);
            locationsJson.save();
        }

        folder.mkdir();
        settingsProps.createNewFile();
        PropertiesManager settingsPropsManager = new PropertiesManager(settingsProps);

        switch (type) {
            case LOCAL:
                eula.createNewFile();
                FileWriter fileWriter = new FileWriter(eula);
                fileWriter.write("eula=true");
                fileWriter.flush();
                fileWriter.close();

                serverProps.createNewFile();
                PropertiesManager serverPropsManager = new PropertiesManager(serverProps);
                serverPropsManager.setProp("server-port", settings[0]);

                settingsPropsManager.setProp("type", type.name().toLowerCase());
                settingsPropsManager.setProp("serverfile", settings[1]);
                settingsPropsManager.setProp("ram", settings[2]);
                settingsPropsManager.setProp("autostart", Boolean.parseBoolean(settings[2]));
                return new LocalServer(name);
            case EXTERNAL:
                settingsPropsManager.setProp("type", type.name().toLowerCase());
                settingsPropsManager.setProp("address", settings[0]);
                settingsPropsManager.setProp("port", settings[1]);
                settingsPropsManager.setProp("pluginport", settings[2]);
                settingsPropsManager.setProp("username", settings[3]);
                settingsPropsManager.setProp("password", settings[4]);
                return new ExternalServer(name);
        }
        return null;
    }

    public static void importServer(String name, String location) {
        Logger.info("Importing server with name '" + name + "' from '" + location + "'...");

        JSONObject locationObject = new JSONObject();
        locationObject.put("location", location);
        locationsJson.addRawData(name, locationObject);
        locationsJson.save();

        ServerAction.add(new LocalServer(name));
    }

    public static void deleteFile(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            File[] arrayOfFile1;
            int j = (arrayOfFile1 = contents).length;
            for (int i = 0; i < j; i++) {
                deleteFile(arrayOfFile1[i]);
            }
        }
        if (!file.delete()) {
            Logger.error("Cannot delete file '" + file.getAbsolutePath() + "'! Maybe it is busy?");
            new AlertDialog(200, 400, Language.getText("error"), Language.getText("cantdeletefile", file.getName()), AlertType.ERROR).show();
        }
    }

    public static boolean renameServer(String serverName, File serverFolder, String newName) {
        Logger.info("Renaming server '" + serverName + "' to '" + newName + "'...");

        if (isExternalServerRoot(serverName)) {
            JSONObject locationObject = (JSONObject) locationsJson.getDefaults().get(serverName);
            locationsJson.getDefaults().remove(serverName);
            locationsJson.addRawData(newName, locationObject);
            locationsJson.save();
            return true;
        } else {
            return serverFolder.renameTo(new File(serverFolder.getParentFile() + File.separator + newName));
        }
    }

    public static void deleteServer(String serverName) {
        Logger.info("Deleting server '" + serverName + "'...");

        File serverFolder = getServerFolder(serverName);

        if (!isExternalServerRoot(serverName)) {
            deleteFile(serverFolder);
        } else {
            locationsJson.getDefaults().remove(serverName);
            locationsJson.save();
            new ConfirmationDialog(200, 500, Language.getText("deleteserver"), Language.getText("customlocationdeletequestion")) {
                @Override
                public void yesAction() {
                    deleteFile(serverFolder);
                    close();
                }

                @Override
                public void noAction() {
                    close();
                }
            }.show();
        }
    }

    public static Image decodeBase64ToImage(String base64) throws IOException {
        String[] parts = base64.split(",");
        String imageString = parts[1];

        BufferedImage image;
        byte[] imageByte;

        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(imageString);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

        return (image != null ? SwingFXUtils.toFXImage(image, null) : null);
    }

    public static String getFileExtension(File file) {
        String extension = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }

        return extension;
    }

    public static void addPlugin(String serverName, File plugin) throws IOException {
        Logger.info("Copying plugin file from '" + plugin.getAbsolutePath() + "' to server '" + serverName + "'...");

        File pluginsFolder = getServerPluginsFolder(serverName);
        if (!pluginsFolder.exists()) pluginsFolder.mkdirs();
        Files.copy(plugin.toPath(), new File(pluginsFolder + File.separator + plugin.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void addServerfile(File serverFile) throws IOException {
        Logger.info("Copying server file from '" + serverFile.getAbsolutePath() + "'...");

        File serverFilesFolder = getServerFilesFolder();
        if (!serverFilesFolder.exists()) serverFilesFolder.mkdirs();
        Files.copy(serverFile.toPath(), new File(serverFilesFolder + File.separator + serverFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        DirectoryChangeEvent.change(DirectoryType.SERVERFILE);
    }

    //Notifications
    public static void addNotification(Notification notification) {
        Logger.info("Adding notification...");

        JSONObject notifObject = new JSONObject();
        notifObject.put("title", notification.getTitle());
        notifObject.put("text", notification.getNotifText());
        notifObject.put("date", notification.getDate());
        notifObject.put("type", notification.getType().name());

        notificationsJson.addRawData((getNotificationCount() + 1) + "", notifObject);
        notificationsJson.save();
    }

    private static Notification parseNotificaion(JSONObject notifObject) {
        return new Notification(notifObject.get("title").toString(), notifObject.get("text").toString(), notifObject.get("date").toString(), NotificationAlertType.valueOf(notifObject.get("type").toString()));
    }

    public static Notification[] getLatestNotifications(int count) {
        if (count > getNotificationCount()) count = getNotificationCount();
        Notification[] notifications = new Notification[count];
        int maxIndex = notificationsJson.getDefaults().size();
        for (int i = 0; i < count; i++) {
            JSONObject notifObject = (JSONObject) notificationsJson.getDefaults().get((maxIndex - (count - i) + 1) + "");
            notifications[i] = parseNotificaion(notifObject);
        }
        return notifications;
    }

    public static int getNotificationCount() {
        return notificationsJson.getDefaults().size();
    }

    //Timed tasks
    public static void addTimedTask(String name, String date, String server, String command, boolean isDaily) {
        Logger.info("Adding timed task with name '" + name + "' to server '" + server + "'...");

        JSONObject task = new JSONObject();
        task.put("name", name);
        task.put("date", (isDaily ? (date.split(" ").length > 1 ? date.split(" ")[1] : date) : date));
        task.put("command", command);
        task.put("isDaily", isDaily);

        if (!timedTasksJson.getDefaults().containsKey(server))
            timedTasksJson.getDefaults().put(server, new JSONArray());
        ((JSONArray) timedTasksJson.getDefaults().get(server)).add(task);
        timedTasksJson.save();
    }

    public static void removeTimedTask(String server, String name) {
        Logger.info("Removing timed task with name '" + name + "' from server '" + server + "'...");

        JSONArray serverArray = (JSONArray) timedTasksJson.getDefaults().get(server);
        JSONObject deleteObject = null;
        for (Object o : serverArray) {
            JSONObject taskObject = (JSONObject) o;
            if (taskObject.get("name").toString().equalsIgnoreCase(name)) {
                deleteObject = taskObject;
            }
        }
        if (deleteObject != null) serverArray.remove(deleteObject);
        if (serverArray.size() == 0)
            timedTasksJson.getDefaults().remove(server);
        timedTasksJson.save();
    }

    private static void loadTimedTasks() {
        if (timedTasksJson.getDefaults().size() == 0) return;
        for (String serverName : timedTasksJson.getDefaults().keySet()) {
            JSONArray serverArray = (JSONArray) timedTasksJson.getDefaults().get(serverName);
            for (Object o : serverArray) {
                JSONObject timedTaskObject = (JSONObject) o;
                String name = timedTaskObject.get("name").toString();
                String date = timedTaskObject.get("date").toString();
                String command = timedTaskObject.get("command").toString();
                boolean isDaily = Boolean.parseBoolean(timedTaskObject.get("isDaily").toString());

                if (!isDaily && (DateTimeUtils.lateDate(date) || DateTimeUtils.lateTime(date))) {
                    removeTimedTask(serverName, name);
                } else {
                    TimedTasksManager.addTimedTask(new TimedTask(name, serverName, date, command, isDaily));
                }
            }
        }
    }

    public static List<String> getFileNamesInJarPath(String jarPath) {
        List<String> fileNames = new ArrayList<>();
        CodeSource src = MainClass.class.getProtectionDomain().getCodeSource();
        try {
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                while (true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null)
                        break;
                    String name = e.getName();
                    if (name.startsWith(jarPath)) {
                        String substringedName = name.substring(name.lastIndexOf("/") + 1);
                        if (!substringedName.equalsIgnoreCase("")) {
                            fileNames.add(substringedName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Logger.exception(e);
        }
        return fileNames;
    }

    public static InputStream getInputStreamInExternalJar(File jarFile, String jarPath) {
        InputStream in = null;
        String inputFile = "jar:file:/" + jarFile.getAbsolutePath() + "!/" + jarPath;
        URL inputURL;
        if (inputFile.startsWith("jar:")) {
            try {
                inputURL = new URL(inputFile);
                JarURLConnection conn = (JarURLConnection) inputURL.openConnection();
                in = conn.getInputStream();
            } catch (IOException e1) {
                //empty catch block
            }
        }
        return in;
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file != null) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += folderSize(file);
                }
            }
        }
        return length;
    }

    public static File saveImage(Image image, String fileName) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", new File(imagesFolder + File.separator + fileName + ".png"));
        } catch (IOException e) {
            return null;
        }
        return new File(imagesFolder + File.separator + fileName + ".png");
    }

    public static Image screenShot(Scene scene) {
        WritableImage img = new WritableImage((int) scene.getWidth(), (int) scene.getHeight());
        scene.snapshot(img);
        return img;
    }

    //Getters

    public static PropertiesManager getConfigProps() {
        return configProps;
    }

    public static File getServerFilesFolder() {
        return serverFilesFolder;
    }

    public static File getServersFolder() {
        return serversFolder;
    }

    public static File getRoot() {
        return root;
    }
}
