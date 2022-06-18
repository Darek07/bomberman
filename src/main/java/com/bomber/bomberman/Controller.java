package com.bomber.bomberman;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller extends Thread implements EventHandler<KeyEvent>, Initializable {
    public static int PLAYERS_NUMBER = 3;
    private static String mapFile;
    private static int roundsNumber;
    private static int roundTimeSec;
    private static final String[] playersNames = new String[PLAYERS_NUMBER];

    @FXML private BorderPane labels;
    @FXML private Label playersLabel;
    @FXML private Label roundsLabel;
    @FXML private Label timeLabel;
    @FXML private BomberView bomberView;
    private BomberModel bomberModel;
    private boolean paused;
    private Timeline clock;
    private long timeStart;
    private int currentRound;
    private AnimationTimer animationTimer;
    private boolean isPaused;

    public static final String[] mapFiles = { Controller.class.getResource("map.txt").getFile().substring(3) };
    public static final KeyCode[] PLAYER0KEYS = { KeyCode.LEFT, KeyCode.RIGHT, KeyCode.DOWN, KeyCode.UP, KeyCode.SLASH };
    public static final KeyCode[] PLAYER1KEYS = { KeyCode.A, KeyCode.D, KeyCode.S, KeyCode.W, KeyCode.TAB };
    public static final KeyCode[] PLAYER2KEYS = { KeyCode.H, KeyCode.K, KeyCode.J, KeyCode.U, KeyCode.SPACE };
    public static final Set<KeyCode> LEFT_KEYS;
    public static final Set<KeyCode> RIGHT_KEYS;
    public static final Set<KeyCode> DOWN_KEYS;
    public static final Set<KeyCode> UP_KEYS;
    public static final Set<KeyCode> BOMB_KEYS;

    static {
        LEFT_KEYS = new HashSet<>(PLAYERS_NUMBER);
        RIGHT_KEYS = new HashSet<>(PLAYERS_NUMBER);
        DOWN_KEYS = new HashSet<>(PLAYERS_NUMBER);
        UP_KEYS = new HashSet<>(PLAYERS_NUMBER);
        BOMB_KEYS = new HashSet<>(PLAYERS_NUMBER);
        Collections.addAll(LEFT_KEYS, PLAYER0KEYS[0], PLAYER1KEYS[0], PLAYER2KEYS[0]);
        Collections.addAll(RIGHT_KEYS, PLAYER0KEYS[1], PLAYER1KEYS[1], PLAYER2KEYS[1]);
        Collections.addAll(DOWN_KEYS, PLAYER0KEYS[2], PLAYER1KEYS[2], PLAYER2KEYS[2]);
        Collections.addAll(UP_KEYS, PLAYER0KEYS[3], PLAYER1KEYS[3], PLAYER2KEYS[3]);
        Collections.addAll(BOMB_KEYS, PLAYER0KEYS[4], PLAYER1KEYS[4], PLAYER2KEYS[4]);
    }

    public Controller() {
        this.paused = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bomberModel = new BomberModel();
        this.bomberView.setBomberModel(bomberModel);
        this.bomberView.initializePlayersViews();

        this.animationTimer = new AnimationTimer() {
            long lastTime = 0;
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                final double elapsedMicroSeconds = (now - lastTime) / 1_000.0 ;
                if (elapsedMicroSeconds >= 1000) {
                    int alive = bomberModel.getNumAlivePlayers();
                    if (alive <= 1 && alive != PLAYERS_NUMBER || clock.getStatus() == Animation.Status.STOPPED) {
                        endRound();
                    }
                    update();
                    lastTime = now;
                }
            }
        };

        this.currentRound = 0;
        this.labels.setPrefHeight(PLAYERS_NUMBER * labels.getPrefHeight() + labels.getPadding().getBottom());
        this.clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long millis = System.currentTimeMillis() - timeStart;
            String txt = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            this.timeLabel.setText(txt);
        }), new KeyFrame(Duration.seconds(1)));
        this.clock.setCycleCount(roundTimeSec);
        startRound();
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        boolean bombKey = false;
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
            bombKey = true;
        }
        else if (isPressed && code == KeyCode.ENTER && !isEndGame()) {
            System.out.println("enter");
            startRound();
            keyRecognized = false;
        }
        else if (isPressed && code == KeyCode.R && isEndGame()) {
            System.out.println("r");
            Stage stage = (Stage) ((Node) keyEvent.getSource()).getScene().getWindow();
            try {
                URL resource = getClass().getResource("bomberman_start.fxml");
                assert resource != null;
                FXMLLoader loader = new FXMLLoader(resource);
                Parent root = loader.load();

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            keyRecognized = false;
        }
        else keyRecognized = false;

        if (keyRecognized) {
            keyEvent.consume();
            int playerID = getPlayerByKey(code);
            Player player = bomberModel.getPlayerByID(playerID);
            if (player.isDied()) {
                return;
            }
            if (bombKey && isPressed) {
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
        updateLabels();
    }

    private void updateLabels() {
        StringBuilder format = new StringBuilder();
        Player player;
        for (int i = 0; i < PLAYERS_NUMBER; i++) {
            player = bomberModel.getPlayerByID(i);
            format.append(String.format("%s: %d wins | %d loses\n", playersNames[i], player.getWins(), player.getDies()));
        }
        this.playersLabel.setText(format.toString());
        this.roundsLabel.setText(String.format("Round: %d", currentRound));
    }

    private void startRound() {
        bomberModel.restoreData();
        isPaused = false;
        animationTimer.start();
        currentRound++;
        this.timeStart = System.currentTimeMillis();
        this.clock.play();
        updateLabels();
    }

    private void endRound() {
        if (bomberModel.isTempCellValues()) {
            return;
        }
        this.animationTimer.stop();
        this.clock.stop();
        bomberModel.determineRoundWinner();
        isPaused = true;
        if (isEndGame()) {
            Player winner = bomberModel.getWinner();
            timeLabel.setText(String.format("WINNER %s.\nPress R to restart the game", winner.getName()));
        }
        else {
            timeLabel.setText("Press Enter to start next round");
        }
    }

    private boolean isEndGame() {
        return isPaused && currentRound >= roundsNumber;
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

    public static String getMapFile() {
        return mapFile;
    }

    public static void setMapFile(int x) {
        if (x < 0 || x >= mapFiles.length) {
            throw new NullPointerException();
        }
        mapFile = mapFiles[x];
    }

    public static void setPlayersNumber(int playersAmount) {
        Controller.PLAYERS_NUMBER = playersAmount;
    }

    public static void setRoundsNumber(int roundsNumber) {
        Controller.roundsNumber = roundsNumber;
    }

    public static void setRoundTimeSec(int roundTimeSec) {
        Controller.roundTimeSec = roundTimeSec;
    }

    public static String getPlayerName(int x) {
        if (x < 0 || x >= playersNames.length) {
            return null;
        }
        return playersNames[x];
    }

    public static void setPlayerName(String playerName, int x) {
        Controller.playersNames[x] = playerName;
    }

}
