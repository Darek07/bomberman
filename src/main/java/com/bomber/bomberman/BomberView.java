package com.bomber.bomberman;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bomber.bomberman.Controller.PLAYERS_NUMBER;

/**
 * BomberView w czasie rozgrywki aktualizuje widok
 */
public class BomberView extends Group {
	/**
	 * Rozmiar komórki
	 */
	public final static int CELL_SIZE = 40;
	/**
	 * Rozmiar gracza
	 */
	public final static int PLAYER_SIZE = 27;

	@FXML private int rowCount;
	@FXML private int columnCount;
	@FXML private BorderPane labels;
	@FXML private Label playersLabel;
	@FXML private Label roundsLabel;
	@FXML private Label timeLabel;
	@FXML private Label bottomLabel;
	private ImageView[][] cellViews;
	private final List<ImageView> playersViews = new ArrayList<>(PLAYERS_NUMBER);
	private final Image[] playersImages = new Image[PLAYERS_NUMBER];
	private final Image unbreakableWallImage;
	private final Image breakableWallImage;
	private final Image emptyWallImage;
	private final Image bombImage;
	private final Image fireImage;
	private final Image ripImage;
	private final Image speedBonusImage;
	private final Image bombBonusImage;

	/**
	 * Inicjalizacja wykorzystywanych w czasie rozrywki zdjęć. Kontroler powinien mieć zainicjalizowany indeks mapy
	 */
	public BomberView() {
		String base = "img/";
		int mapIndex = Controller.getMapIndex() + 1;
		this.unbreakableWallImage = new Image(getClass().getResourceAsStream(base + "unbreakable" + mapIndex + ".png"));
		this.breakableWallImage = new Image(getClass().getResourceAsStream(base + "breakable" + mapIndex + ".png"));
		this.emptyWallImage = new Image(getClass().getResourceAsStream(base + "default" + mapIndex + ".png"));
		this.bombImage = new Image(getClass().getResourceAsStream(base + "bomb.png"));
		this.fireImage = new Image(getClass().getResourceAsStream(base + "fire.png"));
		this.ripImage = new Image(getClass().getResourceAsStream(base + "rip.png"));
		this.speedBonusImage = new Image(getClass().getResourceAsStream(base + "Speed_Bonus.png"));
		this.bombBonusImage = new Image(getClass().getResourceAsStream(base + "Bomb_A.png"));
		for (int i = 0; i < PLAYERS_NUMBER; i++) {
			this.playersImages[i] = new Image(getClass().getResourceAsStream(base + "player-" + (i+1) + ".png"));
		}
	}

	private void initializeGrid() {
		if (this.rowCount <= 0 || this.columnCount <= 0) {
			return;
		}
		this.initializeBackground();
		this.cellViews = new ImageView[this.rowCount][this.columnCount];
		for (int row = 0; row < this.rowCount; row++) {
			for (int column = 0; column < this.columnCount; column++) {
				ImageView imageView = new ImageView();
				imageView.setX(column * CELL_SIZE);
				imageView.setY(row * CELL_SIZE);
				imageView.setFitWidth(CELL_SIZE);
				imageView.setFitHeight(CELL_SIZE);
				this.cellViews[row][column] = imageView;
				this.getChildren().add(imageView);
			}
		}
	}

	private void initializeBackground() {
		for (int row = 0; row < this.rowCount; row++) {
			for (int column = 0; column < this.columnCount; column++) {
				ImageView imageView = new ImageView(this.emptyWallImage);
				imageView.setX(column * CELL_SIZE);
				imageView.setY(row * CELL_SIZE);
				imageView.setFitWidth(CELL_SIZE);
				imageView.setFitHeight(CELL_SIZE);
				this.getChildren().add(imageView);
			}
		}
	}

	/**
	 * Inicjalizacja widoków wszystkich graczy. Kotroler powinien być zainicjalizowany
	 */
	public void initializePlayersViews() {
		playersViews.forEach(playerView -> playerView.setImage(null));
		playersViews.clear();
		for (int i = 0; i < PLAYERS_NUMBER; i++) {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(PLAYER_SIZE);
			imageView.setFitHeight(PLAYER_SIZE);
			playersViews.add(imageView);
		}
		this.getChildren().addAll(playersViews);
	}

	/**
	 * Inicjalizacja aktualnej informacji o grze. Kotroler powinien być zainicjalizowany
	 */
	public void initializeInfo() {
		this.labels.setPrefHeight(PLAYERS_NUMBER * labels.getPrefHeight() + labels.getPadding().getBottom());
		this.bottomLabel.setText("Next round: Enter / New game: R\t\tMove: arrow keys / WASD / UHJK\t\tBomb: Shift TAB Space");
	}

