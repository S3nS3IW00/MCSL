package app.mcsl.window.element;

import app.mcsl.window.element.label.Label;
import app.mcsl.window.element.label.LabelType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;

public class Table extends GridPane {

    private int columnCount = 0, rowCount = 1;

    public Table() {
        setPadding(new Insets(10));
        VBox.setVgrow(this, Priority.ALWAYS);
        HBox.setHgrow(this, Priority.ALWAYS);
        setHgap(20);
        setVgap(5);
    }

    public void addColumn(String columnText) {
        Node columnNode = new Label(columnText, LabelType.H3);
        add(columnNode, columnCount, 0);
        GridPane.setHalignment(columnNode, HPos.CENTER);
        GridPane.setValignment(columnNode, VPos.CENTER);
        columnCount++;
    }

    public void addColumn(Node columnNode) {
        add(columnNode, columnCount, 0);
        GridPane.setHalignment(columnNode, HPos.CENTER);
        GridPane.setValignment(columnNode, VPos.CENTER);
        columnCount++;
    }

    public void addColumn(String... columnTexts) {
        for (String columnText : columnTexts) {
            Node columnNode = new Label(columnText, LabelType.H3);
            add(columnNode, columnCount, 0);
            GridPane.setHalignment(columnNode, HPos.CENTER);
            GridPane.setValignment(columnNode, VPos.CENTER);
            columnCount++;
        }
    }

    public void addColumn(Node... columnNodes) {
        for (Node columnNode : columnNodes) {
            add(columnNode, columnCount, 0);
            GridPane.setHalignment(columnNode, HPos.CENTER);
            GridPane.setValignment(columnNode, VPos.CENTER);
            columnCount++;
        }
    }

    public void addRow(Object... rowNodes) {
        for (int i = 0; i < columnCount; i++) {
            if (rowNodes[i] instanceof Node) {
                add((Node) rowNodes[i], i, rowCount);
                GridPane.setHalignment((Node) rowNodes[i], HPos.CENTER);
                GridPane.setValignment((Node) rowNodes[i], VPos.CENTER);
            } else if (rowNodes[i] instanceof String) {
                Node columnNode = new Label(rowNodes[i].toString(), LabelType.DEFAULT, 13);
                add(columnNode, i, rowCount);
                GridPane.setHalignment(columnNode, HPos.CENTER);
                GridPane.setValignment(columnNode, VPos.CENTER);
            }
        }
        rowCount++;
    }

    public void deleteRow(int row) {
        if (row == 0) return;
        int defaultRowSpan = 1;
        Set<Node> deleteNodes = new HashSet<>();
        for (Node child : getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            if (rowIndex == row) {
                deleteNodes.add(child);
            }
        }
        getChildren().removeAll(deleteNodes);
        int i = row;
        for (Node child : getChildren()) {
            int nodeRowIndex = GridPane.getRowIndex(child);
            if (nodeRowIndex == (i + (defaultRowSpan * 2))) {
                i += defaultRowSpan;
            }
            if (nodeRowIndex > i) {
                GridPane.setRowIndex(child, i);
            }
        }
        rowCount--;
    }

    public void deleteAllRow() {
        for (int i = rowCount - 1; i > 0; i--) {
            deleteRow(i);
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }
}
