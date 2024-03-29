package app.mcsl.manager;

import app.mcsl.MainClass;
import app.mcsl.manager.file.PropertiesManager;
import app.mcsl.manager.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class Language {

    private static PropertiesManager languageProps;
    private static Map<String, String> texts = new HashMap<>();

    public static String LANGUAGE;

    public static void loadLanguage(String language) {
        Logger.info("Loading language '" + language + "'...");
        Language.LANGUAGE = language;

        languageProps = new PropertiesManager(MainClass.class.getResourceAsStream("/app/mcsl/resource/language/" + language + ".properties"));
        for (Object key : languageProps.getKeys()) {
            texts.put(key.toString(), languageProps.getProp(key.toString()));
        }
    }

    public static String getText(String key) {
        return texts.get(key);
    }

    public static String getText(String key, Object... args) {
        String text = texts.get(key);
        for (int i = 0; i < args.length; i++) {
            text = text.replace("{" + i + "}", args[i].toString());
        }

        return text;
    }
}
