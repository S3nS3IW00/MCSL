package app.mcsl.windows.contents.server.type.external.pages;

import app.mcsl.managers.HashManager;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.file.PropertiesManager;
import app.mcsl.managers.file.ResourceType;
import app.mcsl.windows.contents.server.type.external.ExternalServer;
import app.mcsl.windows.elements.settings.Setting;
import app.mcsl.windows.elements.settings.SettingCategory;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ExternalSettings extends ScrollPane {

    private ExternalServer server;

    private VBox body;
    private Map<Node, String> settingComponents = new HashMap<>();

    private Map<String, String> changed = new HashMap<>();

    private File settingsFile;
    private PropertiesManager settingsProps;
    private Map<String, String> settings = new HashMap<>();

    //settings fields
    private TextField serverIpTextField, serverPortTextField, pluginPortTextField, usernameTextField;
    private PasswordField passwordTextField;
    private Setting passwordSetting;

    public ExternalSettings(ExternalServer server) {
        this.server = server;

        serverIpTextField = new TextField();
        settingComponents.put(serverIpTextField, "address");
        Setting serverIpSetting = new Setting(Language.getText("ipaddress"), serverIpTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        serverPortTextField = new TextField();
        settingComponents.put(serverPortTextField, "port");
        Setting serverPortSetting = new Setting("Port", serverPortTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        pluginPortTextField = new TextField();
        settingComponents.put(pluginPortTextField, "pluginport");
        Setting pluginPortSetting = new Setting("Plugin port", pluginPortTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        usernameTextField = new TextField();
        settingComponents.put(usernameTextField, "username");
        Setting usernameSetting = new Setting(Language.getText("pluginusername"), usernameTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        passwordTextField = new PasswordField();
        settingComponents.put(passwordTextField, "password");
        passwordSetting = new Setting(Language.getText("pluginpassword"), passwordTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        SettingCategory mcsl = new SettingCategory(Language.getText("launchersettings"));
        mcsl.addSetting(serverIpSetting, serverPortSetting, pluginPortSetting, usernameSetting, passwordSetting);

        body = new VBox(10, mcsl);

        setContent(body);
        setFitToWidth(true);

        registerSettings();
        loadSettings();
        changed.clear();
    }

    public String getSetting(String key) {
        return settings.get(key);
    }

    public void setSetting(String key, String value) {
        settingsProps.setProp(key, value);
        settings.replace(key, value);
    }

    public void save() {
        if (changed.size() == 0) return;
        for (String key : changed.keySet()) {
            if (key.equalsIgnoreCase("password")) {
                setSetting(key, HashManager.cuttedHash(changed.get(key)));
                passwordTextField.clear();
            } else {
                setSetting(key, changed.get(key));
            }
        }
        if (changed.containsKey("port") || changed.containsKey("address")) server.updateInfos();
        changed.clear();
    }

    public void loadSettings() {
        if (FileManager.getServerResource(server, ResourceType.SETTINGS_PROPERTIES).exists()) {
            settingsFile = FileManager.getServerResource(server, ResourceType.SETTINGS_PROPERTIES);
            settingsProps = new PropertiesManager(settingsFile);
            settings = settingsProps.toMap();
        }

        for (Node component : settingComponents.keySet()) {
            String setting = getSetting(settingComponents.get(component));
            if (!settingComponents.get(component).equalsIgnoreCase("password")) {
                if (component instanceof CheckBox) {
                    ((CheckBox) component).setSelected(setting != null && Boolean.parseBoolean(setting));
                } else if (component instanceof ComboBox) {
                    if (setting == null) {
                        ((ComboBox) component).getSelectionModel().select(0);
                    } else {
                        try {
                            ((ComboBox) component).getSelectionModel().select(Integer.parseInt(setting));
                        } catch (NumberFormatException e) {
                            ((ComboBox) component).getSelectionModel().select(0);
                        }
                    }
                } else if (component instanceof TextField) {
                    ((TextField) component).setText(setting == null ? "" : setting);
                }
            }
        }
    }

    public void closeSettings() {
        settingsProps.close();
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
