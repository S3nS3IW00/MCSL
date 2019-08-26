package app.mcsl.windows.elements;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ResponsiveGridPane extends GridPane {

    private int gridSize;
    private List<Node> items = new ArrayList<>();
    private ScrollPane parent;

    private double lastWidth = 0.0;
    private int lastColumnCount = 0;

    public ResponsiveGridPane(int gridSize) {
        this.gridSize = gridSize;
        VBox.setVgrow(this, Priority.ALWAYS);

        setPadding(new Insets(10));
        setHgap(20);
        setVgap(20);

        /*getChildren().addListener((ListChangeListener<Node>) c -> {
            c.next();
            if(c.getTo() > 0) calculateNewSize();
        });*/
    }

    public void calculateNewSize() {
        double width = parent.getWidth();
        int columnCount = (int) (width - (lastColumnCount * 40)) / gridSize;
        lastWidth = width;

        if (columnCount != 0) {
            lastColumnCount = columnCount;
            getChildren().clear();

            int rowIndex = 0;
            int columnIndex = 0;
            for (int i = 0; i < items.size(); i++) {
                if (i % columnCount == 0) {
                    rowIndex++;
                    columnIndex = 0;
                }
                add(items.get(i), columnIndex, rowIndex);
                columnIndex++;
            }
        }
    }

    public void addItem(Node item) {
        items.add(item);
        calculateNewSize();
    }

    public void removeItem(Node item) {
        items.remove(item);
        calculateNewSize();
    }

    public void setParent(ScrollPane parent) {
        this.parent = parent;
    }
}
