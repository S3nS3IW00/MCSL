package app.mcsl.windows.elements.coloredtextflow;

import javafx.scene.paint.Color;

public enum ChatColor {

    NONE("", Color.WHITE), BLACK("0", Color.BLACK), DARK_BLUE("1", Color.DARKBLUE), DARK_GREEN("2", Color.DARKGREEN), DARK_AQUA("3", Color.DARKCYAN),
    DARK_RED("4", Color.DARKRED), DARK_PURPLE("5", Color.PURPLE), GOLD("6", Color.GOLD), GRAY("7", Color.GRAY), DARK_GRAY("8", Color.DARKGRAY),
    BLUE("9", Color.BLUE), YELLOW("e", Color.YELLOW), GREEN("a", Color.GREEN), PURPLE("d", Color.MAGENTA),
    WHITE("f", Color.WHITE), RED("c", Color.RED), AQUA("b", Color.CYAN);

    String styleCode;
    Color color;

    ChatColor(String styleCode, Color color) {
        this.styleCode = styleCode;
        this.color = color;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public Color getColor() {
        return color;
    }

    public static ChatColor getColorByStyleCode(String styleCode) {
        for (ChatColor type : ChatColor.values()) {
            if (type.getStyleCode().equalsIgnoreCase(styleCode)) return type;
        }
        return ChatColor.WHITE;
    }
}
