package app.mcsl.windows.elements.notifications;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification extends MenuItem {

    private String title, text, date;
    private NotificationAlertType type;
    private SimpleDateFormat notifDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Notification(String title, String text, NotificationAlertType type) {
        this.title = title;
        this.text = text;
        this.date = notifDateFormat.format(new Date());
        this.type = type;

        Label titleLabel = new Label(title);
        titleLabel.setId("notification-title");

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setId("notification-text");

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        Label dateLabel = new Label(date);
        dateLabel.setId("notification-date-text");

        HBox titleBox = new HBox(titleLabel, region, dateLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(5, titleBox, textLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox bodyBox = new HBox(5, new ImageView(type.getIcon()), textBox);
        bodyBox.setId("notification-body");

        setGraphic(bodyBox);
    }

    public Notification(String title, String text, String date, NotificationAlertType type) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.type = type;

        Label titleLabel = new Label(title);
        titleLabel.setId("notification-title");

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setId("notification-text");

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        Label dateLabel = new Label(date);
        dateLabel.setId("notification-date-text");

        HBox titleBox = new HBox(titleLabel, region, dateLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(5, titleBox, textLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox bodyBox = new HBox(5, new ImageView(type.getIcon()), textBox);
        bodyBox.setId("notification-body");

        setGraphic(bodyBox);
    }

    public String getTitle() {
        return title;
    }

    public String getNotifText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public NotificationAlertType getType() {
        return type;
    }
}
