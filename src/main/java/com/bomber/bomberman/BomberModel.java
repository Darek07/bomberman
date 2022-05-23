package com.bomber.bomberman;

import javafx.geometry.Point2D;

import javafx.fxml.FXML;
import java.io.*;

import java.util.*;

public class BomberModel {
    public enum CellValue {
        EMPTY, BREAKABLEWALL, UNBREAKABLEWALL, PLAYER
    }
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    @FXML private int rowCount;
    @FXML private int columnCount;
    private CellValue[][] grid;
    private int score;
    private static boolean gameOver;
    private static boolean youWon;

    public BomberModel() {
        this.startNewGame();
    }

    public void initializeLevel(String mapFile) {

        File file = new File(mapFile);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open map file");
        }

        columnCount = scanner.nextLine().length();
        rowCount++;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            rowCount++;
        }

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid = new CellValue[rowCount][columnCount];
        for (int row = 0; scanner.hasNextLine(); row++){
            String line= scanner.nextLine();
            for (int column = 0; column < line.length(); column++){
                CellValue cell;
                switch (line.charAt(column)) {
                    case '#':
                        cell = CellValue.UNBREAKABLEWALL;
                        break;
                    case '&':
                        cell = CellValue.BREAKABLEWALL;
                        break;
                    case 'P':
                        cell = CellValue.PLAYER;
                        break;
                    case '%':
                    default:
                        cell = CellValue.EMPTY;
                        break;
                }
                grid[row][column] = cell;
            }
        }
    }

    public void startNewGame() {
        this.gameOver = false;
        this.youWon = false;
        rowCount = 0;
        columnCount = 0;
        score = 0;
        initializeLevel(Controller.getLevelFile(0));
    }

    private class Player {

        private Point2D playerLocation;
        private Point2D playerVelocity;
        private Direction playerDirection;

        // todo player's moving
        public void move() {}
    }

    public void step() {}


    public static boolean isYouWon() {
        return youWon;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public CellValue[][] getGrid() {
        return grid;
    }

    public CellValue getCellValue(int row, int column) {
        assert row >= 0 && row < this.grid.length && column >= 0 && column < this.grid[0].length;
        return this.grid[row][column];
    }

    public static Direction getCurrentDirection() {
        return null;
    }

    public void setCurrentDirection(Direction direction) {}

    public int getScore() {
        return score;
    }

    public void addToScore(int points) {
        this.score += points;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

}
