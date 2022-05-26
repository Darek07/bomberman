package com.bomber.bomberman;

import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.bomber.bomberman.BomberModel.Direction;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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

//    ----------vor-o-na-----------
@FXML
private SplitMenuButton choose_map;

    @FXML
    private Rectangle form1;

    @FXML
    private Rectangle form2;

    @FXML
    private Rectangle form3;

    @FXML
    private Rectangle form4;

    @FXML
    private Rectangle form_rounds;

    @FXML
    private Rectangle form_time;

    @FXML
    private ImageView image_map1;

    @FXML
    private ImageView image_map2;

    @FXML
    private ImageView image_map3;

    @FXML
    private MenuItem map1;

    @FXML
    private MenuItem map2;

    @FXML
    private MenuItem map3;

    @FXML
    private Button num1_button;

    @FXML
    private Button num2_button;

    @FXML
    private Button num3_button;

    @FXML
    private TextField player1_name;

    @FXML
    private TextField player2_name;

    @FXML
    private TextField player3_name;

    @FXML
    private TextField player4_name;

    @FXML
    private Slider sl_rounds;

    @FXML
    private Slider sl_time;

    @FXML
    private TextField text_rounds;

    @FXML
    private TextField text_time;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sl_rounds.valueProperty().addListener((observable, oldValue, newValue) -> {

            text_rounds.setText(Integer.toString(newValue.intValue()));


        });
        sl_time.valueProperty().addListener((observable, oldValue, newValue) -> {

            text_time.setText(Double.toString(newValue.doubleValue()));


        });

    }
//    --------end--vor-o-na--------
}
