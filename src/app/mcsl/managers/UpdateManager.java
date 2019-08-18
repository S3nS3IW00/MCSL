package app.mcsl.managers;

import app.mcsl.MainClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;

public class UpdateManager {

    public static boolean needUpdate() {
        String line = "false";
        try {
            if (Inet4Address.getByName(new URL("https://mcserverlauncher.tk").getHost()).isReachable(3000)) {
                URL url = new URL("https://mcserverlauncher.tk/app/properties.php?action=checkversion&type=app&currentversion=" + MainClass.VERSION);
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
