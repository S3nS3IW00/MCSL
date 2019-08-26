package app.mcsl.managers.theme;

public enum ThemeColor {

    DEFAULT("default", "#3498db", "#2980b9"),
    ORANGE("orange", "#e67e22", "#d35400"),
    PURPLE("purple", "#9b59b6", "#8e44ad"),
    GREEN("green", "#6ab04c", "#009432"),
    RED("red", "#e74c3c", "#c0392b");

    String defColor, darkDefColor, displayName;

    ThemeColor(String displayName, String defColor, String darkDefColor) {
        this.displayName = displayName;
        this.defColor = defColor;
        this.darkDefColor = darkDefColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefColor() {
        return defColor;
    }

    public String getDarkDefColor() {
        return darkDefColor;
    }
}
