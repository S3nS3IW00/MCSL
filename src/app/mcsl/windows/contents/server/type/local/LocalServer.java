package app.mcsl.windows.contents.server.type.local;

import app.mcsl.events.ServerStateChangeEvent;
import app.mcsl.events.ServerStatusChangeEvent;
import app.mcsl.managers.Language;
import app.mcsl.managers.file.FileManager;
import app.mcsl.managers.logging.Logger;
import app.mcsl.managers.mainside.OSManager;
import app.mcsl.managers.serverside.query.MinecraftPing;
import app.mcsl.managers.serverside.query.MinecraftPingOptions;
import app.mcsl.managers.serverside.query.MinecraftPingReply;
import app.mcsl.managers.tab.TabManager;
import app.mcsl.managers.threads.RunnableThread;
import app.mcsl.utils.DataTypeUtil;
import app.mcsl.windows.Template;
import app.mcsl.windows.contents.server.LogPattern;
import app.mcsl.windows.contents.server.Server;
import app.mcsl.windows.contents.server.ServerType;
import app.mcsl.windows.contents.server.StatusType;
import app.mcsl.windows.contents.server.pages.ErrorLog;
import app.mcsl.windows.contents.server.pages.TimedTasks;
import app.mcsl.windows.contents.server.type.local.pages.LocalFiles;
import app.mcsl.windows.contents.server.type.local.pages.LocalSettings;
import app.mcsl.windows.elements.ListBox;
import app.mcsl.windows.elements.TabMenu;
import app.mcsl.windows.elements.button.Button;
import app.mcsl.windows.elements.button.ButtonType;
import app.mcsl.windows.elements.coloredtextflow.ColoredTextFlow;
import app.mcsl.windows.elements.dialog.types.AlertDialog;
import app.mcsl.windows.elements.dialog.types.AlertType;
import app.mcsl.windows.elements.label.KeyValueLabel;
import app.mcsl.windows.elements.label.Label;
import app.mcsl.windows.elements.label.LabelColor;
import app.mcsl.windows.elements.label.LabelType;
import app.mcsl.windows.elements.notifications.Notification;
import app.mcsl.windows.elements.notifications.NotificationAlertType;
import app.mcsl.windows.elements.notifications.Notifications;
import com.dosse.upnp.UPnP;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.WritableValue;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Inet4Address;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LocalServer implements Server {

    private String serverName;
    private StatusType serverStatus = StatusType.STOPPED;
    private boolean restart = false;

    private boolean isStackTrace = false;

    private StopType lastStopType;
    private boolean portOpened = false;

    private ProcessBuilder processBuilder;
    private Process process;
    private RunnableThread serverThread;
    private PrintWriter commandWriter;

    private MinecraftPingReply pingReply;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> queryTimerTask;
    private Runnable queryTask;

    private File root;
    private ErrorLog errorLog = new ErrorLog();
    private LocalSettings settings;
    private LocalFiles localFiles;
    private TimedTasks timedTasks;
    private LogPattern logPattern;

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
    private Button startButton, restartButton;
    private Timeline runningAnimation, stoppingAnimation;
    private IntegerProperty stopColorPercentage = new SimpleIntegerProperty(0);
    private WritableValue<Double> restartButtonHeight = new WritableValue<Double>() {
        @Override
        public Double getValue() {
            return restartButton.getMaxHeight();
        }

        @Override
        public void setValue(Double value) {
            restartButton.setMaxHeight(value);
        }
    };

    //stats
    private ListBox playersCard;
    private Map<String, ImageView> onlinePlayersHead = new HashMap<>();
    private KeyValueLabel ipAddress, playerCount;

    private BorderPane content;

    public LocalServer(String serverName) {
        this.serverName = serverName;
        root = FileManager.getServerFolder(this);
        settings = new LocalSettings(this);
        localFiles = new LocalFiles(this);
        timedTasks = new TimedTasks(serverName);
        queryTask = () -> {
            try {
                MinecraftPing ping = new MinecraftPing();
                pingReply = ping.getPing(new MinecraftPingOptions().setHostname("localhost").setPort(Integer.parseInt(settings.getSetting("server-port"))));
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
                        initSystem();
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
                LocalServer.this.content.setCenter(content);
            }

            @Override
            public void onPageChange(String from, String to) {

            }
        };
        tabMenu.addIem(Language.getText("controlpanel"), controlPanelBox);
        tabMenu.addIem(Language.getText("settings"), settings);
        tabMenu.addIem(Language.getText("errorlog"), errorLog);
        tabMenu.addIem(Language.getText("filemanager"), localFiles);
        //tabMenu.addIem(Language.getText("playermanager"), null);
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
                    if (commandHistory.size() == 0 || !commandHistory.get(commandHistoryIndex - 1).equalsIgnoreCase(inputField.getText())) {
                        commandHistory.add(inputField.getText());
                        commandHistoryIndex = commandHistory.size();
                    }
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
                if (commandHistory.size() == 0 || !commandHistory.get(commandHistoryIndex - 1).equalsIgnoreCase(inputField.getText())) {
                    commandHistory.add(inputField.getText());
                    commandHistoryIndex = commandHistory.size();
                }
                inputField.clear();
            }
        });

        inputBox = new HBox(inputField, sendButton);
        inputBox.setMinHeight(30);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        consoleBox = new VBox(consoleScroll, inputBox);
        VBox.setVgrow(consoleBox, Priority.ALWAYS);
        HBox.setHgrow(consoleBox, Priority.ALWAYS);

        ipAddress = new KeyValueLabel(Language.getText("ipaddress"), "localhost", LabelColor.THIRDCOLOR);
        playerCount = new KeyValueLabel(Language.getText("playercount"), "0/0", LabelColor.THIRDCOLOR);

        playersCard = new ListBox(200, 160);
        playersCard.getBody().setSpacing(5);
        VBox.setVgrow(playersCard, Priority.ALWAYS);

        controlInfoBox = new VBox(10, ipAddress, playerCount, new Label(Language.getText("onlineplayers"), LabelType.H2, LabelColor.THIRDCOLOR), playersCard);
        controlInfoBox.setMinWidth(200);
        VBox.setVgrow(controlInfoBox, Priority.ALWAYS);
        controlInfoBox.setStyle("-fx-border-color: -fx-defcolor;-fx-border-width: 4px 0px 4px 0px;");

        startButton = new Button(Language.getText("start"), ButtonType.APPLY_ACTION_BUTTON);

        restartButton = new Button(Language.getText("restart"), ButtonType.WARNING_ACTION_BUTTON);
        restartButton.setMaxHeight(0);
        restartButton.setOnAction(e -> restart());

        controlsBox = new VBox(startButton, restartButton);
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

        controlPanelBox = new HBox(10, consoleBox, controlPanelSideBox);

        stopColorPercentage.addListener((observable, oldValue, newValue) -> startButton.setStyle("-fx-background-color: linear-gradient(to right, -fx-error 0%, -fx-error " + newValue.intValue() + "%, -fx-apply " + newValue.intValue() + "%, -fx-apply 100%)"));

        runningAnimation = new Timeline();
        runningAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(stopColorPercentage, 100)));
        runningAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(restartButtonHeight, 40.0)));
        runningAnimation.setOnFinished(e -> {
            startButton.setDisable(false);
            startButton.setStyle(null);
            startButton.setType(ButtonType.ERROR_ACTION_BUTTON);
            startButton.setOnAction(e1 -> {
                lastStopType = StopType.USER;
                stop();
            });
        });

        stoppingAnimation = new Timeline();
        stoppingAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(stopColorPercentage, 0)));
        stoppingAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(restartButtonHeight, 0.0)));

        logPattern = new LogPattern(null);

        updateInfos();
    }

    /*****************
     SYSTEM
     ****************/
    private void initSystem() {
        switch (OSManager.getOs()) {
            case WINDOWS:
                processBuilder = new ProcessBuilder("cmd", "/c", "cd /d \"" + root + "\" & java -Xms" + settings.getSetting("ram") + "M -Xmx" + settings.getSetting("ram") + "M -Dfile.encoding=UTF-8 -jar \"" + FileManager.getServerFile(settings.getSetting("serverfile")) + "\" nogui");
                break;
            case UNIX:
                processBuilder = new ProcessBuilder("bash", "-c", "cd /d \"" + root + "\" & java -Xms" + settings.getSetting("ram") + "M -Xmx" + settings.getSetting("ram") + "M -Dfile.encoding=UTF-8 -jar \"" + FileManager.getServerFile(settings.getSetting("serverfile")) + "\" nogui");
                break;
            case MAC:
                processBuilder = new ProcessBuilder("#!/bin/bash", "cd /d \"" + root + "\" & exec java -Xms" + settings.getSetting("ram") + "M -Xmx" + settings.getSetting("ram") + "M -Dfile.encoding=UTF-8 -jar \"" + FileManager.getServerFile(settings.getSetting("serverfile")) + "\" nogui");
                break;
        }
        serverThread = new RunnableThread("ServerThread-" + serverName) {
            @Override
            public void onRun() {
                if (!getProcess().isAlive()) {
                    if (lastStopType == null) lastStopType = StopType.ERROR;
                    if (portOpened) closePort();
                    ServerStatusChangeEvent.change(LocalServer.this, StatusType.STOPPED);
                    if (queryTimerTask != null) queryTimerTask.cancel(false);
                    cancel();
                }
                try {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getProcess().getInputStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String l = line;
                        Platform.runLater(() -> parseLine(l));
                    }
                    reader.close();
                } catch (final Exception e) {
                    //empty catch block
                }
            }
        };
    }

    private void setSceneStatus(StatusType type) {
        switch (type) {
            case STARTING:
                startButton.setDisable(true);
                startButton.setText(Language.getText("starting"));
                break;
            case RUNNING:
                startButton.setText(Language.getText("stop"));
                runningAnimation.play();
                settings.loadSettings();
                localFiles.refreshFiles();
                queryTimerTask = scheduledExecutorService.scheduleAtFixedRate(queryTask, 0, 5, TimeUnit.SECONDS);
                break;
            case STOPPING:
                startButton.setText(Language.getText("stopping"));
                restartButton.setDisable(true);
                startButton.setDisable(true);
                stoppingAnimation.play();
                break;
            case STOPPED:
                restartButton.setDisable(false);
                startButton.setText(Language.getText("start"));
                startButton.setDisable(false);
                startButton.setStyle(null);
                startButton.setType(ButtonType.APPLY_ACTION_BUTTON);
                startButton.setOnAction(e1 -> start());
                if (restart) {
                    start();
                    restart = false;
                }
                playersCard.clear();
                playerCount.setValue("0/" + settings.getSetting("max-players"));
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

        ServerStatusChangeEvent.change(this, StatusType.PREPARING);
        console.getChildren().clear();
        errorLog.clear();
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("preparingforstart"));
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("savingsettings"));
        settings.save();
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("loadingsettings"));
        settings.loadSettings();
        console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("checkingfiles"));
        if (FileManager.checkServerFiles(serverName)) {
            console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("gettinglogpattern"));
            String logPattern = getLogPattern();
            if (logPattern != null) {
                this.logPattern.initPattern(logPattern);
                initSystem();
                console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("openingport"));
                if (openPort()) {
                    StringBuilder command = new StringBuilder();
                    for (String cmd : processBuilder.command()) {
                        command.append(cmd).append(" ");
                    }
                    console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("executingcommand") + ": " + command.toString());
                    Logger.info("Executing command: '" + command + "'...");
                    ServerStatusChangeEvent.change(this, StatusType.STARTING);
                    lastStopType = null;
                    try {
                        process = processBuilder.start();
                        commandWriter = new PrintWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
                        serverThread.start();
                    } catch (IOException e) {
                        Logger.exception(e);
                    }
                } else {
                    ServerStatusChangeEvent.change(this, StatusType.STOPPED);
                }
            } else {
                console.appendLine("§c[MinecraftServerLauncher] " + Language.getText("logpatternerrorline1"));
                console.appendLine("§c[MinecraftServerLauncher] " + Language.getText("logpatternerrorline2"));
                console.appendLine("§c[MinecraftServerLauncher] " + Language.getText("logpatternerrorline3"));
                ServerStatusChangeEvent.change(this, StatusType.STOPPED);
            }
        } else {
            Text incorrectConfigText = new Text("[MinecraftServerLauncher] " + Language.getText("checkfileserrormessage"));
            incorrectConfigText.setFill(Color.RED);
            incorrectConfigText.setOnMouseClicked(e -> {
                if (FileManager.getServerFilesFolder().listFiles().length > 0) {
                    FileManager.repairServerFiles(serverName);
                } else {
                    new AlertDialog(200, 400, Language.getText("error"), Language.getText("noserverfile"), AlertType.ERROR).show();
                }
            });
            console.appendLine(incorrectConfigText);

            Notification notification = new Notification(serverName, Language.getText("checkfileserrormessage"), NotificationAlertType.ERROR);
            notification.setOnAction(e -> {
                if (FileManager.getServerFilesFolder().listFiles().length > 0) {
                    FileManager.repairServerFiles(serverName);
                } else {
                    new AlertDialog(200, 400, Language.getText("error"), Language.getText("noserverfile"), AlertType.ERROR).show();
                }
            });
            Notifications.push(TabManager.getTabClassByServer(this), notification);
            ServerStatusChangeEvent.change(this, StatusType.STOPPED);
        }
    }

    @Override
    public void stop() {
        Logger.info("Stopping server '" + serverName + "'...");

        commandWriter.write("stop\n");
        commandWriter.flush();
    }

    @Override
    public void restart() {
        if (isRun()) {
            Logger.info("Restarting server '" + serverName + "'...");

            restart = true;
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
        return ServerType.LOCAL;
    }

    @Override
    public void sendCommand(String command) {
        if (!isRun() || command.length() == 0) return;
        commandWriter.write((chatMode ? "say " + command : command) + "\n");
        commandWriter.flush();
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

    private void parseLine(String line) {
        String logLevel = logPattern.getLogLevel(line);
        String message = logPattern.getMessage(line);

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
            ServerStatusChangeEvent.change(this, StatusType.RUNNING);
            return;
        } else if (message.equalsIgnoreCase("Stopping server")) {
            console.appendLine("§c" + line);
            ServerStatusChangeEvent.change(this, StatusType.STOPPING);
            return;
        } else if (logLevel.equalsIgnoreCase("WARN") && message.contains("FAILED TO BIND TO PORT!")) {
            Notifications.push(TabManager.getTabClassByServer(this), new Notification(serverName, Language.getText("serverstarterror.portbind"), NotificationAlertType.WARNING));
            console.appendLine("§c[MinecraftServerLauncher] " + Language.getText("serverstarterror.portbind"));
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

    private String getLogPattern() {
        Logger.info("Getting log pattern for server '" + serverName + "'...");

        InputStream logXML = FileManager.getInputStreamInExternalJar(FileManager.getServerFile(getSettings().getSetting("serverfile")), "log4j2.xml");
        if (logXML != null) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(logXML);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("PatternLayout");
                Element eElement = (Element) nList.item(0);
                logXML.close();
                return eElement.getAttribute("pattern");
            } catch (ParserConfigurationException | SAXException | IOException e) {
                //empty catch block
            }
        }
        return null;
    }

    public void updateInfos() {
        Platform.runLater(() -> {
            ipAddress.setValue(UPnP.getExternalIP() + (DataTypeUtil.isInt(settings.getSetting("server-port")) ? Integer.parseInt(settings.getSetting("server-port")) == 25565 ? "" : ":" + settings.getSetting("server-port") : ""));
            playerCount.setValue((pingReply == null ? 0 : pingReply.getPlayers().getOnline()) + "/" + settings.getSetting("max-players"));
        });
    }

    private boolean openPort() {
        Logger.info("Opening port '" + settings.getSetting("server-port") + "' for server '" + serverName + "'...");

        if (UPnP.isUPnPAvailable()) {
            if (!UPnP.isMappedTCP(Integer.parseInt(settings.getSetting("server-port")))) {
                UPnP.openPortTCP(Integer.parseInt(settings.getSetting("server-port")));
                console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("portopened", settings.getSetting("server-port")));
                portOpened = true;
            } else {
                console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("portalreadyopened", settings.getSetting("server-port")));
            }
        } else {
            console.appendLine("§c[MinecraftServerLauncher] " + Language.getText("upnperrormessage"));
        }
        return true;
    }

    private void closePort() {
        Logger.info("Closing port '" + settings.getSetting("server-port") + "' for server '" + serverName + "'...");

        Platform.runLater(() -> {
            if (UPnP.isMappedTCP(Integer.parseInt(settings.getSetting("server-port")))) {
                UPnP.closePortTCP(Integer.parseInt(settings.getSetting("server-port")));
                console.appendLine("§a[MinecraftServerLauncher] " + Language.getText("portclosed", settings.getSetting("server-port")));
                portOpened = false;
            }
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
    public Process getProcess() {
        return process;
    }

    public ColoredTextFlow getConsole() {
        return console;
    }

    public LocalSettings getSettings() {
        return settings;
    }
}

enum StopType {

    USER, ERROR;

}
