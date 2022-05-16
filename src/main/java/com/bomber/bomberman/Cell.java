package com.bomber.bomberman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {

    public Cell(CellType type, int x, int y) {
        setWidth(Bomber.CELL_SIZE);
        setHeight(Bomber.CELL_SIZE);

        relocate(x * Bomber.CELL_SIZE, y * Bomber.CELL_SIZE);

        Color color = null;
        switch (type) {
            case FLOOR:
                color = Color.valueOf("#feb");
                break;
            case BREAKABLE:
                color = Color.valueOf("#582");
                break;
            case UNBREAKABLE:
                color = Color.valueOf("#000");
                break;
        }
        setFill(color);

        setOnMousePressed(e -> {

        });
    }
}
