package com.bomber.bomberman;

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

    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label gameOverLabel;
    @FXML private BomberView bomberView;
    private BomberModel bomberModel;

    private static final String[] levelFiles = { Controller.class.getResource("map.txt").getFile().substring(3) };
    public static final KeyCode[] PLAYER0KEYS = { KeyCode.LEFT, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.UP, KeyCode.SLASH };
    public static final KeyCode[] PLAYER1KEYS = { KeyCode.A, KeyCode.D, KeyCode.S, KeyCode.W, KeyCode.TAB };
    public static final KeyCode[] PLAYER2KEYS = { KeyCode.H, KeyCode.K, KeyCode.J, KeyCode.U, KeyCode.SPACE };
    public static final Set<KeyCode> LEFTKEYS = new HashSet<>(3);
    public static final Set<KeyCode> RIGHTKEYS = new HashSet<>(3);
    public static final Set<KeyCode> DOWNKEYS = new HashSet<>(3);
    public static final Set<KeyCode> UPKEYS = new HashSet<>(3);
    public static final Set<KeyCode> BOMBKEYS = new HashSet<>(3);

    static {
        Collections.addAll(LEFTKEYS, PLAYER0KEYS[0], PLAYER1KEYS[0], PLAYER2KEYS[0]);
        Collections.addAll(RIGHTKEYS, PLAYER0KEYS[1], PLAYER1KEYS[1], PLAYER2KEYS[1]);
        Collections.addAll(DOWNKEYS, PLAYER0KEYS[2], PLAYER1KEYS[2], PLAYER2KEYS[2]);
        Collections.addAll(UPKEYS, PLAYER0KEYS[3], PLAYER1KEYS[3], PLAYER2KEYS[3]);
        Collections.addAll(BOMBKEYS, PLAYER0KEYS[4], PLAYER1KEYS[4], PLAYER2KEYS[4]);
    }

    private boolean paused;

    public Controller() {
        this.paused = false;
    }

    public void initialize() {
        this.bomberModel = new BomberModel();
        this.bomberView.initializePlayersViews(bomberModel);
        this.update();
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        boolean bomb = false;
        KeyCode code = keyEvent.getCode();
        Direction direction = Direction.NONE;
        if (LEFTKEYS.stream().anyMatch(k -> k == code)) direction = Direction.LEFT;
        else if (RIGHTKEYS.stream().anyMatch(k -> k == code)) direction = Direction.RIGHT;
        else if (DOWNKEYS.stream().anyMatch(k -> k == code)) direction = Direction.DOWN;
        else if (UPKEYS.stream().anyMatch(k -> k == code)) direction = Direction.UP;
        else if (BOMBKEYS.stream().anyMatch(k -> k == code)) bomb = true;
//        else if (code == KeyCode.R) {
//            pause();
//            bomberModel.startNewGame();
//            gameOverLabel.setText(String.format(""));
//            paused = false;
//        }
        else keyRecognized = false;

        if (keyRecognized) {
            keyEvent.consume();
            if (bomb && keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                bomberModel.setBomb(getPlayerByKey(code), this);
            }
            else {
                bomberModel.setMoving(direction, getPlayerByKey(code), keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED));
            }
            update();
        }
    }

    private void update() {
        this.bomberView.updatePlayersViews(bomberModel);
        this.bomberView.updateGridViews(bomberModel);
        this.bomberModel.step();
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

    public void bombDetonated() {
        this.bomberView.updatePlayersViews(bomberModel);
        this.bomberView.updateGridViews(bomberModel);
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

    public static String getLevelFile(int x) {
        return levelFiles[x];
    }
}
