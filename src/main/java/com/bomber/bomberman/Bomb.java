package com.bomber.bomberman;

import javafx.geometry.Point2D;

import java.util.*;

public class Bomb extends Thread {

	private final Set<Point2D> firePositions = new HashSet<>(9);
	private final List<Player> hitPlayers = new ArrayList<>(Controller.PLAYERS_AMOUNT);
	private final List<Point2D> ripPositions = new ArrayList<>(Controller.PLAYERS_AMOUNT);
	private final BomberModel bomberModel;
	private final Player player;
	private Point2D location;
	private int distance;

	public Bomb(BomberModel bomberModel, Player player) {
		this.bomberModel = bomberModel;
		this.player = player;
		this.start();
	}

	@Override
	public void run() {
		putBomb();
		try {
			Thread.sleep(3000);
			boom();
			boolean isRIP = isHitPlayer();
			Thread.sleep(1000);
			endBoom();
			if (isRIP) {
				Thread.sleep(2000);
				clearRIP();
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
		bomberModel.setCellValue(CellValue.BOMB, row, col);
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

		bomberModel.setCellValue(CellValue.FIRE, initRow, initCol);
		firePositions.add(new Point2D(initCol, initRow));
		for (int i = 1; i < distance; i++) {
			positions.forEach((key, position) -> {
				int row = (int) position.getY();
				int col = (int) position.getX();
				CellValue cellValue = bomberModel.getCellValue(row, col);
				if (cellValue == null || cellValue == CellValue.UNBREAKABLEWALL) {
					toDelete.add(key);
				}
				else if (cellValue == CellValue.BREAKABLEWALL) {
					bomberModel.setCellValue(CellValue.EMPTY, row, col);
					toDelete.add(key);
				} else if (cellValue == CellValue.EMPTY) {
					bomberModel.setCellValue(CellValue.FIRE, row, col);
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
			int row = (int)position.getY();
			int col = (int)position.getX();
			if (bomberModel.getCellValue(row, col) != CellValue.RIP) {
				bomberModel.setCellValue(CellValue.EMPTY, row, col);
			}
		});
	}

	private boolean isHitPlayer() {
		List<Player> players = bomberModel.getPlayers();
		boolean isHitAnyone = players.stream().anyMatch(player -> {
			Point2D location = player.getPlayerLocation();
			int row = (int)location.getY() / BomberView.CELL_SIZE;
			int col = (int)location.getX() / BomberView.CELL_SIZE;
			if (bomberModel.getCellValue(row, col) == CellValue.FIRE) {
				bomberModel.setCellValue(CellValue.RIP, row, col);
				hitPlayers.add(player);
				ripPositions.add(location);
				return true;
			}
			return false;
		});
		players.removeAll(hitPlayers);
		return isHitAnyone;
	}

	private void clearRIP() {
		ripPositions.forEach(ripPosition -> {
			int row = (int)ripPosition.getY() / BomberView.CELL_SIZE;
			int col = (int)ripPosition.getX() / BomberView.CELL_SIZE;
			bomberModel.setCellValue(CellValue.EMPTY, row, col);
		});
	}
}
