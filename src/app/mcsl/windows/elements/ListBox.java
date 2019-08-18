package app.mcsl.windows.elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

public class ListBox extends ScrollPane {

    private VBox body;
    private Map<String, HBox> buttonBoxes = new HashMap<>();
    private int width, height, fontSize, boxSize;

    public ListBox(int width, int height){
        this.width = width;
        this.height = height;
        body = new VBox();
        body.setPrefWidth(width);
        setPrefHeight(height);
        setMaxWidth(width);
        setContent(body);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    }

    public ListBox(int width, int height, int fontSize, int boxSize){
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;
        this.boxSize = boxSize;
        body = new VBox();
        body.setPrefWidth(width);
        setPrefHeight(height);
        setMaxWidth(width);
        setContent(body);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    }

    public ListBox add(Image icon, String title){
        Label titleText = new Label(title);
        titleText.setStyle("-fx-text-fill: -fx-themetypecolor;-fx-font-weight: bold;");
        titleText.setFont(Font.font(fontSize != 0 ? fontSize : 15));
        titleText.setPrefHeight(boxSize != 0 ? boxSize : 30);
        titleText.setPadding(new Insets(0, 0, 0, 5));
        if(icon != null) titleText.setGraphic(new ImageView(icon));

        VBox cardBox = new VBox(titleText);
        cardBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cardBox, Priority.ALWAYS);
        cardBox.setAlignment(Pos.CENTER_LEFT);
        cardBox.setId("list-item");
        body.getChildren().add(cardBox);
        return this;
    }

    public ListBox add(ImageView icon, String title){
        Label titleText = new Label(title);
        titleText.setStyle("-fx-text-fill: -fx-themetypecolor;-fx-font-weight: bold;");
        titleText.setFont(Font.font(fontSize != 0 ? fontSize : 15));
        titleText.setPrefHeight(boxSize != 0 ? boxSize : 30);
        titleText.setPadding(new Insets(0, 0, 0, 5));
        if(icon != null) titleText.setGraphic(icon);

        VBox cardBox = new VBox(titleText);
        cardBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cardBox, Priority.ALWAYS);
        cardBox.setAlignment(Pos.CENTER_LEFT);
        cardBox.setId("list-item");
        body.getChildren().add(cardBox);
        return this;
    }

    public VBox getBody() {
        return body;
    }

    public Map<String, HBox> getButtonBoxes(){
        return buttonBoxes;
    }

    public void clear(){
        getBody().getChildren().clear();
        getButtonBoxes().clear();
    }

    public VBox getCardBox(String title){
        for(Node box : getBody().getChildren()){
            VBox vBox = (VBox) box;
            if(((Label)vBox.getChildren().get(0)).getText().equalsIgnoreCase(title)){
                return vBox;
            }
        }
        return null;
    }

    public boolean isCardBoxExists(String title){
        return getCardBox(title) != null;
    }

    public HBox getButtonBox(String title){
        return buttonBoxes.get(title);
    }

    public Label getCardBoxLabel(String title){
        return (Label)getCardBox(title).getChildren().get(0);
    }
}
