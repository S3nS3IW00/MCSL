package app.mcsl.managers.theme;

import javafx.scene.text.Font;

public enum FontType {

    DEFAULT(Font.getDefault().getName()),
    FANCY("Titan One");

    private String fontName;

    FontType(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return fontName;
    }
}
