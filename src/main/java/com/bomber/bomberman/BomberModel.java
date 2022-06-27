package com.bomber.bomberman;

import javafx.fxml.FXML;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * BomberModel w czasie rozgrywki aktualizuje i kontroluje wszystkie dane
 */
public class BomberModel {

	private final List<Player> alivePlayers = new ArrayList<>(Controller.PLAYERS_NUMBER);
	private final List<Player> ripPlayers = new ArrayList<>(Controller.PLAYERS_NUMBER);
	@FXML private int rowCount;
	@FXML private int columnCount;
	private CellValue[][] grid;

	/**
	 * Tworzenie modelu gry. Kontroler powinien być już zainicjalizowany danymi początkowymi
	 */
	public BomberModel() {
		this.startNewGame();
	}

	/**
	 * Zaczyna nową rozgrywkę
	 */
	public void startNewGame() {
		rowCount = 0;
		columnCount = 0;
		initializeMap(Controller.getMapFile());
	}

	/**
	 * Inicjalizacja pliku z mapą. Kontroler powinien być już zainicjalizowany danymi początkowymi
	 * # - nie zniszczalna ściana
	 * $ - zniaszczalna ściana
	 * P - gracz
	 *
	 * @param file plik z mapą
	 */
	public void initializeMap(File file) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (Exception e) {
			System.out.println("Could not open map file");
			System.exit(1);
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
		for (int row = 0; scanner.hasNextLine(); row++) {
			String line = scanner.nextLine();
			for (int column = 0; column < line.length(); column++) {
				CellValue cell;
				switch (line.charAt(column)) {
					case '#' -> cell = CellValue.UNBREAKABLE_WALL;
					case '&' -> cell = CellValue.BREAKABLE_WALL;
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

	/**
	 * @param playerID id gracza
	 * @return gracz z podanym id
	 */
	public Player getPlayerByID(int playerID) {
		return Stream.of(alivePlayers, ripPlayers)
				.flatMap(Collection::stream)
				.filter(player -> player.getPlayerID() == playerID)
				.findFirst()
				.orElse(null);
	}

	/**
	 * Dodanie zmarłych graczy w danej rundzie oraz ustawienie flagi gracza, że umarł
	 *
	 * @param ripPlayers lista umarłych graczy
	 */
	public void addRipPlayers(List<Player> ripPlayers) {
		ripPlayers.forEach(player -> {
			if (!this.ripPlayers.contains(player)) {
				this.ripPlayers.add(player);
				player.setDied(true);
			}
		});
		this.alivePlayers.removeAll(ripPlayers);
	}

	/**
	 * Wznowienie początkowych danych, aby rozpocząć kolejną rundę
	 */
	public void restoreData() {
		alivePlayers.addAll(ripPlayers);
		ripPlayers.clear();
		alivePlayers.forEach(Player::restoreInitialValues);
	}

	/**
	 * @return gracz, który wygrał daną rundę lub null, w przypadku, gdy zwycięsca nie został jeszcze znaleziony
	 */
	public Player determineRoundWinner() {
		Player player = (alivePlayers.size() == 1 ? alivePlayers.get(0) : null);
		if (player != null) {
			player.increaseWins();
			ripPlayers.add(player);
			alivePlayers.remove(player);
		}
		return player;
	}

	/**
	 * @return true - istnieje bomba, która nie skończyła swojego działania, inaczej false
	 */
	public boolean isAnyBombAppears() {
		return Arrays.stream(grid).flatMap(Arrays::stream).anyMatch(cell -> cell == CellValue.BOMB || cell == CellValue.RIP || cell == CellValue.FIRE);
	}

	/**
	 * @return gracz, który ma najwięcej wygranych rund
	 */
	public Player getWinner() {
		return Stream.of(alivePlayers, ripPlayers)
				.flatMap(Collection::stream).max(Comparator.comparingInt(Player::getWins))
				.orElse(null);
	}

	/**
	 * Ustawia bombę
	 *
	 * @param player gracz, który ustawia bombę
	 */
	public void setBomb(Player player) {
		if (player.getPlayerActiveBombs() < player.getPlayerMaxActiveBombs()) {
			new Bomb(this, player);
		}
	}

	/**
	 * Ustawia dla podanego gracza kierunek ruchu oraz czy może się poruszać
	 *
	 * @param player    gracz, do którego należy zastosować podane ustawienia
	 * @param direction kierunek ruchu
	 * @param isMove    true - może się poruszać, false - nie
	 */
	public void setPlayerMove(Player player, Direction direction, boolean isMove) {
		player.setPlayerDirectionAndMove(direction, isMove);
	}

	/**
	 * @param row    wiersz na planszy
	 * @param column kolumna na planszy
	 * @return wartość komórki
	 */
	public CellValue getCellValue(int row, int column) {
		return (row < 0 || column < 0 || row >= this.grid.length || column >= this.grid[0].length)
				? null
				: this.grid[row][column];
	}

	/**
	 * Ustawia wartość komórki
	 *
	 * @param cellValue nowa wartość komórki
	 * @param row       wiersz na planszy
	 * @param column    kolumna na planszy
	 */
	public void setCellValue(CellValue cellValue, int row, int column) {
		if (row < 0 || column < 0 || row >= this.grid.length || column >= this.grid[0].length) {
			return;
		}
		grid[row][column] = cellValue;
	}

	/**
	 * @return liczba wierszy
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @return liczba kolumn
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * @return listę żywych graczy
	 */
	public List<Player> getAlivePlayers() {
		return alivePlayers;
	}

	/**
	 * @return liczbę żywych graczy
	 */
	public int getNumAlivePlayers() {
		return alivePlayers.size();
	}
}
