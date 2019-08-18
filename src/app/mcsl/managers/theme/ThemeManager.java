package app.mcsl.managers.theme;

import app.mcsl.MainClass;
import app.mcsl.managers.Language;
import javafx.scene.Scene;

public class ThemeManager {

    public static ThemeColor currentColor = ThemeColor.DEFAULT;
    public static ThemeType currentType = ThemeType.LIGHT;

    public static void changeThemeColor(ThemeColor color){
        currentColor = color;
        applyCss();
    }

    public static String[] displayColorValues(){
        String[] displayNames = new String[ThemeColor.values().length];
        for(int i = 0; i < ThemeColor.values().length; i++){
            displayNames[i] = Language.getText(ThemeColor.values()[i].displayName);
        }
        return displayNames;
    }

    public static ThemeColor getColorFromDisplayName(String s){
        for(ThemeColor type : ThemeColor.values()){
            if(Language.getText(type.displayName).equalsIgnoreCase(s)) return type;
        }
        return null;
    }

    public static void changeThemeType(ThemeType type){
        currentType = type;
        applyCss();
    }

    public static String[] displayTypeValues(){
        String[] displayNames = new String[ThemeType.values().length];
        for(int i = 0; i < ThemeType.values().length; i++){
            displayNames[i] = Language.getText(ThemeType.values()[i].displayName);
        }
        return displayNames;
    }

    public static ThemeType getTypeFromDisplayName(String s){
        for(ThemeType type : ThemeType.values()){
            if(Language.getText(type.displayName).equalsIgnoreCase(s)) return type;
        }
        return null;
    }

    private static void applyCss(){
        MainClass.getTemplate().getScene().getRoot().setStyle("-fx-defcolor: "+currentColor.getDefColor()+";" +
                "-fx-defdarkcolor: "+currentColor.getDarkDefColor()+";" +
                "-fx-themetypecolor: "+currentType.getType()+";" +
                "-fx-themetypcoloropacity: "+currentType.getOpacityType()+";" +
                "-fx-themetypetextcolor: "+currentType.getTextType()+";");
    }

    public static void applyCss(Scene scene){
        scene.getRoot().setStyle("-fx-defcolor: "+currentColor.getDefColor()+";" +
                "-fx-defdarkcolor: "+currentColor.getDarkDefColor()+";" +
                "-fx-themetypecolor: "+currentType.getType()+";" +
                "-fx-themetypcoloropacity: "+currentType.getOpacityType()+";" +
                "-fx-themetypetextcolor: "+currentType.getTextType()+";");
    }

}
