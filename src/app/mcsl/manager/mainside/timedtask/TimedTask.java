package app.mcsl.manager.mainside.timedtask;

import app.mcsl.event.TimedTaskExecuteEvent;
import app.mcsl.manager.logging.Logger;
import app.mcsl.manager.server.ServersManager;
import app.mcsl.window.content.server.Server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimedTask {

    private String name, date, command;
    private String serverName;
    private boolean isDaily;

    public TimedTask(String name, String serverName, String date, String command, boolean isDaily) {
        this.name = name;
        this.serverName = serverName;
        this.date = date;
        this.command = command;
        this.isDaily = isDaily;
    }

    public Date getDate() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            try {
                return new SimpleDateFormat("HH:mm:ss").parse(date);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    void run() {
        Server server;
        if (ServersManager.isExists(serverName)) {
            server = ServersManager.getServerByName(serverName);
        } else {
            TimedTasksManager.removeTimedTask(this);
            return;
        }

        Logger.info("Running timed task '" + name + "' for server '" + serverName + "'...");

        for (String cmd : command.split(";")) {
            switch (command) {
                case "start":
                case "connect":
                    if (!server.isRun()) server.start();
                    break;
                case "stop":
                    if (server.isRun()) server.stop();
                    break;
                case "restart":
                    if (server.isRun()) server.restart();
                    break;
                default:
                    if (server.isRun()) server.sendCommand(cmd);
            }
        }
        TimedTaskExecuteEvent.execute(this);
        if (!isDaily) {
            TimedTasksManager.removeTimedTask(this);
        }
    }

    //getters
    public String getName() {
        return name;
    }

    public String getServerName() {
        return serverName;
    }

    public String getDateString() {
        return date;
    }

    public String getCommand() {
        return command;
    }

    public boolean isDaily() {
        return isDaily;
    }
}
