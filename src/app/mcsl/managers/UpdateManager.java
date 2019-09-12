package app.mcsl.managers;

import app.mcsl.MainClass;
import app.mcsl.managers.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;

public class UpdateManager {

    public static boolean needUpdate() {
        Logger.info("Checking for updates...");
        String line = "false";
        try {
            if (Inet4Address.getByName(new URL("https://mcsl.app").getHost()).isReachable(3000)) {
                URL url = new URL("https://mcsl.app/requests/checkupdate.php?type=app&currentversion=" + MainClass.VERSION);
                URLConnection connection = url.openConnection();
                connection.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    line = inputLine;
                }
                in.close();
            }
        } catch (IOException e) {
            //empty catch block
        }
        return line.equalsIgnoreCase("true");
    }

}
