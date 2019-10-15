package app.mcsl.event.listener;

import app.mcsl.manager.mainside.timedtask.TimedTask;

public interface TimedTaskExecuteListener {

    void execute(TimedTask timedTask);

}
