package com.bomber.bomberman;

import javafx.geometry.Point2D;

import static com.bomber.bomberman.BomberView.CELL_SIZE;
import static com.bomber.bomberman.BomberView.PLAYER_SIZE;

public class Bonus {

	private static final CellValue[] randomValues = {
			CellValue.SPEED_BONUS,
			CellValue.EMPTY,
			CellValue.EMPTY,
			CellValue.BOMB_BONUS,
			CellValue.EMPTY,
			CellValue.SPEED_BONUS,
			CellValue.BOMB_BONUS,
			CellValue.EMPTY,
			CellValue.BOMB_BONUS,
			CellValue.SPEED_BONUS };

	public static void randomBonus(BomberModel bomberModel, int row, int column) {
		if (bomberModel.getCellValue(row, column) != CellValue.BREAKABLE_WALL) {
			bomberModel.setCellValue(CellValue.EMPTY, row, column);
			return;
		}
		int rand = (int) (Math.random() * randomValues.length);
		bomberModel.setCellValue(randomValues[rand], row, column);
	}

	public static boolean isPlayerPickBonus(BomberModel bomberModel, Player player) {
		Point2D playerLocation = player.getPlayerLocation();
		Point2D[] points = {
				playerLocation,
				playerLocation.add(PLAYER_SIZE, 0),
				playerLocation.add(0, PLAYER_SIZE),
				playerLocation.add(PLAYER_SIZE, PLAYER_SIZE)
		};

		for (Point2D point : points) {
			int col = (int)point.getX() / CELL_SIZE;
			int row = (int)point.getY() / CELL_SIZE;
			CellValue cellValue = bomberModel.getCellValue(row, col);
			System.out.println(cellValue);
			if (cellValue == CellValue.SPEED_BONUS) {
				bomberModel.setCellValue(CellValue.EMPTY, row, col);
				player.increaseSpeed();
				return true;
			}
			if (cellValue == CellValue.BOMB_BONUS) {
				bomberModel.setCellValue(CellValue.EMPTY, row, col);
				player.increaseMaxActiveBombs();
				return true;
			}
		}
		return false;
	}
}
