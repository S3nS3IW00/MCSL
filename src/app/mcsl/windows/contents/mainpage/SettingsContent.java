package app.mcsl.windows.contents.mainpage;

import app.mcsl.MainClass;
import app.mcsl.managers.Language;
import app.mcsl.managers.theme.ThemeColor;
import app.mcsl.managers.theme.ThemeManager;
import app.mcsl.managers.theme.ThemeType;
import app.mcsl.windows.contents.server.ServerStage;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelType;
import app.mcsl.windows.elements.settings.Setting;
import app.mcsl.windows.elements.settings.SettingCategory;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Arrays;

public class SettingsContent extends StackPane {

    private String[] languages = {"hu", "en"};

    private VBox body, needRestartBox;

    private Timeline needRestartBoxAnimation;
    private FadeTransition needRestartBoxFadeOut;
    private WritableValue<Double> needRestartBoxHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return needRestartBox.getMaxHeight();
        }

        @Override
        public void setValue(Double value) {
            needRestartBox.setMaxHeight(value);
        }
    };

    public SettingsContent() {
        SettingCategory standardSettings = new SettingCategory(Language.getText("standardsettings"));
        SettingCategory designSettings = new SettingCategory(Language.getText("uisettings"));

        ComboBox languageComboBox = new ComboBox(FXCollections.observableArrayList(Arrays.asList(languages)));
        languageComboBox.getSelectionModel().select(MainClass.getFileManager().getConfigProps().getProp("language"));
        Setting languageSetting = new Setting(Language.getText("language"), languageComboBox, null, true) {
            @Override
            public void onChange(Object object) {
                MainClass.getFileManager().getConfigProps().setProp("language", object.toString());
            }
        };

        CheckBox autoupdateCheckBox = new CheckBox();
        autoupdateCheckBox.setSelected(MainClass.getFileManager().getConfigProps().getBoolProp("autoupdate"));
        Setting autoupdateSetting = new Setting(Language.getText("autoupdate"), autoupdateCheckBox, null, false) {
            @Override
            public void onChange(Object object) {
                MainClass.getFileManager().getConfigProps().setBoolProp("autoupdate", (boolean) object);
            }
        };

        CheckBox pushNotificationsCheckBox = new CheckBox();
        pushNotificationsCheckBox.setSelected(MainClass.getFileManager().getConfigProps().getBoolProp("notifications"));
        Setting pushNotificationsSetting = new Setting(Language.getText("pushnotifications"), pushNotificationsCheckBox, null, false) {
            @Override
            public void onChange(Object object) {
                MainClass.getFileManager().getConfigProps().setBoolProp("notifications", (boolean) object);
            }
        };

        CheckBox hideWhenExitCheckBox = new CheckBox();
        hideWhenExitCheckBox.setSelected(MainClass.getFileManager().getConfigProps().getBoolProp("hideonexit"));
        Setting hideWhenExitSetting = new Setting(Language.getText("hideonexit"), hideWhenExitCheckBox, Language.getText("hidedescription"), false) {
            @Override
            public void onChange(Object object) {
                MainClass.getFileManager().getConfigProps().setBoolProp("hideonexit", (boolean) object);
            }
        };

        ComboBox themeColorsComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(ThemeManager.displayColorValues())));
        themeColorsComboBox.getSelectionModel().select(Language.getText(ThemeColor.valueOf(MainClass.getFileManager().getConfigProps().getProp("themecolor").toUpperCase()).getDisplayName()));
        Setting themecolorSetting = new Setting(Language.getText("themecolor"), themeColorsComboBox, null, false) {
            @Override
            public void onChange(Object object) {
                MainClass.getFileManager().getConfigProps().setProp("themecolor", ThemeManager.getColorFromDisplayName(object.toString()).name().toLowerCase());
                ThemeManager.changeThemeColor(ThemeManager.getColorFromDisplayName(object.toString()));
                for (ServerStage serverStage : MainClass.getTabManager().getServerStages()) {
                    ThemeManager.applyCss(serverStage.getScene());
                }
            }
        };

        ComboBox themeTypesComboBox = new ComboBox(FXCollections.observableList(Arrays.asList(ThemeManager.displayTypeValues())));
        themeTypesComboBox.getSelectionModel().select(Language.getText(ThemeType.valueOf(MainClass.getFileManager().getConfigProps().getProp("themetype").toUpperCase()).getDisplayName()));
        Setting themetypeSetting = new Setting(Language.getText("themetype"), themeTypesComboBox, null, false) {
            @Override
            public void onChange(Object object) {
                MainClass.getFileManager().getConfigProps().setProp("themetype", ThemeManager.getTypeFromDisplayName(object.toString()).name().toLowerCase());
                ThemeManager.changeThemeType(ThemeManager.getTypeFromDisplayName(object.toString()));
                for (ServerStage serverStage : MainClass.getTabManager().getServerStages()) {
                    ThemeManager.applyCss(serverStage.getScene());
                }
            }
        };

        standardSettings.addSetting(pushNotificationsSetting, hideWhenExitSetting);

        designSettings.addSetting(languageSetting, themecolorSetting, themetypeSetting);

        body = new VBox();
        body.setPadding(new Insets(5));
        body.getChildren().addAll(standardSettings, designSettings);
        body.setSpacing(20);
        body.setMinHeight(0);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinHeight(0);
        scrollPane.setContent(body);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        Label needRestartLabel = new Label(Language.getText("settingsrestart"), LabelType.H3);
        needRestartLabel.setMinHeight(0);
        Button restartButton = new Button(Language.getText("restart"), ButtonType.APPLY_ACTION_BUTTON);
        restartButton.setMinHeight(0);
        restartButton.setPrefWidth(100);
        restartButton.setOnAction(e -> {
            MainClass.getTemplate().getQuitDialog().restart();
        });

        HBox restartButtonBox = new HBox(20, restartButton);
        restartButtonBox.setAlignment(Pos.CENTER);

        needRestartBox = new VBox(10, needRestartLabel, restartButtonBox);
        needRestartBox.setId("settings-box-needrestart-box");
        needRestartBox.setMaxHeight(100);
        VBox.setVgrow(this, Priority.ALWAYS);
        needRestartBox.setAlignment(Pos.CENTER);

        setMinHeight(0);
        VBox.setVgrow(this, Priority.ALWAYS);
        getChildren().addAll(scrollPane);
        setAlignment(needRestartBox, Pos.BOTTOM_CENTER);

        registerSettings();

        needRestartBoxAnimation = new Timeline();
        needRestartBoxAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(needRestartBoxHeight, 100.0)));

        needRestartBoxFadeOut = new FadeTransition(Duration.millis(200));
        needRestartBoxFadeOut.setFromValue(1);
        needRestartBoxFadeOut.setToValue(0);
        needRestartBoxFadeOut.setNode(needRestartBox);
        needRestartBoxFadeOut.setAutoReverse(true);
        needRestartBoxFadeOut.setCycleCount(1);
        needRestartBoxFadeOut.setOnFinished(e -> removeNeedRestartBox());
    }

    private void registerSettings() {
        ObservableList<Node> settingCategories = body.getChildren();
        for (Node node : settingCategories) {
            SettingCategory settingCategory = (SettingCategory) node;
            for (Setting setting : settingCategory.getSettings()) {
                Node component = setting.getComponent();
                if (component instanceof ComboBox) {
                    ((ComboBox) component).getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        setting.onChange(newValue);
                        if (setting.isNotifyChange() && !isNeedRestartShowing()) {
                            showNeedRestart();
                        }
                    });
                } else if (component instanceof CheckBox) {
                    ((CheckBox) component).selectedProperty().addListener((observable, oldValue, newValue) -> {
                        setting.onChange(newValue);
                        if (setting.isNotifyChange() && !isNeedRestartShowing()) {
                            showNeedRestart();
                        }
                    });
                }
            }
        }
    }

    private void showNeedRestart() {
        needRestartBox.setOpacity(1);
        needRestartBox.setMaxHeight(0);
        getChildren().add(needRestartBox);
        needRestartBoxAnimation.play();
    }

    private void disposeNeedRestart() {
        needRestartBoxFadeOut.play();
    }

    public boolean isNeedRestartShowing() {
        return getChildren().contains(needRestartBox);
    }

    public void removeNeedRestartBox() {
        getChildren().remove(needRestartBox);
    }

}
