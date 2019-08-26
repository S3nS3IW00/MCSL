package app.mcsl.managers.file;

public enum ResourceType {

    SERVER_PROPERTIES("server.properties"), SETTINGS_PROPERTIES("settings.properties");

    String fileName;

    ResourceType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
