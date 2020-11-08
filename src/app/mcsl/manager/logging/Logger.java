package app.mcsl.manager.logging;

import app.mcsl.event.LogEvent;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.mainside.OSManager;
import app.mcsl.window.element.dialog.customdialog.ExceptionDialog;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logger {

    public static final Pattern LOG_PATTERN = Pattern.compile("\\[(?<date>\\d\\d:\\d\\d:\\d\\d) (?<level>[A-Z]+)]:( \\[?<prefix>[A-Za-z0-9]+])*( ?<msg>.+)*");
    public static final File LOGS_FOLDER = new File(OSManager.getRoot() + File.separator + "logs");

    private static final Pattern LOG_FILE_PATTERN = Pattern.compile("mcsl\\.log(?<index>\\d+)\\.txt");
    public static File LOG_FILE;
    private static BufferedWriter OUT_PRINT;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    private static ReadOnlyIntegerWrapper WARN_COUNT_PROP = new ReadOnlyIntegerWrapper(0),
            ERROR_COUNT_PROP = new ReadOnlyIntegerWrapper(0),
            EXCEPTION_COUNT_PROP = new ReadOnlyIntegerWrapper(0);

    public static void init() throws IOException {
        if (!LOGS_FOLDER.exists()) {
            LOGS_FOLDER.getParentFile().mkdirs();
            LOGS_FOLDER.mkdirs();
        }

        if (LOGS_FOLDER.listFiles().length > 0) {
            int logFileIndex = 0;
            for (File file : LOGS_FOLDER.listFiles()) {
                if (file != null && file.isFile()) {
                    Matcher matcher = LOG_FILE_PATTERN.matcher(file.getName());
                    if (matcher.matches()) {
                        if (Integer.parseInt(matcher.group("index")) > logFileIndex)
                            logFileIndex = Integer.parseInt(matcher.group("index"));
                    }
                }
            }
            LOG_FILE = new File(LOGS_FOLDER + File.separator + "mcsl.log" + (logFileIndex + 1) + ".txt");
        } else {
            LOG_FILE = new File(LOGS_FOLDER + File.separator + "mcsl.log0.txt");
        }
        LOG_FILE.createNewFile();
        OUT_PRINT = new BufferedWriter(new FileWriter(LOG_FILE));

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> exception(e));
        /*System.setOut(new PrintStream(System.out, true, Charset.forName("UTF-8").name()) {
            public void println(String s) {
                Logger.append(s, LogLevel.SYSOUT);
            }
        });*/
    }

    private static String getStackTrace(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private static void append(String prefix, String text, LogLevel logLevel) {
        String line = "[" + SDF.format(new Date()) + " " + logLevel.name() + "]: " + (prefix == null ? "" : "[" + prefix + "] ") + text;
        try {
            System.out.println(line);
            OUT_PRINT.write(line);
            OUT_PRINT.newLine();
            OUT_PRINT.flush();
        } catch (IOException e) {
            //empty catch block
        }

        LogEvent.log(logLevel, line, text);
        if (logLevel == LogLevel.EXCEPTION && FileManager.getConfigProps().getBoolProp("errordialog"))
            Platform.runLater(() -> new ExceptionDialog(line).show());
    }

    public static void debug(String text) {
        String className = new Exception().getStackTrace()[1].getClassName();
        append(className.substring(className.lastIndexOf(".") + 1), text, LogLevel.DEBUG);
    }

    public static void info(String text) {
        String className = new Exception().getStackTrace()[1].getClassName();
        append(className.substring(className.lastIndexOf(".") + 1), text, LogLevel.INFO);
    }

    public static void warn(String text) {
        String className = new Exception().getStackTrace()[1].getClassName();
        append(className.substring(className.lastIndexOf(".") + 1), text, LogLevel.WARN);
        WARN_COUNT_PROP.setValue(ERROR_COUNT_PROP.getValue() + 1);
    }

    public static void error(String text) {
        String className = new Exception().getStackTrace()[1].getClassName();
        append(className.substring(className.lastIndexOf(".") + 1), text, LogLevel.ERROR);
        ERROR_COUNT_PROP.setValue(WARN_COUNT_PROP.getValue() + 1);
    }

    public static void exception(Throwable t) {
        String className = new Exception().getStackTrace()[1].getClassName();
        append(className.substring(className.lastIndexOf(".") + 1), getStackTrace(t), LogLevel.EXCEPTION);
        EXCEPTION_COUNT_PROP.setValue(EXCEPTION_COUNT_PROP.getValue() + 1);
    }

    public static void emptyLine() {
        append(null, "", LogLevel.INFO);
    }

    //GETTERS
    public static int getWarnCount() {
        return WARN_COUNT_PROP.getValue();
    }

    public static int getErrorCount() {
        return ERROR_COUNT_PROP.getValue();
    }

    public static int getExceptionCount() {
        return EXCEPTION_COUNT_PROP.getValue();
    }

    public static ReadOnlyIntegerProperty getWarnCountProperty() {
        return WARN_COUNT_PROP.getReadOnlyProperty();
    }

    public static ReadOnlyIntegerProperty getErrorCountProperty() {
        return ERROR_COUNT_PROP.getReadOnlyProperty();
    }

    public static ReadOnlyIntegerProperty getExceptionCountProperty() {
        return EXCEPTION_COUNT_PROP.getReadOnlyProperty();
    }
}
