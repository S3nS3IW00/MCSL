package app.mcsl.windows.elements.coloredtextflow.json;

import app.mcsl.windows.elements.coloredtextflow.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonText extends JSONObject {

    public JsonText(String text){
        put("text", text);
    }

    public JsonText color(ChatColor color){
        if(containsKey("color")){
            replace("color", color.name().toLowerCase());
        } else {
            put("color", color.name().toLowerCase());
        }

        return this;
    }

    public JsonText bold(){
        if(containsKey("bold")){
            replace("bold", true);
        } else {
            put("bold", true);
        }

        return this;
    }

    public JsonText italic(){
        if(containsKey("italic")){
            replace("italic", true);
        } else {
            put("italic", true);
        }

        return this;
    }

    public JsonText underlined(){
        if(containsKey("underlined")){
            replace("underlined", true);
        } else {
            put("underlined", true);
        }

        return this;
    }

    public JsonText strikethrough(){
        if(containsKey("strikethrough")){
            replace("strikethrough", true);
        } else {
            put("strikethrough", true);
        }

        return this;
    }

    public JsonText clickEvent(ClickEvent eventType, String value){
        JSONObject clickObject = new JSONObject();
        clickObject.put("action", eventType.name().toLowerCase());
        clickObject.put("value", value);

        if(containsKey("clickEvent")){
            replace("clickEvent", clickObject);
        } else {
            put("clickEvent", clickObject);
        }

        return this;
    }

    public JsonText hoverEvent(HoverEvent eventType, String value){
        JSONObject hoverObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject textObject = new JSONObject();
        JSONObject extraObject = new JSONObject();

        hoverObject.put("action", eventType.name().toLowerCase());
        hoverObject.put("value", textObject);

        textObject.put("text", "");
        textObject.put("extra", jsonArray);

        extraObject.put("text", value);

        jsonArray.add(extraObject);

        if(containsKey("hoverEvent")){
            replace("hoverEvent", hoverObject);
        } else {
            put("hoverEvent", hoverObject);
        }

        return this;
    }
}


