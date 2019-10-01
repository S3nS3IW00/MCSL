package app.mcsl.managers.theme;

import app.mcsl.managers.Language;
import app.mcsl.managers.logging.Logger;
import app.mcsl.windows.Template;
import javafx.scene.Scene;

public class ThemeManager {

    private static ThemeColor currentColor = ThemeColor.DEFAULT;
    private static ThemeType currentType = ThemeType.LIGHT;
    private static FontType currentFontType = FontType.DEFAULT;

    public static void changeThemeColor(ThemeColor color) {
        Logger.info("Changing theme color to '" + color.getDisplayName() + "'...");
        currentColor = color;
        applyCss();
    }

    public static String[] displayColorValues() {
        String[] displayNames = new String[ThemeColor.values().length];
        for (int i = 0; i < ThemeColor.values().length; i++) {
            displayNames[i] = Language.getText(ThemeColor.values()[i].getDisplayName());
        }
        return displayNames;
    }

    public static ThemeColor getColorFromDisplayName(String s) {
        for (ThemeColor type : ThemeColor.values()) {
            if (Language.getText(type.getDisplayName()).equalsIgnoreCase(s)) return type;
        }
        return null;
    }

    public static void changeThemeType(ThemeType type) {
        Logger.info("Changing theme type to '" + type.getDisplayName() + "'...");
        currentType = type;
        applyCss();
    }

    public static String[] displayTypeValues() {
        String[] displayNames = new String[ThemeType.values().length];
        for (int i = 0; i < ThemeType.values().length; i++) {
            displayNames[i] = Language.getText(ThemeType.values()[i].getDisplayName());
        }
        return displayNames;
    }

    public static ThemeType getTypeFromDisplayName(String s) {
        for (ThemeType type : ThemeType.values()) {
            if (Language.getText(type.getDisplayName()).equalsIgnoreCase(s)) return type;
        }
        return null;
    }

    public static void changeFontType(FontType type) {
        Logger.info("Changing font type to '" + type.getFontName() + "'...");
        currentFontType = type;
        applyCss();
    }

    private static void applyCss() {
        Template.getStage().getScene().getRoot().setStyle("-fx-defcolor: " + currentColor.getDefColor() + ";" +
                "-fx-defdarkcolor: " + currentColor.getDarkDefColor() + ";" +
                "-fx-themetypecolor: " + currentType.getType() + ";" +
                "-fx-themetypcoloropacity: " + currentType.getOpacityType() + ";" +
                "-fx-themetypetextcolor: " + currentType.getTextType() + ";" +
                "-fx-font-family: '" + currentFontType.getFontName() + "';");
    }

    public static void applyCss(Scene scene) {
        scene.getRoot().setStyle("-fx-defcolor: " + currentColor.getDefColor() + ";" +
                "-fx-defdarkcolor: " + currentColor.getDarkDefColor() + ";" +
                "-fx-themetypecolor: " + currentType.getType() + ";" +
                "-fx-themetypcoloropacity: " + currentType.getOpacityType() + ";" +
                "-fx-themetypetextcolor: " + currentType.getTextType() + ";" +
                "-fx-font-family: '" + currentFontType.getFontName() + "';");
    }

}
