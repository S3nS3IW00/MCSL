package app.mcsl.managers.theme;

public enum ThemeType {

    LIGHT("#ecf0f1", "rgba(236,240,241, 0.8)", "#222f3e", "light"),
    DARK("#222f3e", "rgba(34,47,62, 0.8)", "#ecf0f1", "dark");

    String type, opacityType, textType, displayName;

    ThemeType(String type, String opacityType, String textType, String displayName) {
        this.displayName = displayName;
        this.opacityType = opacityType;
        this.textType = textType;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOpacityType() {
        return opacityType;
    }

    public String getTextType() {
        return textType;
    }

    public String getType() {
        return type;
    }
}
