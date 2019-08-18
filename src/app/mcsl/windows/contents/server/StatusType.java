package app.mcsl.windows.contents.server;

public enum StatusType {

    PREPARING("preparing-label", "preparing", "-fx-warning"),
    RUNNING("running-label", "running", "-fx-apply"),
    STOPPED("stopped-label", "stopped", "-fx-error"),
    STARTING("starting-label", "starting", "-fx-warning"),
    STOPPING("stopping-label", "stopping", "-fx-warning"),

    CONNECTING("starting-label", "connecting", "-fx-warning"),
    CONNECTED("running-label", "connected", "-fx-apply");

    String id, text, color;
    StatusType(String id, String text, String color) {
        this.id = id;
        this.text = text;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }
}
