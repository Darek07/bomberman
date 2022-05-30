package com.bomber.bomberman;

import javafx.geometry.Point2D;

import javafx.fxml.FXML;
import java.io.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BomberModel {
    public enum CellValue {
        EMPTY, BREAKABLEWALL, UNBREAKABLEWALL, PLAYER
    }
    public enum Direction {
        UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0), NONE(0, 0);
        final Point2D velocity;

        Direction(int x, int y) {
            this.velocity = new Point2D(x, y);
        }
    }

    @FXML private int rowCount;
    @FXML private int columnCount;
    private CellValue[][] grid;
    private int score;
    private static boolean gameOver;
    private static boolean youWon;
    private List<Player> players = new ArrayList<>(3);

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
        Collections.addAll(players, new Player(20, 9));
    }

    private class Player {

        private Point2D playerLocation;
        private Integer playerSpeed;
        private Direction playerDirection;
        private boolean isMoving;
        private ScheduledExecutorService executor;

        public Player(int col, int row) {
            this.playerLocation = new Point2D(col, row);
            this.playerSpeed = 20;
            this.playerDirection = Direction.NONE;
            this.isMoving = false;
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                if (isMoving) {
                    grid[(int)playerLocation.getY()][(int)playerLocation.getX()] = CellValue.EMPTY;
                    System.out.println(grid[(int)playerLocation.getY()][(int)playerLocation.getX()]);
                    this.playerLocation = this.playerLocation.add(playerDirection.velocity);
                    grid[(int)playerLocation.getY()][(int)playerLocation.getX()] = CellValue.PLAYER;
                    System.out.println(grid[(int)playerLocation.getY()][(int)playerLocation.getX()]);
                }
            }, 0, 1000 / playerSpeed, TimeUnit.MILLISECONDS);
        }

        public void setPlayerDirection(Direction playerDirection, boolean isMoving) {
            this.playerDirection = playerDirection;
            this.isMoving = isMoving;
        }

        // todo player's moving
        public void move() {}
    }

    public void step() {}

    public void setMoving(Direction direction, int player, boolean isMove) {
        players.get(player).setPlayerDirection(direction, isMove);
    }

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
