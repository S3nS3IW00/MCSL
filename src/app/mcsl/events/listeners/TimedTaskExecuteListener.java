package app.mcsl.events.listeners;

import app.mcsl.managers.mainside.timedtasks.TimedTask;

public interface TimedTaskExecuteListener {

    void execute(TimedTask timedTask);

}
