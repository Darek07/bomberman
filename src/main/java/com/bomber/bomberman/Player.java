package com.bomber.bomberman;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;

import static com.bomber.bomberman.BomberView.PLAYER_SIZE;
import static com.bomber.bomberman.BomberView.CELL_SIZE;

/**
 * Klasa reprezentująca gracza. Przechowywane są dane o graczu. Umożliwienie poruszania się po planszy
 */
public class Player extends AnimationTimer {
	private final int playerID;
	private final BomberModel model;
	private final Point2D playerInitialLocation;
	private final Integer playerInitialSpeed;
	private final Integer initialMaxActiveBombs;
	private Point2D playerLocation;
	private Integer playerSpeed;
	private Direction playerDirection;
	private Integer playerMaxActiveBombs;
	private Integer playerActiveBombs;
	private boolean isMoving;
	private boolean isInsideBomb;
	private boolean isDied;
	private int fireDistance;
	private long lastUpdateTime;
	private int dies = 0;
	private int wins = 0;

	private final String name;

	/**
	 * Tworzenie gracza
	 *
	 * @param model    BomberModel wykorzystywany w grze
	 * @param playerID id gracza
	 * @param col      początkowa kolumna gracza na planszy
	 * @param row      początkowy wiersz gracza na planszy
	 */
	public Player(BomberModel model, int playerID, int col, int row) {
		this.model = model;
		this.playerID = playerID;
		this.name = Controller.getPlayerName(playerID);
		this.playerInitialLocation = new Point2D(col * BomberView.CELL_SIZE, row * BomberView.CELL_SIZE);
		this.playerInitialSpeed = 10;
		this.initialMaxActiveBombs = 1;
		restoreInitialValues();
		start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(long now) {
		if (lastUpdateTime == 0 || lastUpdateTime >= now) {
			lastUpdateTime = now;
			return;
		}
		final double elapsedMilliSeconds = (now - lastUpdateTime) / 1_000_000.0;
		if (elapsedMilliSeconds >= 10) {
			movePlayer();
			lastUpdateTime = now;
		}
	}

	/**
	 * Ruch gracza w kierunku wskazanym przez Controller oraz zgodnym z zasadami gry
	 */
	public void movePlayer() {
		if (!isMoving) {
			return;
		}

		Point2D velocitySpeed = playerDirection.velocity.multiply((float) playerSpeed / 10);
		Point2D newLocation = this.playerLocation.add(velocitySpeed);
		if (!isLocationCollide(newLocation)) {
			this.playerLocation = newLocation;
			Bonus.isPlayerPickBonus(model, this);
		}
		checkIsInsideBomb();
	}

	private boolean isLocationCollide(Point2D location) {
		Point2D point1 = location;
		Point2D point2 = location.add(PLAYER_SIZE, PLAYER_SIZE);
		switch (playerDirection) {
			case UP -> point2 = point2.subtract(0, PLAYER_SIZE);
			case RIGHT -> point1 = point1.add(PLAYER_SIZE, 0);
			case DOWN -> point1 = point1.add(0, PLAYER_SIZE);
			case LEFT -> point2 = point2.subtract(PLAYER_SIZE, 0);
		}
		return isPointCollide(point1) || isPointCollide(point2);
	}

	private boolean isPointCollide(Point2D point) {
		int col = (int) point.getX() / CELL_SIZE;
		int row = (int) point.getY() / CELL_SIZE;
		return switch (model.getCellValue(row, col)) {
			case BOMB -> !isInsideBomb;
			case UNBREAKABLE_WALL, BREAKABLE_WALL -> true;
			case EMPTY, FIRE, RIP, SPEED_BONUS, BOMB_BONUS -> false;
		};
	}

	private void checkIsInsideBomb() {
		if (!isInsideBomb) {
			return;
		}

		isInsideBomb = false;
		isInsideBomb =
				isPointCollide(playerLocation) ||
						isPointCollide(playerLocation.add(PLAYER_SIZE, 0)) ||
						isPointCollide(playerLocation.add(PLAYER_SIZE, PLAYER_SIZE)) ||
						isPointCollide(playerLocation.add(0, PLAYER_SIZE));
	}

	/**
	 * Ustawienie, czy gracz może się poruszać i w jakim kierunku
	 *
	 * @param direction kierunek ruchu
	 * @param move      jeżeli true, to gracz może się poruszać, w przeciwnym razie nie
	 */
	public void setPlayerDirectionAndMove(Direction direction, boolean move) {
		if (this.isMoving && !move && this.playerDirection != direction) {
			return;
		}
		this.playerDirection = direction;
		this.isMoving = move;
	}

	/**
	 * Wznowienie początkowych danych gracza, aby rozpocząć kolejną rundę
	 */
	public void restoreInitialValues() {
		playerLocation = playerInitialLocation;
		playerSpeed = playerInitialSpeed;
		playerMaxActiveBombs = initialMaxActiveBombs;
		playerActiveBombs = 0;
		playerDirection = Direction.NONE;
		isMoving = false;
		isInsideBomb = false;
		isDied = false;
		fireDistance = 3;
		lastUpdateTime = 0;
		this.start();
	}

	/**
	 * Ustawianie flagi, że gracz znajduję się wewnątrz bomby. Metoda jest wywoływana gdy gracz umieszcza bombe na planszy
	 */
	public void setIsInsideBomb() {
		isInsideBomb = true;
	}

	/**
	 * @return pozycję gracza na planszy
	 */
	public Point2D getPlayerLocation() {
		return playerLocation;
	}

	/**
	 * @return odległość ognia umieszczonej przez gracza bomby
	 */
	public int getFireDistance() {
		return fireDistance;
	}

	/**
	 * @return id gracza
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * @return imię gracza
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return ilość przegranych rund
	 */
	public int getDies() {
		return dies;
	}

	/**
	 * Zwiększenie o jeden liczby rund przegranych przez gracza
	 */
	public void increaseDies() {
		this.dies++;
	}

	/**
	 * @return ilość wygranych rund
	 */
	public int getWins() {
		return wins;
	}

	/**
	 * Zwiększenie o jeden liczby rund wygranych przez gracza
	 */
	public void increaseWins() {
		this.wins++;
	}

	/**
	 * @return czy gracz żyje
	 */
	public boolean isDied() {
		return isDied;
	}

	/**
	 * @param died true - zwiększyć ilość przegranych rund oraz zatrzymać ruch, false - gracz żyje
	 */
	public void setDied(boolean died) {
		if (this.isDied == died) {
			return;
		}
		isDied = died;
		if (died) {
			increaseDies();
			this.stop();
		}
	}

	/**
	 * Zwiększenie prędkości gracza
	 */
	public void increaseSpeed() {
		this.playerSpeed++;
		if (this.playerSpeed > 25) this.playerSpeed = 25;
	}

	/**
	 * @return maksymalną ilość aktywnych bomb, które może postawić gracz
	 */
	public Integer getPlayerMaxActiveBombs() {
		return playerMaxActiveBombs;
	}

	/**
	 * Zwiększenie maksymalnej ilości aktywnych bomb, które może postawić gracz
	 */
	public void increaseMaxActiveBombs() {
		this.playerMaxActiveBombs++;
	}

	/**
	 * @return ilość postawionych bomb, które są nadal aktywne
	 */
	public Integer getPlayerActiveBombs() {
		return playerActiveBombs;
	}

	/**
	 * Zwiększenie ilości postawionych graczem bomb
	 */
	public void increaseActiveBombs() {
		this.playerActiveBombs++;
	}

	/**
	 * Zmiejszenie ilości postawionych graczem bomb
	 */
	public void decreaseActiveBombs() {
		this.playerActiveBombs--;
	}
}
