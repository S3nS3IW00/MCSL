package app.mcsl.managers.file;

import app.mcsl.managers.logging.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class PropertiesManager {

    Properties properties;
    File file;
    InputStream inputStream;

    public PropertiesManager() {
        properties = new Properties();
    }

    public PropertiesManager(File file) {
        this.file = file;
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file.getAbsolutePath())) {
            properties.load(new InputStreamReader(fis, Charset.forName("UTF-8")));
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    public PropertiesManager(InputStream inputStream) {
        this.inputStream = inputStream;
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    public void setProp(String key, String value) {
        try (FileOutputStream fos = new FileOutputStream(file.getAbsolutePath())) {
            properties.setProperty(key, value);
            properties.store(fos, null);
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    public void setBoolProp(String key, boolean value) {
        try (FileOutputStream fos = new FileOutputStream(file.getAbsolutePath())) {
            properties.setProperty(key, (value ? "true" : "false"));
            properties.store(fos, null);
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    public String getProp(String key) {
        String value = "";
        value = properties.getProperty(key);
        return value;
    }

    public Integer getIntegerProp(String key) {
        try {
            return Integer.parseInt(getProp(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean getBoolProp(String key) {
        return getProp(key).equalsIgnoreCase("true");
    }

    public void removeProp(String key) {
        try (FileOutputStream fos = new FileOutputStream(file.getAbsolutePath())) {
            properties.remove(key);
            properties.store(fos, null);
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    public boolean hasProp(String key) {
        return properties.containsKey(key);
    }

    public void loadFile(File file) {
        properties.clear();
        try {
            properties.load(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), Charset.forName("UTF-8")));
        } catch (IOException e) {
            Logger.exception(e);
        }
    }

    public File getFile() {
        return file;
    }

    public List<Object> getKeys() {
        return new ArrayList<>(properties.keySet());
    }

    public List<Object> getValues() {
        return new ArrayList<>(properties.values());
    }

    public void close() {
        properties.clear();
    }

    public InputStream getFileInputStream() {
        return inputStream;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < getKeys().size(); i++) {
            map.put(getKeys().get(i).toString(), getValues().get(i).toString());
        }
        return map;
    }
}
