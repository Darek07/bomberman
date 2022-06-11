package com.bomber.bomberman;

import javafx.geometry.Point2D;

import java.util.*;

import com.bomber.bomberman.BomberModel.*;

public class Bomb extends Thread {

	private final Set<Point2D> firePositions = new HashSet<>(9);
	private final List<Player> hitPlayers = new ArrayList<>(3);
	private final int rowCount;
	private final int columnCount;
	private final BomberModel bomberModel;
	private final Player player;
	private final Controller controller;
	private CellValue[][] grid;
	private Point2D location;
	private int distance;

	public Bomb(BomberModel bomberModel, Player player, Controller controller) {
		this.bomberModel = bomberModel;
		this.player = player;
		this.controller = controller;
		this.rowCount = bomberModel.getRowCount();
		this.columnCount = bomberModel.getColumnCount();
		this.grid = bomberModel.getGrid();
		this.start();
	}

	@Override
	public void run() {
		putBomb();
		try {
			Thread.sleep(3000);
			boom();
			boolean isRIP = isHitPlayer();
			controller.bombDetonated();
			Thread.sleep(1000);
			endBoom();
			controller.bombDetonated();
			if (isRIP) {
				Thread.sleep(2000);
				clearRIP();
				controller.bombDetonated();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void putBomb() {
		location = player.getPlayerLocation();
		distance = player.getFireDistance();
		int row = (int)location.getY() / BomberView.CELL_SIZE;
		int col = (int)location.getX() / BomberView.CELL_SIZE;
		grid[row][col] = CellValue.BOMB;
		player.setIsInsideBomb();
	}

	public void boom() {
		int initRow = (int)location.getY() / BomberView.CELL_SIZE;
		int initCol = (int)location.getX() / BomberView.CELL_SIZE;
		Map<Direction, Point2D> positions = new HashMap<>(4);
		List<Direction> toDelete = new ArrayList<>(4);
		positions.put(Direction.UP, new Point2D(initCol, initRow - 1));
		positions.put(Direction.RIGHT, new Point2D(initCol + 1, initRow));
		positions.put(Direction.DOWN, new Point2D(initCol, initRow + 1));
		positions.put(Direction.LEFT, new Point2D(initCol - 1, initRow));

		grid[initRow][initCol] = CellValue.FIRE;
		firePositions.add(new Point2D(initCol, initRow));
		for (int i = 1; i < distance; i++) {
			positions.forEach((key, position) -> {
				int x = (int) position.getX();
				int y = (int) position.getY();
				if (y < 0 || x < 0 || y >= rowCount || x >= columnCount) {
					toDelete.add(key);
				}
				else if (grid[y][x] == CellValue.BREAKABLEWALL) {
					grid[y][x] = CellValue.EMPTY;
					toDelete.add(key);
				} else if (grid[y][x] == CellValue.EMPTY) {
					grid[y][x] = CellValue.FIRE;
					firePositions.add(position);
				}
			});
			toDelete.forEach(positions::remove);
			toDelete.clear();
			positions.replaceAll((key, value) ->
				switch (key) {
					case UP -> value.add(0, -1);
					case RIGHT -> value.add(1, 0);
					case DOWN -> value.add(0, 1);
					case LEFT -> value.add(-1, 0);
					case NONE -> value;
				}
			);
		}
	}

	private void endBoom() {
		firePositions.forEach(position -> {
			int x = (int)position.getX();
			int y = (int)position.getY();
			if (grid[y][x] != CellValue.RIP) {
				grid[y][x] = CellValue.EMPTY;
			}
		});
	}

	private boolean isHitPlayer() {
		List<Player> players = bomberModel.getPlayers();
		boolean isHitAnyone = players.stream().anyMatch(player -> {
			Point2D location = player.getPlayerLocation();
			int row = (int)location.getY() / BomberView.CELL_SIZE;
			int col = (int)location.getX() / BomberView.CELL_SIZE;
			if (grid[row][col] == CellValue.FIRE) {
				grid[row][col] = CellValue.RIP;
				hitPlayers.add(player);
				return true;
			}
			return false;
		});
		players.removeAll(hitPlayers);
		return isHitAnyone;
	}

	private void clearRIP() {
		hitPlayers.forEach(hitPlayer -> {
			Point2D position = hitPlayer.getPlayerLocation();
			int row = (int)position.getY() / BomberView.CELL_SIZE;
			int col = (int)position.getX() / BomberView.CELL_SIZE;
			grid[row][col] = CellValue.EMPTY;
		});
	}
}
