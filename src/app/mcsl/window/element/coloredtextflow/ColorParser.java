package app.mcsl.window.element.coloredtextflow;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ColorParser {

    private String text;
    private int fontSize;
    private ChatColor defaultColor, color;
    private boolean underlined = false, bold = false, italic = false, striketrough = false;

    public ColorParser(String text, int fontSize, ChatColor defaultColor) {
        this.text = text.replace("§", "&").replace("\u00A7", "&");
        this.fontSize = fontSize;
        this.defaultColor = defaultColor;
        this.color = defaultColor;
    }

    public Text[] parse() {
        Text[] texts = new Text[text.split("&").length];
        int index = 0;

        if (text.split("&").length == 1) {
            texts[index] = getStyledText(text);
            return texts;
        }
        if (text.split("&")[0].length() > 0) {
            texts[0] = getStyledText(text.split("&")[0]);
            index = 1;
        }
        for (int i = index; i < text.split("&").length; i++) {
            String s = text.split("&")[i];
            if (s.length() > 0) {
                String styleCode = s.charAt(0) + "";
                String sText = s.substring(1);
                if (isStyleCode(styleCode)) {
                    setStyleValue(styleCode);
                    if (sText.length() > 0) {
                        texts[index] = getStyledText(sText);
                        index++;
                    }
                } else {
                    texts[index] = getStyledText("&" + styleCode + sText);
                    index++;
                }
            }
        }
        return texts;
    }

    private boolean isStyleCode(String styleCode) {
        if (styleCode.equalsIgnoreCase("r") || styleCode.equalsIgnoreCase("o") ||
                styleCode.equalsIgnoreCase("l") || styleCode.equalsIgnoreCase("m") || styleCode.equalsIgnoreCase("n"))
            return true;
        for (ChatColor type : ChatColor.values()) {
            if (type.getStyleCode().equalsIgnoreCase(styleCode)) return true;
        }
        return false;
    }

    private ChatColor getColorFromStyleCode(String styleCode) {
        for (ChatColor type : ChatColor.values()) {
            if (type.getStyleCode().equalsIgnoreCase(styleCode)) return type;
        }
        return defaultColor;
    }

    private Text getStyledText(String s) {
        Text text = new Text(s);
        FontWeight fontWeight = FontWeight.NORMAL;
        FontPosture fontPosture = FontPosture.REGULAR;
        if (underlined) text.setUnderline(true);
        if (striketrough) text.setStrikethrough(true);
        if (bold) fontWeight = FontWeight.BOLD;
        if (italic) fontPosture = FontPosture.ITALIC;
        text.setFont(Font.font(null, fontWeight, fontPosture, fontSize));
        text.setFill(color.getColor());
        return text;
    }

    private void setStyleValue(String styleCode) {
        if (styleCode.equalsIgnoreCase("r")) {
            color = ChatColor.WHITE;
            bold = false;
            italic = false;
            underlined = false;
            striketrough = false;
        } else if (styleCode.equalsIgnoreCase("o")) {
            italic = true;
        } else if (styleCode.equalsIgnoreCase("l")) {
            bold = true;
        } else if (styleCode.equalsIgnoreCase("n")) {
            underlined = true;
        } else if (styleCode.equalsIgnoreCase("m")) {
            striketrough = true;
        } else {
            color = getColorFromStyleCode(styleCode);
        }
    }

}
