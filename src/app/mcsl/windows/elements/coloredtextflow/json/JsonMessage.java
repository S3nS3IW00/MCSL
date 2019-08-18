package app.mcsl.windows.elements.coloredtextflow.json;

import org.json.simple.JSONArray;

public class JsonMessage extends JSONArray {

    public void append(JsonText jsonText){
        add(jsonText);
    }

}
