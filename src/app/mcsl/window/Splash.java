package app.mcsl.window;

import app.mcsl.MainClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Splash extends Stage {

    public Splash() {
        this.setTitle("Loading...");
        this.getIcons().add(new Image("/app/mcsl/resource/favicon.png"));

        Image mcslLogo = new Image("/app/mcsl/resource/favicon.png", 100, 100, false, false);

        Label title = new Label("Setting up...");
        title.setStyle("-fx-font-size: 24px;");

        Line line = new Line();
        line.setStartX(20);
        line.setEndX(280);
        line.setStrokeWidth(3);

        Line line2 = new Line();
        line2.setStartX(20);
        line2.setEndX(280);
        line2.setStrokeWidth(3);

        Label versionLabel = new Label("MCSL v" + MainClass.VERSION);
        versionLabel.setStyle("-fx-font-size: 20px");

        VBox mcslBox = new VBox(new ImageView(mcslLogo), versionLabel);
        mcslBox.setAlignment(Pos.TOP_CENTER);

        Region region1 = new Region();
        VBox.setVgrow(region1, Priority.ALWAYS);

        Region region2 = new Region();
        VBox.setVgrow(region2, Priority.ALWAYS);

        VBox body = new VBox(5, title, line, region1, mcslBox, region2, new Label("Copyright \u00A9 " + new SimpleDateFormat("YYYY").format(new Date())), line2);
        body.setAlignment(Pos.TOP_CENTER);
        body.setPadding(new Insets(5, 0, 20, 0));
        body.setMaxHeight(Double.MAX_VALUE);

        Scene scene = new Scene(body, 300, 350);
        scene.getStylesheets().add(getClass().getResource("/app/mcsl/window/style/splashstyle.css").toExternalForm());

        this.setScene(scene);
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
    }
}
