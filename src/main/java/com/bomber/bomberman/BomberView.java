package com.bomber.bomberman;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.bomber.bomberman.BomberModel.CellValue;

public class BomberView extends Group {
    public final static int CELL_SIZE = 40;

    @FXML private int rowCount;
    @FXML private int columnCount;
    private ImageView[][] cellViews;
    private final Image unbreakableWallImage;
    private final Image breakableWallImage;
    private final Image playerImage;

    public BomberView() {
        String base = "img/";
        this.unbreakableWallImage = new Image(getClass().getResourceAsStream(base + "unbreakablewall.jpg"));
        this.breakableWallImage = new Image(getClass().getResourceAsStream(base + "breakablewall.jpg"));
        this.playerImage = new Image(getClass().getResourceAsStream(base + "player.gif"));
    }

    private void initializeGrid() {
        if (this.rowCount > 0 && this.columnCount > 0) {
            this.cellViews = new ImageView[this.rowCount][this.columnCount];
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    ImageView imageView = new ImageView();
                    imageView.setX(column * CELL_SIZE);
                    imageView.setY(row * CELL_SIZE);
                    imageView.setFitWidth(CELL_SIZE);
                    imageView.setFitHeight(CELL_SIZE);
                    this.cellViews[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    public void update(BomberModel model) {
        assert model.getRowCount() == this.rowCount && model.getColumnCount() == this.columnCount;
        for (int row = 0; row < this.rowCount; row++){
            for (int column = 0; column < this.columnCount; column++){
                CellValue value = model.getCellValue(row, column);
                switch (value) {
                    case BREAKABLEWALL:
                        this.cellViews[row][column].setImage(this.breakableWallImage);
                        break;
                    case UNBREAKABLEWALL:
                        this.cellViews[row][column].setImage(this.unbreakableWallImage);
                        break;
                    case PLAYER:
                        this.cellViews[row][column].setImage(this.playerImage);
                        break;
                    default:
                        this.cellViews[row][column].setImage(null);
                        break;
                }
            }
        }
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.initializeGrid();
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        this.initializeGrid();
    }
}
