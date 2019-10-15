package app.mcsl.window.content.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogPattern {

    private Pattern logPattern;

    public LogPattern(String patternString) {
        initPattern(patternString);
    }

    public void initPattern(String patternString) {
        if (patternString == null) {
            logPattern = Pattern.compile("\\[\\d\\d:\\d\\d:\\d\\d (?<logLevel>[a-zA-Z]+)]: (?<message>.+)");
        } else {
            patternString = patternString.replace("[", "\\[")
                    .replaceAll("%d\\{[a-zA-Z:-]+}", "(?<date>[0-9:-]+)")
                    .replaceAll("%t", "(?<thread>[\\\\sa-zA-Z0-9-]+)")
                    .replaceAll("%level", "(?<logLevel>[a-zA-Z]+)")
                    .replaceAll("%msg", "(?<message>.+)")
                    .replaceAll("%n", "");
            logPattern = Pattern.compile(patternString);
        }
    }

    public String getLogLevel(String text) {
        Matcher matcher = logPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group("logLevel");
        }
        return null;
    }

    public String getMessage(String text) {
        Matcher matcher = logPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group("message");
        }
        return null;
    }
}
