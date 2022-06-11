package com.bomber.bomberman;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class BomberView extends Group {
    public final static int CELL_SIZE = 40;
    public final static int PLAYER_SIZE = 27;

    @FXML private int rowCount;
    @FXML private int columnCount;
    private ImageView[][] cellViews;
    private final List<ImageView> playersViews = new ArrayList<>(3);
    private final Image unbreakableWallImage;
    private final Image breakableWallImage;
    private final Image playerImage;
    private final Image bombImage;
    private final Image fireImage;
    private final Image ripImage;

    public BomberView() {
        String base = "img/";
        this.unbreakableWallImage = new Image(getClass().getResourceAsStream(base + "unbreakablewall.jpg"));
        this.breakableWallImage = new Image(getClass().getResourceAsStream(base + "breakablewall.jpg"));
        this.playerImage = new Image(getClass().getResourceAsStream(base + "player.gif"));
        this.bombImage = new Image(getClass().getResourceAsStream(base + "bomb.png"));
        this.fireImage = new Image(getClass().getResourceAsStream(base + "fire.png"));
        this.ripImage = new Image(getClass().getResourceAsStream(base + "rip.png"));
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

    public void initializePlayersViews(BomberModel model) {
        playersViews.forEach(playerView -> playerView.setImage(null));
        playersViews.clear();
        for (int i = 0; ; i++) {
            Player player = model.getPlayerByID(i);
            if (player == null) {
                break;
            }
            ImageView imageView = new ImageView();
            imageView.setFitWidth(PLAYER_SIZE);
            imageView.setFitHeight(PLAYER_SIZE);
            imageView.setImage(this.playerImage);
            playersViews.add(imageView);
        }
        this.getChildren().addAll(playersViews);
    }

    public void updatePlayersViews(BomberModel model) {
        if (playersViews.size() == 0) {
            initializePlayersViews(model);
        }
        for (int i = 0; i < playersViews.size(); i++) {
            Player player = model.getPlayerByID(i);
            if (player == null) {
                playersViews.get(i).setImage(null);
                continue;
            }
            Point2D location = player.getPlayerLocation();
            playersViews.get(i).setX(location.getX());
            playersViews.get(i).setY(location.getY());
        }
    }

    public void updateGridViews(BomberModel model) {
        assert model.getRowCount() == this.rowCount && model.getColumnCount() == this.columnCount;
        for (int row = 0; row < this.rowCount; row++){
            for (int column = 0; column < this.columnCount; column++){
                CellValue value = model.getCellValue(row, column);
                switch (value) {
                    case BREAKABLEWALL -> this.cellViews[row][column].setImage(this.breakableWallImage);
                    case UNBREAKABLEWALL -> this.cellViews[row][column].setImage(this.unbreakableWallImage);
                    case BOMB -> this.cellViews[row][column].setImage(this.bombImage);
                    case FIRE -> this.cellViews[row][column].setImage(this.fireImage);
                    case RIP -> this.cellViews[row][column].setImage(this.ripImage);
                    default -> this.cellViews[row][column].setImage(null);
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
