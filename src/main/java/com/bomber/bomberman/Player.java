package com.bomber.bomberman;

import javafx.geometry.Point2D;

import static com.bomber.bomberman.BomberView.PLAYER_SIZE;
import static com.bomber.bomberman.BomberView.CELL_SIZE;

public class Player extends Thread {
	private static final int STEP_DISTANCE = 10;
	private final int playerID;
	private Point2D playerLocation;
	private Integer playerSpeed;
	private Direction playerDirection;
	private boolean isMoving;
	private BomberModel model;
	private boolean isInsideBomb;
	private int fireDistance = 3;

	public Player(BomberModel model, int playerID, int col, int row) {
		this.model = model;
		this.playerID = playerID;
		this.playerLocation = new Point2D(col * BomberView.CELL_SIZE, row * BomberView.CELL_SIZE);
		this.playerSpeed = 200;
		this.playerDirection = Direction.NONE;
		this.isMoving = false;
		this.isInsideBomb = false;
		start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				movePlayer();
				Thread.sleep(playerSpeed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void movePlayer() {
		if (!isMoving) {
			return;
		}

		Point2D newLocation = this.playerLocation.add(playerDirection.velocity.multiply(STEP_DISTANCE));
		if (!isLocationCollide(newLocation)) {
			this.playerLocation = newLocation;
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
		CellValue[][] grid = model.getGrid();
		int x = (int)point.getX() / CELL_SIZE;
		int y = (int)point.getY() / CELL_SIZE;
		return switch (grid[y][x]) {
			case BOMB -> !isInsideBomb;
			case UNBREAKABLEWALL, BREAKABLEWALL -> true;
			case EMPTY, FIRE, RIP -> false;
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

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean moving) {
		isMoving = moving;
	}

	public void setPlayerDirection(Direction playerDirection) {
		this.playerDirection = playerDirection;
	}
}
