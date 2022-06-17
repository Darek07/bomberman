package com.bomber.bomberman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartWindowController implements Initializable {

	public static final int secondsPerMinute = 60;
	@FXML
	public Button submitButton;
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
	private ToggleButton num1_button;

	@FXML
	private ToggleButton num2_button;

	@FXML
	private ToggleButton num3_button;

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

	private final ToggleGroup toggleGroup = new ToggleGroup();

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		sl_rounds.valueProperty().addListener((observable, oldValue, newValue) -> {

			text_rounds.setText(Integer.toString(newValue.intValue()));


		});
		sl_time.valueProperty().addListener((observable, oldValue, newValue) -> {

			text_time.setText(Double.toString(newValue.doubleValue()));


		});
		initializeRadioGroupButtons();
		initializeMapSelection();
		submitButton.setOnAction(this::changeToBoard);
	}
//    --------end--vor-o-na--------

	private void changeToBoard(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		try {
			configureGame();

			URL resource = getClass().getResource("bomber.fxml");
			assert resource != null;
			FXMLLoader loader = new FXMLLoader(resource);
			Parent root = loader.load();

			Controller controller = loader.getController();
			root.setOnKeyPressed(controller);
			root.setOnKeyReleased(controller);
			double sceneWidth = controller.getBoardWidth() + 20.0;
			double sceneHeight = controller.getBoardHeight() + 100.0;

			Scene scene = new Scene(root, sceneWidth, sceneHeight);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
			root.requestFocus();
		} catch (IOException error) {
			error.printStackTrace();
		} catch (NullPointerException notSelected) {
			System.out.println("Not all fields are filled");
		}
	}

	private void configureGame() {
		Controller.setPlayersNumber(getPlayersNumber());
		Controller.setMapFile(getSelectedMap());
		Controller.setRoundsNumber(getRoundsNumber());
		Controller.setRoundTimeSec(getTimePerRound());
		configurePlayersNames();
	}

	private void initializeRadioGroupButtons() {
		num1_button.setToggleGroup(toggleGroup);
		num2_button.setToggleGroup(toggleGroup);
		num3_button.setToggleGroup(toggleGroup);
	}

	private void initializeMapSelection() {
		choose_map.setOnAction(e -> choose_map.setText(map1.getText()));
		map1.setOnAction(e -> choose_map.setText(map1.getText()));
		map2.setOnAction(e -> choose_map.setText(map2.getText()));
		map3.setOnAction(e -> choose_map.setText(map3.getText()));
	}

	private int getPlayersNumber() {
		ToggleButton selected = (ToggleButton) toggleGroup.getSelectedToggle();
		String selectedText = selected.getText();

		String threePlayers = num3_button.getText();
		String twoPlayers = num2_button.getText();

		if (selectedText.equals(threePlayers)) {
			return 3;
		}

		if (selectedText.equals(twoPlayers)) {
			return 2;
		}

		return 1;
	}

	private int getSelectedMap() {
		String text = choose_map.getText();
		return Character.getNumericValue(text.charAt(text.length()-1)) - 1;
	}

	private int getRoundsNumber() {
		return (int)sl_rounds.getValue();
	}

	private int getTimePerRound() {
		return (int)(sl_time.getValue() * secondsPerMinute);
	}

	private void configurePlayersNames() {
		Controller.setPlayerName(player1_name.getText(), 0);
		Controller.setPlayerName(player2_name.getText(), 1);
		Controller.setPlayerName(player3_name.getText(), 2);
	}
}
