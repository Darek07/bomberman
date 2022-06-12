package com.bomber.bomberman;

import javafx.fxml.FXML;
import java.io.*;

import java.util.*;

public class BomberModel {

    private final List<Player> players = new ArrayList<>(Controller.PLAYERS_NUMBER);
    @FXML private int rowCount;
    @FXML private int columnCount;
    private CellValue[][] grid;
    private int score;
    private static boolean gameOver;
    private static boolean youWon;

    public BomberModel() {
        this.startNewGame();
    }

    public void startNewGame() {
        this.gameOver = false;
        this.youWon = false;
        rowCount = 0;
        columnCount = 0;
        score = 0;
        initializeMap(Controller.getMapFile());
    }

    public void initializeMap(String mapFile) {

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
            String line = scanner.nextLine();
            for (int column = 0; column < line.length(); column++){
                CellValue cell;
                switch (line.charAt(column)) {
                    case '#' -> cell = CellValue.UNBREAKABLEWALL;
                    case '&' -> cell = CellValue.BREAKABLEWALL;
                    case 'P' -> {
                        cell = CellValue.EMPTY;
                        if (players.size() < Controller.PLAYERS_NUMBER) {
                            players.add(new Player(this, players.size(), column, row));
                        }
                    }
                    default -> cell = CellValue.EMPTY;
                }
                grid[row][column] = cell;
            }
        }
    }

    public void setBomb(Player player) {
        new Bomb(this, player);
    }

    public void setPlayerMove(Player player, Direction direction, boolean isMove) {
        player.setPlayerDirectionAndMove(direction, isMove);
    }

    public static boolean isYouWon() {
        return youWon;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public CellValue getCellValue(int row, int column) {
        return (row < 0 || column < 0 || row >= this.grid.length || column >= this.grid[0].length)
                ? null
                : this.grid[row][column];
    }

    public void setCellValue(CellValue cellValue, int row, int column) {
        if (row < 0 || column < 0 || row >= this.grid.length || column >= this.grid[0].length) {
            return;
        }
        grid[row][column] = cellValue;
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