	/**
	 * Aktualizuje widoki graczy
	 * @param bomberModel BomberModel wykorzystywany w grze
	 */
	public void updatePlayersViews(BomberModel bomberModel) {
		if (playersViews.size() != PLAYERS_NUMBER) {
			initializePlayersViews();
		}
		for (int i = 0; i < playersViews.size(); i++) {
			Player player = bomberModel.getPlayerByID(i);
			if (player.isDied()) {
				playersViews.get(i).setImage(null);
				continue;
			}
			Point2D location = player.getPlayerLocation();
			playersViews.get(i).setX(location.getX());
			playersViews.get(i).setY(location.getY());
			playersViews.get(i).setImage(this.playersImages[i]);
		}
	}

	/**
	 * Aktualizuje widok planszy
	 * @param bomberModel BomberModel wykorzystywany w grze
	 */
	public void updateGridViews(BomberModel bomberModel) {
		if (bomberModel.getRowCount() != this.rowCount || bomberModel.getColumnCount() != this.columnCount) {
			return;
		}
		for (int row = 0; row < this.rowCount; row++) {
			for (int column = 0; column < this.columnCount; column++) {
				CellValue value = bomberModel.getCellValue(row, column);
				switch (value) {
					case BREAKABLE_WALL -> this.cellViews[row][column].setImage(this.breakableWallImage);
					case UNBREAKABLE_WALL -> this.cellViews[row][column].setImage(this.unbreakableWallImage);
					case SPEED_BONUS -> this.cellViews[row][column].setImage(this.speedBonusImage);
					case BOMB_BONUS -> this.cellViews[row][column].setImage(this.bombBonusImage);
					case BOMB -> this.cellViews[row][column].setImage(this.bombImage);
					case FIRE -> this.cellViews[row][column].setImage(this.fireImage);
					case RIP -> this.cellViews[row][column].setImage(this.ripImage);
					default -> this.cellViews[row][column].setImage(null);
				}
			}
		}
	}

	/**
	 * Aktualizuje aktualną informację o grze
	 * @param bomberModel BomberModel wykorzystywany w grze
	 * @param currentRound aktualna runda
	 */
	public void updateInfo(BomberModel bomberModel, int currentRound) {
		StringBuilder format = new StringBuilder();
		Player player;
		for (int i = 0; i < PLAYERS_NUMBER; i++) {
			player = bomberModel.getPlayerByID(i);
			format.append(String.format(
					"%s: %d wins | %d loses\n",
					player.getName(),
					player.getWins(),
					player.getDies()));
		}
		this.playersLabel.setText(format.toString());
		this.roundsLabel.setText(String.format("Round: %d", currentRound));
	}

	/**
	 * Aktualizuje widok aktualnego czasu rundy (mm:ss)
	 * @param milliSeconds aktualny czas rundy
	 */
	public void updateTimeLabel(long milliSeconds) {
		String txt = String.format("%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(milliSeconds) -
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
				TimeUnit.MILLISECONDS.toSeconds(milliSeconds) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
		this.timeLabel.setText(txt);
	}

	/**
	 * Wyświetla informację o końcu rundy lub gry
	 * @param winner zwycięzca gry lub null w przypadku kontynuacji rozgrywki
	 */
	public void endInfo(String winner) {
		this.timeLabel.setText(winner == null
						? "Press Enter to start next round"
						: String.format("WINNER %s.\nPress R to restart the game", winner));
	}

	/**
	 * @return liczbę wierszy
	 */
	public int getRowCount() {
		return this.rowCount;
	}

	/**
	 * Inicjalizacji planszy w przypadku już podanych wartości wierszy oraz kolumn
	 * @param rowCount liczba wierszy
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
		this.initializeGrid();
	}

	/**
	 * @return liczbę kolumn
	 */
	public int getColumnCount() {
		return this.columnCount;
	}

	/**
	 * Inicjalizacji planszy w przypadku już podanych wartości wierszy oraz kolumn
	 * @param columnCount liczba kolumn
	 */
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
		this.initializeGrid();
	}

	/**
	 * Inicjalizacja Javafx Label dla aktualnej informacji o rozgrywce. Wywoływany w Controller
	 * @param labels Javafx Label
	 */
	public void setLabels(BorderPane labels) {
		this.labels = labels;
	}

	/**
	 * Inicjalizacja Javafx Label dla informacji o graczach. Wywoływany w Controller
	 * @param playersLabel Javafx Label players
	 */
	public void setPlayersLabel(Label playersLabel) {
		this.playersLabel = playersLabel;
	}

	/**
	 * Inicjalizacja Javafx Label dla informacji o rundzie. Wywoływany w Controller
	 * @param roundsLabel Javafx Label rounds
	 */
	public void setRoundsLabel(Label roundsLabel) {
		this.roundsLabel = roundsLabel;
	}

	/**
	 * Inicjalizacja Javafx Label dla informacji o czasie rundy. Wywoływany w Controller
	 * @param timeLabel Javafx Label time
	 */
	public void setTimeLabel(Label timeLabel) {
		this.timeLabel = timeLabel;
	}

	/**
	 * Inicjalizacja Javafx Label dla informacji o klawiszach używanych w rozgrywce. Wywoływany w Controller
	 * @param bottomLabel Javafx Label bottom
	 */
	public void setBottomLabel(Label bottomLabel) {
		this.bottomLabel = bottomLabel;
	}
}
