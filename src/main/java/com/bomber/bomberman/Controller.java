package com.bomber.bomberman;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends Thread implements EventHandler<KeyEvent>, Initializable {
    public static int PLAYERS_NUMBER = 3;
    private static File mapFile;
    private static int roundsNumber;
    private static int roundTimeSec;
    private static final String[] playersNames = new String[PLAYERS_NUMBER];

    @FXML private BorderPane labels;
    @FXML private Label playersLabel;
    @FXML private Label roundsLabel;
    @FXML private Label timeLabel;
    @FXML private Label bottomLabel;
    @FXML private BomberView bomberView;
    private BomberModel bomberModel;
    private Timeline clock;
    private long timeStart;
    private int currentRound;
    private AnimationTimer animationTimer;
    private boolean isPaused;

    public static final URL[] mapFiles = { Controller.class.getResource("map.txt") };
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bomberModel = new BomberModel();
        this.currentRound = 0;
        this.initView();

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
                    updateView();
                    lastTime = now;
                }
            }
        };

        this.clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long millis = System.currentTimeMillis() - timeStart;
            this.bomberView.updateTimeLabel(millis);
        }), new KeyFrame(Duration.seconds(1)));
        this.clock.setCycleCount(roundTimeSec);
        this.startRound();
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        boolean bombKey = false;
        boolean isPressed = keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED);
        KeyCode keyCode = keyEvent.getCode();
        Direction direction = Direction.NONE;

        if (LEFT_KEYS.stream().anyMatch(k -> k == keyCode)) {
            direction = Direction.LEFT;
        }
        else if (RIGHT_KEYS.stream().anyMatch(k -> k == keyCode)) {
            direction = Direction.RIGHT;
        }
        else if (DOWN_KEYS.stream().anyMatch(k -> k == keyCode)) {
            direction = Direction.DOWN;
        }
        else if (UP_KEYS.stream().anyMatch(k -> k == keyCode)) {
            direction = Direction.UP;
        }
        else if (BOMB_KEYS.stream().anyMatch(k -> k == keyCode)) {
            bombKey = true;
        }
        else {
            if (keyCode == KeyCode.R && isPressed && isEndGame()) {
                changeToStartWindow();
            }
            else if (keyCode == KeyCode.ENTER && isPressed && isPaused && !isEndGame()) {
                startRound();
            }
            return;
        }

        keyEvent.consume();
        Player player = getPlayerByKey(keyCode);
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

    private void startRound() {
        bomberModel.restoreData();
        isPaused = false;
        currentRound++;
        animationTimer.start();
        this.timeStart = System.currentTimeMillis();
        this.clock.play();
        this.bomberView.updateInfo(bomberModel, currentRound);
    }

    private void endRound() {
        if (bomberModel.isAnyBombAppears()) {
            return;
        }
        this.animationTimer.stop();
        this.clock.stop();
        isPaused = true;
        bomberModel.determineRoundWinner();
        this.bomberView.endInfo(isEndGame()
                ? this.bomberModel.getWinner().getName()
                : null);
    }

    private void changeToStartWindow() {
        Stage stage = (Stage) labels.getScene().getWindow();
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
    }

    public Player getPlayerByKey(KeyCode key) {
        int playerID = -1;
        if (Arrays.stream(PLAYER0KEYS).anyMatch(k -> k == key)) {
            playerID =  0;
        }
        else if (Arrays.stream(PLAYER1KEYS).anyMatch(k -> k == key)) {
            playerID =  1;
        }
        else if (Arrays.stream(PLAYER2KEYS).anyMatch(k -> k == key)) {
            playerID =  2;
        }
        return bomberModel.getPlayerByID(playerID);
    }

    private void initView() {
        this.bomberView.setLabels(this.labels);
        this.bomberView.setPlayersLabel(this.playersLabel);
        this.bomberView.setRoundsLabel(this.roundsLabel);
        this.bomberView.setTimeLabel(this.timeLabel);
        this.bomberView.setBottomLabel(this.bottomLabel);

        this.bomberView.initializePlayersViews();
        this.bomberView.initializeInfo();
    }

    private void updateView() {
        this.bomberView.updatePlayersViews(bomberModel);
        this.bomberView.updateGridViews(bomberModel);
        this.bomberView.updateInfo(bomberModel, currentRound);
    }

    private boolean isEndGame() {
        return isPaused && currentRound >= roundsNumber;
    }

    public double getBoardWidth() {
        return BomberView.CELL_SIZE * this.bomberView.getColumnCount();
    }

    public double getBoardHeight() {
        return BomberView.CELL_SIZE * this.bomberView.getRowCount();
    }

    public static File getMapFile() {
        return mapFile;
    }

    public static void setMapFile(int x) {
        if (x < 0 || x >= mapFiles.length) {
            throw new NullPointerException();
        }
        try {
            mapFile = Paths.get(mapFiles[x].toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ;
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
