package com.bomber.bomberman;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;

import static com.bomber.bomberman.BomberView.PLAYER_SIZE;
import static com.bomber.bomberman.BomberView.CELL_SIZE;

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

	@Override
	public void handle(long now) {
		if (lastUpdateTime == 0 || lastUpdateTime >= now) {
			lastUpdateTime = now;
			return;
		}
		final double elapsedMilliSeconds = (now - lastUpdateTime) / 1_000_000.0 ;
		if (elapsedMilliSeconds >= 10) {
			movePlayer();
			lastUpdateTime = now;
		}
	}

	public void movePlayer() {
		if (!isMoving) {
			return;
		}

		Point2D velocitySpeed = playerDirection.velocity.multiply((float)playerSpeed / 10);
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
		int col = (int)point.getX() / CELL_SIZE;
		int row = (int)point.getY() / CELL_SIZE;
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

	public void setPlayerDirectionAndMove(Direction direction, boolean move) {
		if (this.isMoving && !move && this.playerDirection != direction) {
			return;
		}
		this.playerDirection = direction;
		this.isMoving = move;
	}

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

	public void setIsInsideBomb() {
		isInsideBomb = true;
	}

	public Point2D getPlayerLocation() {
		return playerLocation;
	}

	public int getFireDistance() {
		return fireDistance;
	}

	public void setFireDistance(int fireDistance) {
		this.fireDistance = fireDistance;
	}

	public int getPlayerID() {
		return playerID;
	}

	public String getName() {
		return name;
	}

	public int getDies() {
		return dies;
	}

	public void increaseDies() {
		this.dies++;
	}

	public int getWins() {
		return wins;
	}

	public void increaseWins() {
		this.wins++;
	}

	public boolean isDied() {
		return isDied;
	}

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

	public void increaseSpeed() {
		this.playerSpeed++;
		if (this.playerSpeed > 25) this.playerSpeed = 25;
	}

	public Integer getPlayerMaxActiveBombs() {
		return playerMaxActiveBombs;
	}

	public void increaseMaxActiveBombs() {
		this.playerMaxActiveBombs++;
	}

	public Integer getPlayerActiveBombs() {
		return playerActiveBombs;
	}

	public void increaseActiveBombs() {
		this.playerActiveBombs++;
	}

	public void decreaseActiveBombs() {
		this.playerActiveBombs--;
	}
}
