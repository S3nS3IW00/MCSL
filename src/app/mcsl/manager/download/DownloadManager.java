package app.mcsl.manager.download;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DownloadManager {

    private static List<DownloadItem> downloadItems = new ArrayList<>();
    private static List<DownloadType> downloadTypes = new ArrayList<>();

    public static void fetch() {
        downloadItems.clear();
        downloadTypes.clear();

        StringBuilder jsonString = new StringBuilder();
        try {
            if (Inet4Address.getByName(new URL("https://mcsl.app").getHost()).isReachable(3000)) {
                URL url = new URL("https://mcsl.app/requests/downloads.json");
                URLConnection connection = url.openConnection();
                connection.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    jsonString.append(inputLine);
                }
                in.close();
            }
        } catch (IOException e) {
            //empty catch block
        }
        try {
            JSONArray array = (JSONArray) new JSONParser().parse(jsonString.toString());
            for (Object o : array) {
                JSONObject object = (JSONObject) o;
                DownloadType type = DownloadType.valueOf(object.get("type").toString().toUpperCase());
                downloadItems.add(new DownloadItem(object.get("name").toString(), object.get("downloadUrl").toString(), type));
                if (!downloadTypes.contains(type)) downloadTypes.add(type);
            }
        } catch (ParseException e) {
            //empty catch block
        }
    }

    public static List<DownloadItem> getDownloadItems() {
        return downloadItems;
    }

    public static List<DownloadType> getDownloadTypes() {
        return downloadTypes;
    }

    public static List<DownloadItem> getDownloadItemsByType(DownloadType type) {
        List<DownloadItem> items = new ArrayList<>();
        for (DownloadItem downloadItem : downloadItems) {
            if (downloadItem.getType() == type) items.add(downloadItem);
        }
        return items;
    }
}
