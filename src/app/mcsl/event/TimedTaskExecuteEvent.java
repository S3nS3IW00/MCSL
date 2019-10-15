package app.mcsl.event;

import app.mcsl.event.listener.TimedTaskExecuteListener;
import app.mcsl.manager.mainside.timedtasks.TimedTask;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class TimedTaskExecuteEvent {

    private static List<TimedTaskExecuteListener> listeners = new ArrayList<>();

    public static void addListener(TimedTaskExecuteListener listener) {
        listeners.add(listener);
    }

    public static void execute(TimedTask timedTask) {
        Platform.runLater(() -> listeners.forEach(l -> l.execute(timedTask)));

    }

}
