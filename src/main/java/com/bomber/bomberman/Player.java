package com.bomber.bomberman;

import javafx.geometry.Point2D;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Player {
	private Point2D playerLocation;
	private Integer playerSpeed;
	private BomberModel.Direction playerDirection;
	private boolean isMoving;
	private ScheduledExecutorService executor;
	private BomberModel model;
	private boolean inBomb;

	public Player(int col, int row) {
		this.playerLocation = new Point2D(col * BomberView.CELL_SIZE, row * BomberView.CELL_SIZE);
		this.playerSpeed = 10;
		this.playerDirection = BomberModel.Direction.NONE;
		this.isMoving = false;
		this.inBomb = false;
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
			if (inBomb) {
				if (grid[y][x] != BomberModel.CellValue.BOMB && grid[yr][xr] != BomberModel.CellValue.BOMB
						&& grid[y][xr] != BomberModel.CellValue.BOMB && grid[yr][x] != BomberModel.CellValue.BOMB) {
					inBomb = false;
				}
			}
			if ((inBomb && grid[y][x] == BomberModel.CellValue.BOMB || grid[y][x] == BomberModel.CellValue.EMPTY || grid[y][x] == BomberModel.CellValue.FIRE)
					&& (inBomb && grid[yr][xr] == BomberModel.CellValue.BOMB || grid[yr][xr] == BomberModel.CellValue.EMPTY || grid[yr][xr] == BomberModel.CellValue.FIRE)
					&& (inBomb && grid[y][xr] == BomberModel.CellValue.BOMB || grid[y][xr] == BomberModel.CellValue.EMPTY || grid[y][xr] == BomberModel.CellValue.FIRE)
					&& (inBomb && grid[yr][x] == BomberModel.CellValue.BOMB || grid[yr][x] == BomberModel.CellValue.EMPTY || grid[yr][x] == BomberModel.CellValue.FIRE)){
				this.playerLocation = newLocation;
			}
			else if (grid[y][x] == BomberModel.CellValue.RIP) {
				this.playerLocation = newLocation;
			}
		}
	}

	public void setInBomb() {
		inBomb = true;
	}

	public Point2D getPlayerLocation() {
		return playerLocation;
	}
}
