package app.mcsl.window.content.server;

import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import javafx.scene.image.Image;

public enum ServerType {
    LOCAL("local", FileManager.SERVER_ICON), EXTERNAL("external", FileManager.EXTERNAL_SERVER_ICON), BUNGEE("bungee", FileManager.PROXY_ICON);

    String typeName;
    Image icon;

    ServerType(String typeName, Image icon) {
        this.typeName = typeName;
        this.icon = icon;
    }

    public String getTypeName() {
        return typeName;
    }

    public Image getIcon() {
        return icon;
    }

    public static String[] displayValues() {
        String[] displayNames = new String[ServerType.values().length];
        for (int i = 0; i < ServerType.values().length; i++) {
            displayNames[i] = Language.getText(ServerType.values()[i].typeName);
        }
        return displayNames;
    }

    public static ServerType getByDisplayName(String s) {
        for (ServerType type : ServerType.values()) {
            if (Language.getText(type.typeName).equalsIgnoreCase(s)) return type;
        }
        return null;
    }
}
