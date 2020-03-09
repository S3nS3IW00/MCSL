package app.mcsl.window.content.server.type.bungee.page;

import app.mcsl.event.DirectoryChangeEvent;
import app.mcsl.manager.Language;
import app.mcsl.manager.file.DirectoryType;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.file.PropertiesManager;
import app.mcsl.manager.file.ResourceType;
import app.mcsl.window.content.server.type.bungee.BungeeServer;
import app.mcsl.window.element.setting.Setting;
import app.mcsl.window.element.setting.SettingCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BungeeSettings extends ScrollPane {

    private BungeeServer server;

    private VBox body;
    private Map<Node, String> settingComponents = new HashMap<>();

    private Map<String, String> changed = new HashMap<>();

    private File settingsFile, serverPropsFile;
    private PropertiesManager settingsProps, serverProps;
    private Map<String, String> settings = new HashMap<>(), serverSettings = new HashMap<>();

    //settings fields
    private CheckBox autostartCheckBox;
    private ComboBox serverFileComboBox;
    private TextField serverPortTextField, serverRamTextField;

    public BungeeSettings(BungeeServer server) {
        this.server = server;

        serverPortTextField = new TextField();
        settingComponents.put(serverPortTextField, "server-port");
        Setting serverPortSetting = new Setting("Port", serverPortTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        serverFileComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(FileManager.getServerFilesFolder().list())));
        settingComponents.put(serverFileComboBox, "serverfile");
        Setting serverFileSetting = new Setting(Language.getText("serverfile"), serverFileComboBox, null, false) {
            @Override
            public void onChange(Object object) {
                if (FileManager.getServerFilesFolder().listFiles().length > 0 && serverFileComboBox.getSelectionModel().getSelectedItem() != null) {
                    changed.put("serverfile", serverFileComboBox.getSelectionModel().getSelectedItem().toString());
                } else {
                    settingsProps.removeProp("serverfile");
                }
            }
        };

        serverRamTextField = new TextField();
        settingComponents.put(serverRamTextField, "ram");
        Setting ramSetting = new Setting(Language.getText("maxram"), serverRamTextField, "MB", false) {
            @Override
            public void onChange(Object object) {

            }
        };

        autostartCheckBox = new CheckBox();
        settingComponents.put(autostartCheckBox, "autostart");
        Setting autostartSetting = new Setting(Language.getText("autostart"), autostartCheckBox, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        SettingCategory mcsl = new SettingCategory(Language.getText("launchersettings"));
        mcsl.addSetting(serverPortSetting, serverFileSetting, ramSetting, autostartSetting);

        body = new VBox(10, mcsl);

        setContent(body);
        setFitToWidth(true);

        loadSettings();
        registerSettings();
        changed.clear();

        DirectoryChangeEvent.addListener(type -> {
            if (type == DirectoryType.SERVERFILE) {
                serverFileComboBox.setItems(FXCollections.observableList(Arrays.asList(FileManager.getServerFilesFolder().list())));
                if (settings.get("serverfile") != null) {
                    serverFileComboBox.getSelectionModel().select(settings.get("serverfile"));
                } else {
                    settingsProps.removeProp("serverfile");
                }
            }
        });
    }

    public String getSetting(String key) {
        if (serverSettings.containsKey(key)) return serverSettings.get(key);
        return settings.get(key);
    }

    public void setSetting(String key, String value) {
        if (key.equalsIgnoreCase("ram") || key.equalsIgnoreCase("serverfile") || key.equalsIgnoreCase("autostart")) {
            settingsProps.setProp(key, value);
            settings.replace(key, value);
            return;
        }
        serverProps.setProp(key, value);
        serverSettings.replace(key, value);
    }

    public void save() {
        if (changed.size() == 0) return;
        for (String key : changed.keySet()) {
            setSetting(key, changed.get(key));
        }
        if (changed.containsKey("server-port") || changed.containsKey("max-players")) server.updateInfos();
        changed.clear();
    }

    public void loadSettings() {
        if (FileManager.getServerResource(server, ResourceType.SETTINGS_PROPERTIES).exists()) {
            settingsFile = FileManager.getServerResource(server, ResourceType.SETTINGS_PROPERTIES);
            settingsProps = new PropertiesManager(settingsFile);
            settings = settingsProps.toMap();
        }
        if (FileManager.getServerResource(server, ResourceType.SERVER_PROPERTIES).exists()) {
            serverPropsFile = FileManager.getServerResource(server, ResourceType.SERVER_PROPERTIES);
            serverProps = new PropertiesManager(serverPropsFile);
            serverSettings = serverProps.toMap();
        }

        for (Node component : settingComponents.keySet()) {
            String setting = getSetting(settingComponents.get(component));
            if (component instanceof CheckBox) {
                ((CheckBox) component).setSelected(setting != null && Boolean.parseBoolean(setting));
            } else if (component instanceof ComboBox) {
                if (setting == null) {
                    ((ComboBox) component).getSelectionModel().select(0);
                } else {
                    try {
                        ((ComboBox) component).getSelectionModel().select(Integer.parseInt(setting));
                    } catch (NumberFormatException e) {
                        ((ComboBox) component).getSelectionModel().select(setting);
                    }
                }
            } else if (component instanceof TextField) {
                ((TextField) component).setText(setting == null ? "" : setting);
            }
        }
        changed.clear();
    }

    public void closeSettings() {
        serverProps.close();
        settingsProps.close();

        serverProps = null;
        settingsProps = null;
    }

    private void registerSettings() {
        ObservableList<Node> settingCategories = body.getChildren();
        for (Node node : settingCategories) {
            if (node instanceof SettingCategory) {
                SettingCategory settingCategory = (SettingCategory) node;
                for (Setting setting : settingCategory.getSettings()) {
                    Node component = setting.getComponent();
                    if (component instanceof ComboBox) {
                        ((ComboBox) component).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                            setting.onChange(newValue);
                            if (setting.isNotifyChange()) {

                            }
                        });
                    } else if (component instanceof CheckBox) {
                        ((CheckBox) component).selectedProperty().addListener((observable, oldValue, newValue) -> {
                            setting.onChange(newValue);
                            if (settingComponents.containsKey(component))
                                changed.put(settingComponents.get(component), newValue.toString());
                            if (setting.isNotifyChange()) {

                            }
                        });
                    } else if (component instanceof TextField) {
                        ((TextField) component).textProperty().addListener((observable, oldValue, newValue) -> {
                            setting.onChange(newValue);
                            if (settingComponents.containsKey(component))
                                changed.put(settingComponents.get(component), newValue);
                            if (setting.isNotifyChange()) {

                            }
                        });
                    }
                }
            }
        }
    }

}
