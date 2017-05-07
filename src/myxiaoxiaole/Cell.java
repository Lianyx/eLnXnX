package myxiaoxiaole;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by tiberius on 2017/5/1.
 */
public class Cell extends Rectangle{
    private final int cx;
    private final int cy;

    public int getCx() {
        return cx;
    }

    public int getCy() {
        return cy;
    }


    public Cell(int i, int j) {
        this.cx = i;
        this.cy = j;

        setLayoutX(i * GridPanel.CELL_SIZE);
        setLayoutY(j * GridPanel.CELL_SIZE);
        setWidth(GridPanel.CELL_SIZE);
        setHeight(GridPanel.CELL_SIZE);

        setFill(Color.TRANSPARENT);
        setStroke(Color.GREY);
        setArcHeight(GridPanel.CELL_SIZE / 6d);
        setArcWidth(GridPanel.CELL_SIZE / 6d);
    }
}
