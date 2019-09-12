package app.mcsl.windows.contents.server.type.local.pages;

import app.mcsl.events.DirectoryChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.DirectoryType;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.file.PropertiesManager;
import app.mcsl.managers.file.ResourceType;
import app.mcsl.windows.contents.server.type.local.LocalServer;
import app.mcsl.windows.elements.settings.Setting;
import app.mcsl.windows.elements.settings.SettingCategory;
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

public class LocalSettings extends ScrollPane {

    private LocalServer server;

    private VBox body;
    private Map<Node, String> settingComponents = new HashMap<>();

    private Map<String, String> changed = new HashMap<>();

    private File settingsFile, serverPropsFile;
    private PropertiesManager settingsProps, serverProps;
    private Map<String, String> settings = new HashMap<>(), serverSettings = new HashMap<>();

    //settings fields
    private CheckBox spawnAnimalsCheckBox, spawnMonstersCheckBox, spawnNpcsCheckBox, hardcoreModeCheckBox, netherWorldCheckBox, pvpCheckBox, flightCheckBox, forceGamemodeCheckBox,
            generateStructuresCheckBox, commandBlockCheckBox, onlineModeCheckBox, whitelistingCheckBox, preventProxyCheckBox, rconCheckBox, queryCheckBox, autostartCheckBox;
    private ComboBox difficultyComboBox, gamemodeComboBox, levelTypeComboBox, serverFileComboBox;
    private TextField motdTextField, respackUrlTextField, respackSha1TextField, worldNameTextField, levelSeedTextField, rconPasswordTextField, rconPortTextField,
            playerLimitTextField, serverPortTextField, serverRamTextField;

