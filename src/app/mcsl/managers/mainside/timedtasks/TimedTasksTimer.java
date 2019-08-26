package app.mcsl.managers.mainside.timedtasks;

import app.mcsl.utils.DateTimeUtils;
import javafx.application.Platform;

import java.text.SimpleDateFormat;

public class TimedTasksTimer implements Runnable {

    @Override
    public void run() {
        if (TimedTasksManager.getTimedTasks().size() > 0) {
            for (TimedTask timedTask : TimedTasksManager.getTimedTasks()) {
                final Runnable runnable1 = timedTask::run;
                if (timedTask.isDaily()) {
                    if (DateTimeUtils.isEqualsTime(new SimpleDateFormat("HH:mm:ss").format(timedTask.getDate()))) {
                        Platform.runLater(runnable1);
                    }
                } else {
                    if (DateTimeUtils.isEquals(timedTask.getDate())) {
                        Platform.runLater(runnable1);
                    }
                }
            }
        }
    }
}
