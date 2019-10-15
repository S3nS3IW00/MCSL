package app.mcsl.manager.version;

import app.mcsl.MainClass;
import app.mcsl.manager.file.PropertiesManager;
import app.mcsl.manager.mainside.OSManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileUpdater {

    private File root;

    private String fileVersion;
    private File configFile;
    private PropertiesManager configProps;

    private File serversFolder, serverFilesFolder;

    public FileUpdater() {
        root = OSManager.getRoot();

        serversFolder = new File(root + File.separator + "servers");
        serverFilesFolder = new File(root + File.separator + "serverfiles");

        configFile = new File(root + File.separator + "config.properties");

        if (root.exists() && configFile.exists()) {
            configProps = new PropertiesManager(configFile);
            if (configProps.hasProp("version")) {
                fileVersion = configProps.getProp("version");
            }
        }
    }

    public FileUpdateStatusType updateFiles() throws IOException {
        if (!root.exists() || root.listFiles().length == 0 || (root.listFiles().length == 1 && root.listFiles()[0].isDirectory() && root.listFiles()[0].getName().equalsIgnoreCase("logs"))) {
            return FileUpdateStatusType.NOTHING;
        }
        if (fileVersion == null) {

            File oldFolder = new File(root + "_old");
            File oldServersFolder = new File(oldFolder + File.separator + "servers");
            File oldServerFilesFolder = new File(oldFolder + File.separator + "serverfiles");

            if (!root.renameTo(oldFolder)) {
                return FileUpdateStatusType.CANNOT_UPDATE;
            }

            if (!root.exists() && !root.mkdirs()) {
                return FileUpdateStatusType.CANNOT_UPDATE;
            }

            if (oldServersFolder.exists()) {
                if (!serversFolder.exists()) serversFolder.mkdirs();
                for (File server : oldServersFolder.listFiles()) {
                    if (server != null && server.isDirectory()) {
                        FileUtils.copyDirectory(server, new File(serversFolder + File.separator + server.getName()));
                    }
                }
            }

            if (oldServerFilesFolder.exists()) {
                if (serverFilesFolder.exists()) serverFilesFolder.mkdirs();
                FileUtils.copyDirectory(oldServerFilesFolder, serverFilesFolder);
            }

            return FileUpdateStatusType.RECREATED_WITH_COPY;
        }

        if (MainClass.VERSION.equalsIgnoreCase(fileVersion)) {
            return FileUpdateStatusType.NOTHING;
        }

        //Process to update still nothing
        switch (fileVersion) {
            case "":
                //something
                break;
        }
        return FileUpdateStatusType.UPDATED;
    }

}
