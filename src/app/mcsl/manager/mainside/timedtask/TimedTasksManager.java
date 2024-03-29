package app.mcsl.manager.mainside.timedtask;

import app.mcsl.manager.file.FileManager;

import java.util.ArrayList;
import java.util.List;

public class TimedTasksManager {

    private static List<TimedTask> timedTaskList = new ArrayList<>();

    public static void addTimedTask(TimedTask timedTask) {
        timedTaskList.add(timedTask);
    }

    public static void createTimedTask(TimedTask timedTask) {
        timedTaskList.add(timedTask);
        FileManager.addTimedTask(timedTask.getName(), timedTask.getDateString(), timedTask.getServerName(), timedTask.getCommand(), timedTask.isDaily());
    }

    public static void removeTimedTask(TimedTask timedTask) {
        timedTaskList.remove(timedTask);
        FileManager.removeTimedTask(timedTask.getServerName(), timedTask.getName());
    }

    public static List<TimedTask> getTimedTasks() {
        return timedTaskList;
    }

    public static List<TimedTask> getServersTimedTasks(String serverName) {
        List<TimedTask> timedTasks = new ArrayList<>();
        for (TimedTask timedTask : timedTaskList) {
            if (timedTask.getServerName().equalsIgnoreCase(serverName)) {
                timedTasks.add(timedTask);
            }
        }
        return timedTasks;
    }

    public static boolean isExistsInServer(String id) {
        for (TimedTask timedTask : timedTaskList) {
            if (timedTask.getName().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

}
