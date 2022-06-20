package com.bomber.bomberman;

import javafx.fxml.FXML;

import java.io.*;

import java.util.*;
import java.util.stream.Stream;

public class BomberModel {

    private final List<Player> alivePlayers = new ArrayList<>(Controller.PLAYERS_NUMBER);
    private List<Player> ripPlayers = new ArrayList<>(Controller.PLAYERS_NUMBER);
    @FXML private int rowCount;
    @FXML private int columnCount;
    private CellValue[][] grid;

    public BomberModel() {
        this.startNewGame();
    }

    public void startNewGame() {
        rowCount = 0;
        columnCount = 0;
        initializeMap(Controller.getMapFile());
    }

    public void initializeMap(String mapFile) {
        System.out.println(mapFile);
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
                        if (alivePlayers.size() < Controller.PLAYERS_NUMBER) {
                            alivePlayers.add(new Player(this, alivePlayers.size(), column, row));
                        }
                    }
                    default -> cell = CellValue.EMPTY;
                }
                grid[row][column] = cell;
            }
        }
    }

    public Player getPlayerByID(int playerID) {
        return Stream.of(alivePlayers, ripPlayers)
                .flatMap(Collection::stream)
                .filter(player -> player.getPlayerID() == playerID)
                .findFirst()
                .orElse(null);
    }

    public void addRipPlayers(List<Player> ripPlayers) {
        ripPlayers.forEach(player -> {
            if (!this.ripPlayers.contains(player)){
                this.ripPlayers.add(player);
                player.setDied(true);
            }
        });
        this.alivePlayers.removeAll(ripPlayers);
        System.out.println(this.ripPlayers.size());
    }

    public void restoreData() {
        alivePlayers.addAll(ripPlayers);
        ripPlayers.clear();
        alivePlayers.forEach(Player::restoreInitialValues);
    }

    public Player determineRoundWinner() {
        Player player = (alivePlayers.size() == 1 ? alivePlayers.get(0) : null);
        if (player != null) {
            player.increaseWins();
            ripPlayers.add(player);
            alivePlayers.remove(player);
        }
        return player;
    }

    public boolean isAnyBombAppears() {
        return Arrays.stream(grid).flatMap(Arrays::stream).anyMatch(cell -> cell == CellValue.RIP || cell == CellValue.FIRE);
    }

    public Player getWinner() {
        return Stream.of(alivePlayers, ripPlayers)
                .flatMap(Collection::stream).max(Comparator.comparingInt(Player::getWins))
                .orElse(null);
    }

    public void setBomb(Player player) {
        new Bomb(this, player);
    }

    public void setPlayerMove(Player player, Direction direction, boolean isMove) {
        player.setPlayerDirectionAndMove(direction, isMove);
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

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public List<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public int getNumAlivePlayers() {
        return alivePlayers.size();
    }
}