    public LocalSettings(LocalServer server) {
        this.server = server;

        spawnAnimalsCheckBox = new CheckBox();
        settingComponents.put(spawnAnimalsCheckBox, "spawn-animals");
        Setting spawnAnimalsSetting = new Setting(Language.getText("spawnanimals"), spawnAnimalsCheckBox, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        spawnMonstersCheckBox = new CheckBox();
        settingComponents.put(spawnMonstersCheckBox, "spawn-monsters");
        Setting spawnMonstersSetting = new Setting(Language.getText("spawnmonsters"), spawnMonstersCheckBox, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        spawnNpcsCheckBox = new CheckBox();
        settingComponents.put(spawnNpcsCheckBox, "spawn-npcs");
        Setting spawnNpcsSetting = new Setting(Language.getText("spawnnpcs"), spawnNpcsCheckBox, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        hardcoreModeCheckBox = new CheckBox();
        settingComponents.put(hardcoreModeCheckBox, "hardcore");
        Setting hardcoreModeSetting = new Setting(Language.getText("hardcoremode"), hardcoreModeCheckBox, Language.getText("hardcoremodedescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        netherWorldCheckBox = new CheckBox();
        settingComponents.put(netherWorldCheckBox, "allow-nether");
        Setting netherWorldSetting = new Setting(Language.getText("nether"), netherWorldCheckBox, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        pvpCheckBox = new CheckBox();
        settingComponents.put(pvpCheckBox, "pvp");
        Setting pvpSetting = new Setting(Language.getText("pvp"), pvpCheckBox, Language.getText("pvpdescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        flightCheckBox = new CheckBox();
        settingComponents.put(flightCheckBox, "allow-flight");
        Setting flightSetting = new Setting(Language.getText("flight"), flightCheckBox, Language.getText("flightdescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        forceGamemodeCheckBox = new CheckBox();
        settingComponents.put(forceGamemodeCheckBox, "force-gamemode");
        Setting forceGamemodeSetting = new Setting(Language.getText("forcegamemode"), forceGamemodeCheckBox, Language.getText("forcegamemodedescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        difficultyComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(
                new String[]{"0 - " + Language.getText("peaceful"), "1 - " + Language.getText("easy"), "2 - " + Language.getText("normal"), "3 - " + Language.getText("hard")})));
        settingComponents.put(difficultyComboBox, "difficulty");
        Setting difficultySetting = new Setting(Language.getText("difficulty"), difficultyComboBox, null, false) {
            @Override
            public void onChange(Object object) {
                changed.put("difficulty", difficultyComboBox.getSelectionModel().getSelectedItem().toString().substring(0, 1));
            }
        };

        gamemodeComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(
                new String[]{"0 - " + Language.getText("survival"), "1 - " + Language.getText("creative"), "2 - " + Language.getText("adventure"), "3 - " + Language.getText("spectator")})));
        settingComponents.put(gamemodeComboBox, "gamemode");
        Setting gamemodeSetting = new Setting(Language.getText("gamemode"), gamemodeComboBox, null, false) {
            @Override
            public void onChange(Object object) {
                changed.put("gamemode", gamemodeComboBox.getSelectionModel().getSelectedItem().toString().substring(0, 1));
            }
        };

        SettingCategory gameplay = new SettingCategory(Language.getText("gameplay"));
        gameplay.addSetting(spawnAnimalsSetting, spawnMonstersSetting, spawnNpcsSetting, hardcoreModeSetting, netherWorldSetting, pvpSetting, flightSetting, forceGamemodeSetting, difficultySetting, gamemodeSetting);


        motdTextField = new TextField();
        motdTextField.setMinWidth(500);
        settingComponents.put(motdTextField, "motd");
        Setting motdSetting = new Setting("MOTD:", motdTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        respackUrlTextField = new TextField();
        respackUrlTextField.setMinWidth(500);
        settingComponents.put(respackUrlTextField, "resource-pack");
        Setting respackUrlSetting = new Setting(Language.getText("respackurl"), respackUrlTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        respackSha1TextField = new TextField();
        respackSha1TextField.setMinWidth(500);
        settingComponents.put(respackSha1TextField, "resource-pack-sha1");
        Setting respackSha1Setting = new Setting(Language.getText("respacksha1"), respackSha1TextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        SettingCategory appearance = new SettingCategory(Language.getText("appearance"));
        appearance.addSetting(motdSetting, respackUrlSetting, respackSha1Setting);


        generateStructuresCheckBox = new CheckBox();
        settingComponents.put(generateStructuresCheckBox, "generate-structures");
        Setting generateStructuresSetting = new Setting(Language.getText("genstructures"), generateStructuresCheckBox, Language.getText("genstructuresdescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        commandBlockCheckBox = new CheckBox();
        settingComponents.put(commandBlockCheckBox, "enable-command-block");
        Setting commandBlockSetting = new Setting(Language.getText("commandblock"), commandBlockCheckBox, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        worldNameTextField = new TextField();
        settingComponents.put(worldNameTextField, "level-name");
        Setting worldNameSetting = new Setting(Language.getText("worldname"), worldNameTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        levelSeedTextField = new TextField();
        levelSeedTextField.setMinWidth(500);
        settingComponents.put(levelSeedTextField, "level-seed");
        Setting levelSeedSetting = new Setting(Language.getText("levelseed"), levelSeedTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        levelTypeComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(new String[]{"DEFAULT", "FLAT", "LARGEBIOMES", "AMPLIFIED"})));
        settingComponents.put(levelTypeComboBox, "level-type");
        Setting levelTypeSetting = new Setting(Language.getText("leveltype"), levelTypeComboBox, null, false) {
            @Override
            public void onChange(Object object) {
                changed.put("level-type", levelTypeComboBox.getSelectionModel().getSelectedItem().toString());
            }
        };

        SettingCategory world = new SettingCategory(Language.getText("world"));
        world.addSetting(generateStructuresSetting, commandBlockSetting, worldNameSetting, levelSeedSetting, levelTypeSetting);

        onlineModeCheckBox = new CheckBox();
        settingComponents.put(onlineModeCheckBox, "online-mode");
        Setting onlineModeSetting = new Setting(Language.getText("onlinemode"), onlineModeCheckBox, Language.getText("onlinemodedescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        whitelistingCheckBox = new CheckBox();
        settingComponents.put(whitelistingCheckBox, "white-list");
        Setting whitelistSetting = new Setting(Language.getText("whitelist"), whitelistingCheckBox, Language.getText("whitelistdescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        preventProxyCheckBox = new CheckBox();
        settingComponents.put(preventProxyCheckBox, "prevent-proxy-connections");
        Setting preventProxySetting = new Setting(Language.getText("preventproxy"), preventProxyCheckBox, Language.getText("preventproxydescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        rconCheckBox = new CheckBox();
        settingComponents.put(rconCheckBox, "enable-rcon");
        Setting rconSetting = new Setting("Rcon:", rconCheckBox, Language.getText("rcondescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        rconPasswordTextField = new TextField();
        settingComponents.put(rconPasswordTextField, "rcon.password");
        Setting rconPasswordSetting = new Setting(Language.getText("rconpassword"), rconPasswordTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        rconPortTextField = new TextField();
        settingComponents.put(rconPortTextField, "rcon.port");
        Setting rconPortSetting = new Setting("Rcon port:", rconPortTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        playerLimitTextField = new TextField();
        settingComponents.put(playerLimitTextField, "max-players");
        Setting playerLimitSetting = new Setting(Language.getText("playerlimit"), playerLimitTextField, null, false) {
            @Override
            public void onChange(Object object) {

            }
        };

        queryCheckBox = new CheckBox();
        settingComponents.put(queryCheckBox, "enable-query");
        Setting querySetting = new Setting(Language.getText("query"), queryCheckBox, Language.getText("querydescription"), false) {
            @Override
            public void onChange(Object object) {

            }
        };

        SettingCategory networking = new SettingCategory(Language.getText("networking"));
        networking.addSetting(onlineModeSetting, whitelistSetting, preventProxySetting, rconSetting, rconPasswordSetting, rconPortSetting, playerLimitSetting, querySetting);

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

        body = new VBox(10, gameplay, appearance, world, networking, mcsl);

        setContent(body);
        setFitToWidth(true);

        registerSettings();
        loadSettings();
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
