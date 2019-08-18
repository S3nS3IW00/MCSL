package app.mcsl.utils;

public class DataTypeUtil {

    public static boolean isInt(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

}
