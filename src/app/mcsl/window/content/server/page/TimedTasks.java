package app.mcsl.window.content.server.page;

import app.mcsl.event.TimedTaskExecuteEvent;
import app.mcsl.manager.Language;
import app.mcsl.manager.file.FileManager;
import app.mcsl.manager.mainside.timedtask.TimedTask;
import app.mcsl.manager.mainside.timedtask.TimedTasksManager;
import app.mcsl.util.DataTypeUtil;
import app.mcsl.util.DateTimeUtils;
import app.mcsl.window.Template;
import app.mcsl.window.content.server.Server;
import app.mcsl.window.element.GroupBox;
import app.mcsl.window.element.IconCard;
import app.mcsl.window.element.Table;
import app.mcsl.window.element.button.Button;
import app.mcsl.window.element.button.ButtonType;
import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelColor;
import app.mcsl.window.element.label.LabelType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class TimedTasks extends ScrollPane {

    private Table timedTasksTable;
    private List<TimedTask> timedTaskList = new ArrayList<>();

    private TextField nameField, commandField, yearField, monthField, dayField, hourField, minuteField, secondField;
    private CheckBox dailyCheckBox;

    public TimedTasks(Server server) {
        nameField = new TextField();
        nameField.setMaxWidth(100);
        nameField.setPromptText(Language.getText("id"));

        commandField = new TextField();
        commandField.setMinWidth(590);
        commandField.setPromptText(Language.getText("command"));

        HBox addBoxFirstRow = new HBox(10, nameField, commandField);
        addBoxFirstRow.setAlignment(Pos.CENTER);
        addBoxFirstRow.setMaxWidth(700);
        addBoxFirstRow.setPadding(new Insets(10, 10, 0, 10));

        yearField = new TextField();
        yearField.setMaxWidth(100);
        yearField.setPromptText(Language.getText("year"));

        monthField = new TextField();
        monthField.setMaxWidth(100);
        monthField.setPromptText(Language.getText("month"));

        dayField = new TextField();
        dayField.setMaxWidth(100);
        dayField.setPromptText(Language.getText("day"));

        hourField = new TextField();
        hourField.setMaxWidth(100);
        hourField.setPromptText(Language.getText("hour"));

        minuteField = new TextField();
        minuteField.setMaxWidth(100);
        minuteField.setPromptText(Language.getText("minute"));

        secondField = new TextField();
        secondField.setMaxWidth(100);
        secondField.setPromptText(Language.getText("second"));

        HBox dateBox = new HBox(5, yearField, new Label("-", LabelType.H1), monthField, new Label("-", LabelType.H1), dayField, new Label(" ", LabelType.H1),
                hourField, new Label(":", LabelType.H1), minuteField, new Label(":", LabelType.H1), secondField);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPadding(new Insets(0, 10, 0, 10));
        dateBox.setMaxWidth(700);

        dailyCheckBox = new CheckBox(Language.getText("daily"));
        dailyCheckBox.setPadding(new Insets(0, 10, 20, 10));

        Button addButton = new Button(Language.getText("add"), ButtonType.ACTION_BUTTON);
        addButton.setMaxHeight(30);
        addButton.setOnAction(e -> {
            if (!nameField.getText().isEmpty() && !commandField.getText().isEmpty() &&
                    ((!dailyCheckBox.isSelected() && !yearField.getText().isEmpty() && DataTypeUtil.isInt(yearField.getText()) &&
                            !monthField.getText().isEmpty() && DataTypeUtil.isInt(monthField.getText()) &&
                            !dayField.getText().isEmpty() && DataTypeUtil.isInt(dayField.getText()) &&
                            !hourField.getText().isEmpty() && DataTypeUtil.isInt(hourField.getText()) &&
                            !minuteField.getText().isEmpty() && DataTypeUtil.isInt(minuteField.getText()) &&
                            !secondField.getText().isEmpty() && DataTypeUtil.isInt(secondField.getText())) ||
                            (dailyCheckBox.isSelected() && !hourField.getText().isEmpty() && DataTypeUtil.isInt(hourField.getText()) &&
                                    !minuteField.getText().isEmpty() && DataTypeUtil.isInt(minuteField.getText()) &&
                                    !secondField.getText().isEmpty() && DataTypeUtil.isInt(secondField.getText())))) {
                if (!TimedTasksManager.isExistsInServer(nameField.getText())) {
                    String date = (!dailyCheckBox.isSelected() ? yearField.getText() + "-" + monthField.getText() + "-" + dayField.getText() + " " : "") + hourField.getText() + ":" + minuteField.getText() + ":" + secondField.getText();
                    if (dailyCheckBox.isSelected() || (!DateTimeUtils.lateDate(date) && !DateTimeUtils.lateTime(date))) {
                        TimedTask timedTask = new TimedTask(nameField.getText(), server.getName(), date, commandField.getText(), dailyCheckBox.isSelected());
                        addTimedTask(timedTask);
                        TimedTasksManager.createTimedTask(timedTask);
                        nameField.clear();
                        commandField.clear();
                        yearField.clear();
                        monthField.clear();
                        dayField.clear();
                        hourField.clear();
                        minuteField.clear();
                        secondField.clear();
                        dailyCheckBox.setSelected(false);
                    } else {
                        Template.showNotification(Language.getText("latedate"), LabelColor.ERROR);
                    }
                } else {
                    Template.showNotification(Language.getText("timedtaskidexists"), LabelColor.ERROR);
                }
            } else {
                Template.showNotification(Language.getText("mustfillallfields"), LabelColor.ERROR);
            }
        });

        GroupBox addBox = new GroupBox(Language.getText("addtimedtask"));
        HBox.setHgrow(addBox.getBody(), Priority.ALWAYS);
        addBox.getBody().setSpacing(5);
        addBox.addAll(addBoxFirstRow, dateBox, dailyCheckBox, addButton);
        addBox.getBody().setAlignment(Pos.CENTER);

        timedTasksTable = new Table();
        timedTasksTable.setAlignment(Pos.CENTER);
        timedTasksTable.addColumn(Language.getText("id"), Language.getText("date"), Language.getText("command"), Language.getText("daily"), Language.getText("delete"));

        GroupBox tasksBox = new GroupBox(Language.getText("timedtasks"));
        tasksBox.add(timedTasksTable);
        tasksBox.getBody().setPadding(new Insets(10));

        javafx.scene.control.Label timedtaskdescriptionLabel = new javafx.scene.control.Label(Language.getText("timedtaskdescription"));
        timedtaskdescriptionLabel.maxWidthProperty().bind(Template.getStage().widthProperty());
        timedtaskdescriptionLabel.setStyle("-fx-text-fill: -fx-themetypecolor;\n" +
                "    -fx-font-size: 15px;\n" +
                "    -fx-font-weight: bold;");
        timedtaskdescriptionLabel.setWrapText(true);

        IconCard timedTaskIconCard = new IconCard(new ImageView(FileManager.TIP_ICON), timedtaskdescriptionLabel, 200, 50);
        timedTaskIconCard.prefWidthProperty().bind(Template.getStage().widthProperty());

        VBox body = new VBox();
        body.setSpacing(20);
        body.getChildren().addAll(timedTaskIconCard, addBox, tasksBox);

        setContent(body);
        setFitToWidth(true);

        for (TimedTask timedTask : TimedTasksManager.getServersTimedTasks(server.getName())) {
            addTimedTask(timedTask);
        }

        TimedTaskExecuteEvent.addListener(timedTask -> {
            if (timedTask.getServerName().equalsIgnoreCase(server.getName()) && !timedTask.isDaily()) {
                removeTimedTask(timedTask);
            }
        });
    }

    public void addTimedTask(TimedTask timedTask) {
        Button deleteButton = new Button("", ButtonType.ROUNDED_ERROR);
        deleteButton.setGraphic(new ImageView(FileManager.DELETE_ICON));
        deleteButton.setOnAction(e -> {
            removeTimedTask(timedTask);
            TimedTasksManager.removeTimedTask(timedTask);
        });

        timedTasksTable.addRow(timedTask.getName(), timedTask.getDateString(), timedTask.getCommand(), (timedTask.isDaily() ? Language.getText("yes") : Language.getText("no")), deleteButton);

        timedTaskList.add(timedTask);
    }

    public void removeTimedTask(TimedTask timedTask) {
        for (int i = 0; i < timedTaskList.size(); i++) {
            if (timedTaskList.get(i).equals(timedTask)) {
                timedTasksTable.deleteRow(i + 1);
            }
        }
        timedTaskList.remove(timedTask);
    }

}
