package com.bomber.bomberman;

import javafx.geometry.Point2D;

import java.util.concurrent.ScheduledExecutorService;

public class Player {
	private Point2D playerLocation;
	private Integer playerSpeed;
	private BomberModel.Direction playerDirection;
	private boolean isMoving;
	private ScheduledExecutorService executor;
	private BomberModel model;
	private boolean isInsideBomb;
	private int fireDistance = 3;

	public Player(int col, int row) {
		this.playerLocation = new Point2D(col * BomberView.CELL_SIZE, row * BomberView.CELL_SIZE);
		this.playerSpeed = 10;
		this.playerDirection = BomberModel.Direction.NONE;
		this.isMoving = false;
		this.isInsideBomb = false;
//
//		executor = Executors.newSingleThreadScheduledExecutor();
//		executor.scheduleAtFixedRate(() -> {
//			if (isMoving) {
//				System.out.println("MOVING");
//				this.playerLocation = this.playerLocation.add(playerDirection.velocity);
//			}
//		}, 0, 1000 / playerSpeed, TimeUnit.MILLISECONDS);
	}

	public void setPlayerDirection(BomberModel.Direction playerDirection, boolean isMoving, BomberModel.CellValue[][] grid) {
		this.playerDirection = playerDirection;
		this.isMoving = isMoving;
		if (isMoving) {
			Point2D newLocation = this.playerLocation.add(playerDirection.velocity.multiply(playerSpeed));
			int x = (int)newLocation.getX() / BomberView.CELL_SIZE;
			int y = (int)newLocation.getY() / BomberView.CELL_SIZE;
			int xr = ((int)newLocation.getX() + BomberView.PLAYER_SIZE) / BomberView.CELL_SIZE;
			int yr = ((int)newLocation.getY() + BomberView.PLAYER_SIZE) / BomberView.CELL_SIZE;
			if (isInsideBomb) {
				if (grid[y][x] != BomberModel.CellValue.BOMB && grid[yr][xr] != BomberModel.CellValue.BOMB
						&& grid[y][xr] != BomberModel.CellValue.BOMB && grid[yr][x] != BomberModel.CellValue.BOMB) {
					isInsideBomb = false;
				}
			}
			if ((isInsideBomb && grid[y][x] == BomberModel.CellValue.BOMB || grid[y][x] == BomberModel.CellValue.EMPTY || grid[y][x] == BomberModel.CellValue.FIRE)
					&& (isInsideBomb && grid[yr][xr] == BomberModel.CellValue.BOMB || grid[yr][xr] == BomberModel.CellValue.EMPTY || grid[yr][xr] == BomberModel.CellValue.FIRE)
					&& (isInsideBomb && grid[y][xr] == BomberModel.CellValue.BOMB || grid[y][xr] == BomberModel.CellValue.EMPTY || grid[y][xr] == BomberModel.CellValue.FIRE)
					&& (isInsideBomb && grid[yr][x] == BomberModel.CellValue.BOMB || grid[yr][x] == BomberModel.CellValue.EMPTY || grid[yr][x] == BomberModel.CellValue.FIRE)){
				this.playerLocation = newLocation;
			}
			else if (grid[y][x] == BomberModel.CellValue.RIP) {
				this.playerLocation = newLocation;
			}
		}
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

}
