package app.mcsl.event.listener;

import app.mcsl.manager.mainside.timedtasks.TimedTask;

public interface TimedTaskExecuteListener {

    void execute(TimedTask timedTask);

}
