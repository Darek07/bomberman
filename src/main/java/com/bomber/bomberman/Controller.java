package com.bomber.bomberman;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Controller extends Thread implements EventHandler<KeyEvent> {
    public static final int PLAYERS_AMOUNT = 3;

    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label gameOverLabel;
    @FXML private BomberView bomberView;
    private BomberModel bomberModel;
    private boolean paused;

    private static final String[] mapFiles = { Controller.class.getResource("map.txt").getFile().substring(3) };
    public static final KeyCode[] PLAYER0KEYS = { KeyCode.LEFT, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.UP, KeyCode.SLASH };
    public static final KeyCode[] PLAYER1KEYS = { KeyCode.A, KeyCode.D, KeyCode.S, KeyCode.W, KeyCode.TAB };
    public static final KeyCode[] PLAYER2KEYS = { KeyCode.H, KeyCode.K, KeyCode.J, KeyCode.U, KeyCode.SPACE };
    public static final Set<KeyCode> LEFT_KEYS;
    public static final Set<KeyCode> RIGHT_KEYS;
    public static final Set<KeyCode> DOWN_KEYS;
    public static final Set<KeyCode> UP_KEYS;
    public static final Set<KeyCode> BOMB_KEYS;

    static {
        LEFT_KEYS = new HashSet<>(PLAYERS_AMOUNT);
        RIGHT_KEYS = new HashSet<>(PLAYERS_AMOUNT);
        DOWN_KEYS = new HashSet<>(PLAYERS_AMOUNT);
        UP_KEYS = new HashSet<>(PLAYERS_AMOUNT);
        BOMB_KEYS = new HashSet<>(PLAYERS_AMOUNT);
        Collections.addAll(LEFT_KEYS, PLAYER0KEYS[0], PLAYER1KEYS[0], PLAYER2KEYS[0]);
        Collections.addAll(RIGHT_KEYS, PLAYER0KEYS[1], PLAYER1KEYS[1], PLAYER2KEYS[1]);
        Collections.addAll(DOWN_KEYS, PLAYER0KEYS[2], PLAYER1KEYS[2], PLAYER2KEYS[2]);
        Collections.addAll(UP_KEYS, PLAYER0KEYS[3], PLAYER1KEYS[3], PLAYER2KEYS[3]);
        Collections.addAll(BOMB_KEYS, PLAYER0KEYS[4], PLAYER1KEYS[4], PLAYER2KEYS[4]);
    }

    public Controller() {
        this.paused = false;
    }

    public void initialize() {
        this.bomberModel = new BomberModel();
        this.bomberView.setBomberModel(bomberModel);
        this.bomberView.initializePlayersViews();
        this.update();

        new AnimationTimer() {
            long lastTime = 0;
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                final double elapsedMicroSeconds = (now - lastTime) / 1_000.0 ;
                if (elapsedMicroSeconds >= 1000) {
                    update();
                    lastTime = now;
                }
            }
        }.start();
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        boolean bomb = false;
        boolean isPressed = keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED);
        KeyCode code = keyEvent.getCode();
        Direction direction = Direction.NONE;
        if (LEFT_KEYS.stream().anyMatch(k -> k == code)) {
            direction = Direction.LEFT;
        }
        else if (RIGHT_KEYS.stream().anyMatch(k -> k == code)) {
            direction = Direction.RIGHT;
        }
        else if (DOWN_KEYS.stream().anyMatch(k -> k == code)) {
            direction = Direction.DOWN;
        }
        else if (UP_KEYS.stream().anyMatch(k -> k == code)) {
            direction = Direction.UP;
        }
        else if (BOMB_KEYS.stream().anyMatch(k -> k == code)) {
            bomb = true;
        }
//        else if (code == KeyCode.R) {
//            pause();
//            bomberModel.startNewGame();
//            gameOverLabel.setText(String.format(""));
//            paused = false;
//        }
        else keyRecognized = false;

        if (keyRecognized) {
            keyEvent.consume();
            int playerID = getPlayerByKey(code);
            Player player = bomberModel.getPlayerByID(playerID);
            if (player == null) {
                return;
            }
            if (bomb && isPressed) {
                bomberModel.setBomb(player);
            }
            else {
                bomberModel.setPlayerMove(player, direction, isPressed);
            }
        }
    }

    private void update() {
        this.bomberView.updatePlayersViews();
        this.bomberView.updateGridViews();
        this.scoreLabel.setText(String.format("Score: %d", this.bomberModel.getScore()));
        this.levelLabel.setText(String.format("Level: %d", 0));
        if (bomberModel.isGameOver()) {
            this.gameOverLabel.setText(String.format("GAME OVER"));
            pause();
        }
        if (bomberModel.isYouWon()) {
            this.gameOverLabel.setText(String.format("YOU WON!"));
        }
    }

    public int getPlayerByKey(KeyCode key) {
        if (Arrays.stream(PLAYER0KEYS).anyMatch(k -> k == key)) return 0;
        else if (Arrays.stream(PLAYER1KEYS).anyMatch(k -> k == key)) return 1;
        else if (Arrays.stream(PLAYER2KEYS).anyMatch(k -> k == key)) return 2;
        else return -1;
    }

    public void pause() {
        this.paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public double getBoardWidth() {
        return BomberView.CELL_SIZE * this.bomberView.getColumnCount();
    }

    public double getBoardHeight() {
        return BomberView.CELL_SIZE * this.bomberView.getRowCount();
    }

    public static String getMapFile(int x) {
        if (x < 0 || x >= mapFiles.length) {
            return null;
        }
        return mapFiles[x];
    }
}
