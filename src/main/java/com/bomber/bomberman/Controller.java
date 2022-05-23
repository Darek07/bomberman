package com.bomber.bomberman;

import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.bomber.bomberman.BomberModel.Direction;

public class Controller {

    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label gameOverLabel;
    @FXML private BomberView bomberView;
    private BomberModel bomberModel;
    private static final String[] levelFiles = { Controller.class.getResource("map.txt").getFile().substring(3) };

    private boolean paused;

    public Controller() {
        this.paused = false;
    }

    public void initialize() {
        this.bomberModel = new BomberModel();
        this.update();
    }

    private void update() {
        this.bomberModel.step();
        this.bomberView.update(bomberModel);
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

    private class KeyHandler extends Thread implements EventHandler<KeyEvent> {

        public static final KeyCode[] PLAYER0KEYS = {KeyCode.LEFT, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.UP, KeyCode.SLASH};
        public static final KeyCode[] PLAYER1KEYS = {KeyCode.A, KeyCode.D, KeyCode.S, KeyCode.W, KeyCode.TAB};
        public static final KeyCode[] PLAYER2KEYS = {KeyCode.H, KeyCode.K, KeyCode.J, KeyCode.U, KeyCode.SPACE};
        public static final KeyCode[][] PLAYERSKEYS = {PLAYER0KEYS, PLAYER1KEYS, PLAYER2KEYS};

        public static int PLAYERS = 0;

        private final KeyCode[] PLAYERKEYS;

        public KeyHandler() {
            boolean isOneComp = true;
            PLAYERKEYS = isOneComp ? PLAYERSKEYS[PLAYERS] : PLAYER0KEYS;
            PLAYERS++;
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            boolean keyRecognized = true;
            KeyCode code = keyEvent.getCode();
            Direction direction = Direction.NONE;
            if (code == PLAYERKEYS[0]) direction = Direction.LEFT;
            else if (code == PLAYERKEYS[1]) direction = Direction.RIGHT;
            else if (code == PLAYERKEYS[2]) direction = Direction.DOWN;
            else if (code == PLAYERKEYS[3]) direction = Direction.UP;
//            else if (code == PLAYERKEYS[4]) direction = Direction.NONE;
            else if (code == KeyCode.R) {
                pause();
                bomberModel.startNewGame();
                gameOverLabel.setText(String.format(""));
                paused = false;
            }
            else keyRecognized = false;

            if (keyRecognized) {
                keyEvent.consume();
                bomberModel.setCurrentDirection(direction);
            }
        }

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
