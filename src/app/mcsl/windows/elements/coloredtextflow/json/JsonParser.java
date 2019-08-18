package app.mcsl.windows.elements.coloredtextflow.json;

import app.mcsl.managers.logging.Logger;
import app.mcsl.windows.elements.MouseTooltip;
import app.mcsl.windows.elements.coloredtextflow.ChatColor;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class JsonParser {

    private String json;
    private JSONParser jsonParser;

    public JsonParser(String json) {
        this.json = json;
        jsonParser = new JSONParser();
    }

    public Text[] parse() throws ParseException {
        JSONArray textArray = (JSONArray) jsonParser.parse(json);
        Text[] texts = new Text[textArray.size()];

        for(int i = 0; i < textArray.size(); i++){
            Object o = textArray.get(i);
            if(o instanceof JSONObject) {
                JSONObject textObject = (JSONObject) o;
                texts[i] = getStyledText(textObject);
            }
        }
        return texts;
    }

    private Text getStyledText(JSONObject textObject) {
        Text text = new Text(textObject.get("text").toString());

        FontWeight fontWeight = FontWeight.NORMAL;
        FontPosture fontPosture = FontPosture.REGULAR;
        for(Object o : textObject.keySet()){
            String key = o.toString();
            switch (key){
                case "color":
                    text.setFill(ChatColor.valueOf(textObject.get("color").toString().toUpperCase()).getColor());
                    break;
                case "italic":
                    if((boolean)textObject.get("italic")) fontPosture = FontPosture.ITALIC;
                    break;
                case "bold":
                    if((boolean)textObject.get("bold")) fontWeight = FontWeight.BOLD;
                    break;
                case "underlined":
                    if((boolean)textObject.get("underlined")) text.setUnderline(true);
                    break;
                case "strikethrough":
                    if((boolean)textObject.get("strikethrough")) text.setStrikethrough(true);
                    break;
                case "clickEvent":
                    JSONObject clickObject = (JSONObject) textObject.get("clickEvent");
                    String clickAction = clickObject.get("action").toString();
                    String clickValue = clickObject.get("value").toString();
                    text.setOnMouseClicked(e -> {
                        switch (clickAction){
                            case "run_command":
                                //run command in console
                                break;
                            case "suggest_command":
                                //write text into input field
                                break;
                            case "open_url":
                                try {
                                    Desktop.getDesktop().browse(URI.create(clickValue));
                                } catch (IOException ex) {
                                    Logger.exception(ex);
                                }
                                break;
                            case "change_page":
                                //change page in launcher
                                break;
                        }
                    });
                    text.setCursor(Cursor.HAND);
                    break;
                case "hoverEvent":
                    JSONObject hoverObject = (JSONObject) textObject.get("hoverEvent");
                    String hoverAction = hoverObject.get("action").toString();
                    if (hoverAction.equalsIgnoreCase("show_text")) {
                        JSONObject hoverValueObject = (JSONObject) hoverObject.get("value");
                        JSONArray hoverExtraArray = (JSONArray) hoverValueObject.get("extra");
                        StringBuilder hoverTextBuilder = new StringBuilder();
                        for(Object ho : hoverExtraArray){
                            JSONObject hoText = (JSONObject) ho;
                            hoverTextBuilder.append(hoText.get("text").toString());
                        }
                        MouseTooltip.install(hoverTextBuilder.toString(), text);
                    }
                    break;
            }
        }
        text.setFont(Font.font(null, fontWeight, fontPosture, 13));

        return text;
    }

}
