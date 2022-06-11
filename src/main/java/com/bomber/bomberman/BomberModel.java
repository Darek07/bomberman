package com.bomber.bomberman;

import javafx.geometry.Point2D;

import javafx.fxml.FXML;
import java.io.*;

import java.util.*;

public class BomberModel {

    @FXML private int rowCount;
    @FXML private int columnCount;
    private CellValue[][] grid;
    private int score;
    private static boolean gameOver;
    private static boolean youWon;
    private final List<Player> players = new ArrayList<>(3);

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
                        cell = CellValue.EMPTY;
                        players.add(new Player(this, players.size(), column, row));
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

    public void step() {}

    public void setBomb(int playerID, Controller controller) {
        Player player = players.get(playerID);
        new Bomb(this, player, controller);
    }

    public void setMoving(Direction direction, int player, boolean isMove) {
        if (player >= players.size()) return;
        players.get(player).setPlayerDirection(direction);
        players.get(player).setMoving(isMove);
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

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayerByID(int playerID) {
        for (Player player : players) {
            if (player.getPlayerID() == playerID) {
                return player;
            }
        }
        return null;
    }

}
