package app.mcsl.windows.contents.server.type.external;

import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.events.ServerStatusChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.serverside.query.MinecraftPing;
import app.mcsl.managers.serverside.query.MinecraftPingOptions;
import app.mcsl.managers.serverside.query.MinecraftPingReply;
import app.mcsl.managers.tab.TabManager;
import app.mcsl.network.Connection;
import app.mcsl.utils.DataTypeUtil;
import app.mcsl.windows.Template;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerType;
import app.mcsl.windows.contents.server.StatusType;
import app.mcsl.windows.contents.server.pages.ErrorLog;
import app.mcsl.windows.contents.server.pages.TimedTasks;
import app.mcsl.windows.contents.server.type.external.pages.ExternalSettings;
import app.mcsl.windows.elements.ListBox;
import app.mcsl.windows.elements.TabMenu;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.coloredtextflow.ColoredTextFlow;
import app.mcsl.windows.elements.label.KeyValueLabel;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelColor;
import app.mcsl.windows.elements.label.LabelType;
import app.mcsl.windows.elements.notifications.Notification;
import app.mcsl.windows.elements.notifications.NotificationAlertType;
import app.mcsl.windows.elements.notifications.Notifications;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalServer implements Server {

    private String serverName;
    private StatusType serverStatus = StatusType.STOPPED;

    private Pattern logPattern = Pattern.compile("\\[\\d\\d:\\d\\d:\\d\\d (?<logLevel>[a-zA-Z]+)]: (?<message>.+)");
    private boolean isStackTrace = false, reconnect = false;

    private MinecraftPingReply pingReply;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> queryTimerTask;
    private Runnable queryTask;

    private File root;
    private ErrorLog errorLog = new ErrorLog();
    private TimedTasks timedTasks;

    private Connection connection;

    /*****************
     CONTROL PANEL
     ****************/
    private ScrollPane consoleScroll;
    private boolean autoScroll = true, chatMode = false;
    private ColoredTextFlow console;
    private TextField inputField;
    private Button sendButton;
    private VBox controlInfoBox, consoleBox, controlsBox, controlPanelSideBox;
    private HBox inputBox, controlPanelBox, consoleOptions;
    private CheckBox chatModeCheckBox, autoScrollCheckBox;
    private List<String> commandHistory = new ArrayList<>();
    private int commandHistoryIndex = 0;
    private Button startButton;
    private Timeline runningAnimation, stoppingAnimation;
    private IntegerProperty stopColorPercentage = new SimpleIntegerProperty(0);

    //stats
    private ListBox playersCard;
    private Map<String, ImageView> onlinePlayersHead = new HashMap<>();
    private KeyValueLabel ipAddress, playerCount;

    private BorderPane content;

    private ExternalSettings settings;

    public ExternalServer(String serverName) {
        this.serverName = serverName;
        root = FileManager.getServerFolder(this);
        settings = new ExternalSettings(this);
        timedTasks = new TimedTasks(serverName);
        queryTask = () -> {
            try {
                MinecraftPing ping = new MinecraftPing();
                pingReply = ping.getPing(new MinecraftPingOptions().setHostname(settings.getSetting("address")).setPort(Integer.parseInt(settings.getSetting("port"))));
                updateInfos();
                updateOnlinePlayersListCard();
                Template.getServersContent().getServerCardByServer(this).updateInfos(pingReply.getFavicon(), pingReply.getDescription().getText(),
                        pingReply.getPlayers().getOnline(), pingReply.getPlayers().getMax(), (int) ping.getLatency());
            } catch (IOException e) {
                //empty catch block
            }
        };

        ServerStateChangeEvent.addListener((server, newType) -> {
            if (server == this) {
                switch (newType) {
                    case RENAMED:
                        root = FileManager.getServerFolder(getName());
                        settings.loadSettings();
                        break;
                }
            }
        });

        initControlPage();
        build();
    }

    /*****************
     BUILD
     ****************/
    private void build() {
        TabMenu tabMenu = new TabMenu(20) {
            @Override
            public void setContent(Node content) {
                ExternalServer.this.content.setCenter(content);
            }

            @Override
            public void onPageChange(String from, String to) {

            }
        };
        tabMenu.addIem(Language.getText("controlpanel"), controlPanelBox);
        tabMenu.addIem(Language.getText("settings"), settings);
        tabMenu.addIem(Language.getText("errorlog"), errorLog);
        tabMenu.addIem(Language.getText("timedtasks"), timedTasks);

        content = new BorderPane();
        content.setPadding(new Insets(10));
        content.setTop(tabMenu);

        tabMenu.selectTab(0);

        ServerStatusChangeEvent.addListener((server, newType) -> {
            if (server == this) {
                Logger.info("Status changed for server '" + serverName + "' to '" + newType.name() + "'.");

                serverStatus = newType;
                setSceneStatus(newType);
            }
        });

        setSceneStatus(StatusType.STOPPED);
    }

    /*****************
     CONTROL PANEL
     ****************/
    private void initControlPage() {
        console = new ColoredTextFlow(13);
        console.getChildren().addListener((ListChangeListener<Node>) c -> {
            if (autoScroll) {
                consoleScroll.setVvalue(1.0);
            }
        });

        consoleScroll = new ScrollPane();
        consoleScroll.setId("console");
        consoleScroll.setContent(console);
        VBox.setVgrow(consoleScroll, Priority.ALWAYS);

        inputField = new TextField();
        inputField.setMinWidth(450);
        inputField.setMinHeight(30);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setPromptText(Language.getText("inputfieldprompt"));
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0 && isRun()) {
                sendButton.setDisable(false);
            } else {
                sendButton.setDisable(true);
            }
        });
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!inputField.getText().isEmpty()) {
                    sendCommand(inputField.getText());
                    if (commandHistory.size() == 0 || !commandHistory.get(commandHistory.size() - 1).equalsIgnoreCase(inputField.getText())) {
                        commandHistory.add(inputField.getText());
                    }
                    commandHistoryIndex = commandHistory.size();
                    inputField.clear();
                }
            } else if (e.getCode() == KeyCode.UP) {
                if (commandHistory.size() > 0 && commandHistoryIndex > 0) {
                    commandHistoryIndex--;
                    inputField.setText(commandHistory.get(commandHistoryIndex));
                }
                e.consume();
                inputField.requestFocus();
                inputField.end();
            } else if (e.getCode() == KeyCode.DOWN) {
                if (commandHistory.size() > 0) {
                    if (commandHistoryIndex + 1 < commandHistory.size()) {
                        commandHistoryIndex++;
                        inputField.setText(commandHistory.get(commandHistoryIndex));
                        e.consume();
                        inputField.requestFocus();
                        inputField.end();
                    } else {
                        if (commandHistoryIndex + 1 == commandHistory.size()) commandHistoryIndex++;
                        inputField.clear();
                    }
                }
            } else if (e.getCode() == KeyCode.TAB) {
                if (inputField.getText().isEmpty()) return;
                String word = getWordAtCaretPosition(inputField.getText(), inputField.getCaretPosition());
                int startIndex = getStartIndexAtCaretPosition(inputField.getText(), inputField.getCaretPosition());
                int endIndex = getEndIndexAtCaretPosition(inputField.getText(), inputField.getCaretPosition());
                if (isRun() && pingReply != null && pingReply.getPlayers().getOnline() > 0) {
                    for (String playerName : onlinePlayersHead.keySet()) {
                        if (playerName.toLowerCase().startsWith(word.toLowerCase())) {
                            inputField.replaceText(startIndex, endIndex, playerName);
                            break;
                        }
                    }
                }
                e.consume();
            }
        });

        sendButton = new Button(Language.getText("send"), ButtonType.APPLY_ACTION_BUTTON);
        sendButton.setDisable(true);
        sendButton.setMinWidth(50);
        sendButton.setMaxHeight(30);
        sendButton.setOnAction(e -> {
            if (!inputField.getText().isEmpty()) {
                sendCommand(inputField.getText());
                if (commandHistory.size() == 0 || !commandHistory.get(commandHistory.size() - 1).equalsIgnoreCase(inputField.getText())) {
                    commandHistory.add(inputField.getText());
                }
                commandHistoryIndex = commandHistory.size();
                inputField.clear();
            }
        });

        inputBox = new HBox(inputField, sendButton);
        inputBox.setMinHeight(30);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        consoleBox = new VBox(consoleScroll, inputBox);
        VBox.setVgrow(consoleBox, Priority.ALWAYS);
        HBox.setHgrow(consoleBox, Priority.ALWAYS);

        ipAddress = new KeyValueLabel(Language.getText("ipaddress"), "-", LabelColor.THIRDCOLOR);
        playerCount = new KeyValueLabel(Language.getText("playercount"), "0/0", LabelColor.THIRDCOLOR);

        playersCard = new ListBox(200, 160);
        playersCard.getBody().setSpacing(5);
        VBox.setVgrow(playersCard, Priority.ALWAYS);

        controlInfoBox = new VBox(10, ipAddress, playerCount, new Label(Language.getText("onlineplayers"), LabelType.H2, LabelColor.THIRDCOLOR), playersCard);
        controlInfoBox.setMinWidth(200);
        VBox.setVgrow(controlInfoBox, Priority.ALWAYS);
        controlInfoBox.setStyle("-fx-border-color: -fx-defcolor;-fx-border-width: 4px 0px 4px 0px;");

        startButton = new Button(Language.getText("start"), ButtonType.ACTION_BUTTON);
        startButton.setStyle("-fx-background-color: -fx-apply;");

        controlsBox = new VBox(startButton);
        controlsBox.setAlignment(Pos.TOP_CENTER);
        controlsBox.setMinHeight(150);
        controlsBox.setPadding(new Insets(10, 0, 10, 0));

        chatModeCheckBox = new CheckBox(Language.getText("chatmode"));
        chatModeCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> chatMode = newValue);

        autoScrollCheckBox = new CheckBox(Language.getText("autoscroll"));
        autoScrollCheckBox.setSelected(true);
        autoScrollCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> autoScroll = newValue);

        consoleOptions = new HBox(10, chatModeCheckBox, autoScrollCheckBox);

        controlPanelSideBox = new VBox(10, controlInfoBox, controlsBox, consoleOptions);
        controlPanelSideBox.setPadding(new Insets(0, 0, 10, 0));

        controlPanelBox = new HBox(10, consoleBox, controlPanelSideBox);

        stopColorPercentage.addListener((observable, oldValue, newValue) -> startButton.setStyle("-fx-background-color: linear-gradient(to right, -fx-error 0%, -fx-error " + newValue.intValue() + "%, -fx-apply " + newValue.intValue() + "%, -fx-apply 100%)"));

        runningAnimation = new Timeline();
        runningAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(stopColorPercentage, 100)));
        runningAnimation.setOnFinished(e -> {
            startButton.setStyle(null);
            startButton.setType(ButtonType.ERROR_ACTION_BUTTON);
            startButton.setDisable(false);
            startButton.setOnAction(e1 -> stop());
        });

        stoppingAnimation = new Timeline();
        stoppingAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(stopColorPercentage, 0)));
        stoppingAnimation.setOnFinished(e -> {
            startButton.setStyle(null);
            startButton.setType(ButtonType.APPLY_ACTION_BUTTON);
            startButton.setDisable(false);
            startButton.setOnAction(e1 -> start());
        });

        updateInfos();
    }

    private void setSceneStatus(StatusType type) {
        switch (type) {
            case CONNECTING:
                startButton.setDisable(true);
                startButton.setText(Language.getText("connecting"));
                break;
            case CONNECTED:
                startButton.setText(Language.getText("disconnect"));
                runningAnimation.play();
                settings.loadSettings();
                queryTimerTask = scheduledExecutorService.scheduleAtFixedRate(queryTask, 0, 5, TimeUnit.SECONDS);
                break;
            case STOPPED:
                startButton.setText(Language.getText("connect"));
                stoppingAnimation.play();
                playersCard.clear();
                playerCount.setValue("0/0");
                if (queryTimerTask != null) queryTimerTask.cancel(false);
                if (reconnect) {
                    start();
                    reconnect = false;
                }
                break;
            case PREPARING:
                startButton.setDisable(true);
                startButton.setText(Language.getText("preparing"));
                break;
        }
    }

    @Override
    public Pane getContent() {
        return content;
    }

    @Override
    public void start() {
        Logger.info("Starting server '" + serverName + "'...");

        console.getChildren().clear();
        errorLog.clear();
        ServerStatusChangeEvent.change(this, StatusType.PREPARING);
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("preparingforstart"));
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("savingsettings"));
        settings.save();
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("loadingsettings"));
        settings.loadSettings();
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("checkingfiles"));
        if (FileManager.checkServerFiles(serverName)) {
            Logger.info("Connecting to '" + settings.getSetting("address") + "'...");

            connection = new Connection(this, settings.getSetting("address"), Integer.parseInt(settings.getSetting("pluginport")), settings.getSetting("username"), settings.getSetting("password"));
            connection.connect();
            ServerStatusChangeEvent.change(this, StatusType.CONNECTING);
            console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("connectingtoserver"));
        } else {
            Text incorrectConfigText = new Text("[MinecraftServerLauncher] " + Language.getText("checkfileserrormessage"));
            incorrectConfigText.setFill(Color.RED);
            incorrectConfigText.setOnMouseClicked(e -> FileManager.repairServerFiles(serverName));
            console.appendLine(incorrectConfigText);

            Notification notification = new Notification(serverName, Language.getText("checkfileserrormessage"), NotificationAlertType.ERROR);
            notification.setOnAction(e -> FileManager.repairServerFiles(serverName));
            Notifications.push(TabManager.getTabClassByServer(this), notification);
            ServerStatusChangeEvent.change(this, StatusType.STOPPED);
        }
    }

    @Override
    public void stop() {
        Logger.info("Stopping server '" + serverName + "'...");

        if (!connection.getSocket().isClosed()) {
            Logger.info("Disconnecting from '" + connection.getAddress() + "'...");

            connection.getTask().cancel(true);
            sendCommand("#disconnect");
            try {
                connection.getSocket().close();
            } catch (IOException e) {
                //empty catch block
            }
        }
    }

    @Override
    public void restart() {
        if (isRun()) {
            Logger.info("Restarting server '" + serverName + "'...");

            reconnect = true;
            stop();
        }
    }

    @Override
    public String getName() {
        return serverName;
    }

    @Override
    public void rename(String name) {
        settings.closeSettings();
        serverName = name;
    }

    @Override
    public ServerType getType() {
        return ServerType.EXTERNAL;
    }

    @Override
    public void sendCommand(String command) {
        if (!isRun() || command.length() == 0) return;
        connection.getClient().sendData((chatMode ? "say " + command : command));
    }

    @Override
    public StatusType getStatus() {
        return serverStatus;
    }

    @Override
    public void saveSettings() {
        settings.save();
    }

    @Override
    public File getRoot() {
        return root;
    }

    @Override
    public boolean isRun() {
        return serverStatus != StatusType.STOPPED;
    }

    private String getLogLevel(String text) {
        Matcher matcher = logPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group("logLevel");
        }
        return null;
    }

    private String getMessage(String text) {
        Matcher matcher = logPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group("message");
        }
        return null;
    }

    void parseLine(String line) {
        String logLevel = getLogLevel(line);
        String message = getMessage(line);

        if (logLevel == null || message == null) {
            if (line.contains("Exception")) isStackTrace = true;
            if (isStackTrace) {
                console.appendLine("§c" + line);
                errorLog.log("§c" + line, getStatus(), false);
                return;
            }
            console.appendLine(line);
            return;
        }

        if (message.matches("Done \\(\\d+[,.]\\d+s\\)!.*")) {
            console.appendLine("§a" + line);
            return;
        } else if (message.equalsIgnoreCase("Stopping server")) {
            console.appendLine("§c" + line);
            return;
        }

        isStackTrace = false;
        switch (logLevel) {
            case "WARN":
                console.appendLine("§e" + line);
                errorLog.log("§e" + line, getStatus(), true);
                break;
            case "INFO":
                console.appendLine(line.replace(logLevel, "§b" + logLevel + "§r"));
                break;
            case "ERROR":
                console.appendLine("§c" + line);
                errorLog.log("§c" + line, getStatus(), true);
                Notifications.push(TabManager.getTabClassByServer(this), new Notification(serverName, Language.getText("servererror"), NotificationAlertType.ERROR));
                isStackTrace = true;
                break;
            default:
                console.appendLine(line);
        }
    }

    public void updateInfos() {
        Platform.runLater(() -> {
            ipAddress.setValue(settings.getSetting("address") + (DataTypeUtil.isInt(settings.getSetting("port")) ? Integer.parseInt(settings.getSetting("port")) == 25565 ? "" : ":" + settings.getSetting("port") : ""));
            playerCount.setValue(pingReply == null ? "-/-" : pingReply.getPlayers().getOnline() + "/" + pingReply.getPlayers().getMax());
        });
    }

    private void updateOnlinePlayersListCard() {
        Platform.runLater(() -> {
            playersCard.clear();
            if (pingReply.getPlayers().getSample() != null) {
                for (MinecraftPingReply.Player player : pingReply.getPlayers().getSample()) {
                    try {
                        if (Inet4Address.getByName(new URL("https://minotar.net").getHost()).isReachable(3000)) {
                            if (!onlinePlayersHead.containsKey(player.getName()))
                                onlinePlayersHead.put(player.getName(), new ImageView("https://minotar.net/avatar/" + player.getName() + "/18"));
                            playersCard.add(onlinePlayersHead.get(player.getName()), player.getName());
                        } else {
                            playersCard.add(FileManager.STEVE_ICON, player.getName());
                        }
                    } catch (IOException e) {
                        Logger.exception(e);
                    }
                }
            }
        });
    }

    private String splitTextToCaretWord(String text, int caretPosition) {
        if (text.isEmpty()) return null;
        int startIndex = 0, endIndex = text.length();
        if (caretPosition >= text.length()) caretPosition -= 1;
        if (caretPosition != startIndex) {
            if (text.charAt(caretPosition) == ' ') caretPosition -= 1;
            for (int i = caretPosition; i >= 0; i--) {
                if (text.charAt(i) == ' ') {
                    startIndex = i + 1;
                    break;
                }
            }
        }
        if (text.charAt(caretPosition) != ' ') {
            if (text.charAt(caretPosition) != ' ') {
                for (int i = caretPosition; i < text.toCharArray().length; i++) {
                    if (text.charAt(i) == ' ') {
                        endIndex = i;
                        break;
                    }
                }
            } else {
                endIndex = caretPosition;
            }
        }
        return text.substring(startIndex, endIndex) + ";" + startIndex + ";" + endIndex;
    }

    private String getWordAtCaretPosition(String text, int caretPosition) {
        return splitTextToCaretWord(text, caretPosition).split(";")[0];
    }

    private int getStartIndexAtCaretPosition(String text, int caretPosition) {
        return Integer.parseInt(splitTextToCaretWord(text, caretPosition).split(";")[1]);
    }

    private int getEndIndexAtCaretPosition(String text, int caretPosition) {
        return Integer.parseInt(splitTextToCaretWord(text, caretPosition).split(";")[2]);
    }

    //getters
    public ColoredTextFlow getConsole() {
        return console;
    }

    public ExternalSettings getSettings() {
        return settings;
    }

    public Connection getConnection() {
        return connection;
    }
}
