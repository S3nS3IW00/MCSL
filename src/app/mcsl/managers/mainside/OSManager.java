package app.mcsl.managers.mainside;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class OSManager {

    public enum OS {
        WINDOWS,
        UNIX,
        MAC,
        OTHER;

        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    private static OS os = OS.OTHER;
    private static File root;

    static {
        try {
            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            }
            osName = osName.toLowerCase(Locale.ENGLISH);
            if (osName.contains("windows")) {
                os = OS.WINDOWS;
                root = new File(System.getenv("APPDATA") + File.separator + ".mcserverlauncher");
            } else if (osName.contains("linux")
                    || osName.contains("mpe/ix")
                    || osName.contains("freebsd")
                    || osName.contains("irix")
                    || osName.contains("digital unix")
                    || osName.contains("unix")) {
                os = OS.UNIX;
                root = new File(System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "mcserverlauncher");
            } else if (osName.contains("mac os")) {
                os = OS.MAC;
                root = new File(System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator + "mcserverlauncher");
            } else {
                os = OS.OTHER;
                root = new File(System.getenv("APPDATA") + File.separator + ".mcserverlauncher");
            }

        } catch (Exception ex) {
            os = OS.OTHER;
        } finally {
            os.setVersion(System.getProperty("os.version"));
        }
    }

    public static OS getOs() {
        return os;
    }

    public static File getRoot() {
        return root;
    }
}
