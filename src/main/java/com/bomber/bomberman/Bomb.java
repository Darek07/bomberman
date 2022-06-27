package com.bomber.bomberman;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;

import java.util.*;

/**
 * Klasa reprezentująca bombę
 */
public class Bomb extends AnimationTimer {

	/**
	 * {@value #BOMB_WAIT_MS} (ms) bomba stoi i czeka przed wybuchem
	 */
	public static final int BOMB_WAIT_MS = 3000;
	/**
	 * {@value #BOMB_FIRE_MS} (ms) trwa wybuch
	 */
	public static final int BOMB_FIRE_MS = 1000;
	/**
	 * {@value #RIP_MS} (ms) trwa animacja, kiedy gracz został pochłonięty bombą
	 */
	public static final int RIP_MS = 2000;

	private final Set<Point2D> firePositions = new HashSet<>(9);
	private final List<Player> hitPlayers = new ArrayList<>(Controller.PLAYERS_NUMBER);
	private final List<Point2D> ripPositions = new ArrayList<>(Controller.PLAYERS_NUMBER);
	private final BomberModel bomberModel;
	private final Player player;
	private Point2D location;
	private int distance;
	private volatile boolean isFire;
	private long lastTime = 0;
	private Timer wait;

	/**
	 * Tworzenie bomby
	 *
	 * @param bomberModel BomberModel wykorzystywany w grze
	 * @param player      gracz, który umieścił bombę
	 */
	public Bomb(BomberModel bomberModel, Player player) {
		this.bomberModel = bomberModel;
		this.player = player;
		this.isFire = false;
		this.start();
	}

	private void initializeAnimationTimer(long now) {
		try {
			putBomb();
		} catch (NullPointerException n) {
			System.out.println("Bomb has already been set");
			stop();
		}
		lastTime = now;
		wait = new Timer(true);
		wait.schedule(new TimerTask() {
			@Override
			public void run() {
				boom();
			}
		}, BOMB_WAIT_MS);
		new Timer(true).schedule(new TimerTask() {
			@Override
			public void run() {
				endBoom();
			}
		}, BOMB_FIRE_MS + BOMB_WAIT_MS);
	}

	@Override
	public void handle(long now) {
		if (lastTime == 0) {
			initializeAnimationTimer(now);
			return;
		}
		final double elapsedMicroSeconds = (now - lastTime) / 1_000.0;
		if (elapsedMicroSeconds < 100) {
			return;
		}
		if (!isFire && isAnotherBombHit()) {
			wait.cancel();
			boom();
		}
		if (isFire && isHitPlayer()) {
			Timer tim = new Timer(true);
			tim.schedule(new TimerTask() {
				@Override
				public void run() {
					clearRIP();
				}
			}, RIP_MS);
		}
		lastTime = now;
	}

	/**
	 * Umieszcza bombę na pozycji gracza, ustawiając flagę gracza, że jest wewnątrz bomby oraz zwiekszająć ilość aktywnych bomb gracza
	 */
	public void putBomb() {
		location = player.getPlayerLocation();
		distance = player.getFireDistance();
		int row = (int) location.getY() / BomberView.CELL_SIZE;
		int col = (int) location.getX() / BomberView.CELL_SIZE;
		if (bomberModel.getCellValue(row, col) == CellValue.BOMB) {
			throw new NullPointerException();
		}
		bomberModel.setCellValue(CellValue.BOMB, row, col);
		player.setIsInsideBomb();
		player.increaseActiveBombs();
	}

	private void boom() {
		int initRow = (int) location.getY() / BomberView.CELL_SIZE;
		int initCol = (int) location.getX() / BomberView.CELL_SIZE;

		Map<Direction, Point2D> firePositions = new HashMap<>(4);
		List<Direction> fireDirectionNotAllowed = new ArrayList<>(4);
		firePositions.put(Direction.UP, new Point2D(initCol, initRow - 1));
		firePositions.put(Direction.RIGHT, new Point2D(initCol + 1, initRow));
		firePositions.put(Direction.DOWN, new Point2D(initCol, initRow + 1));
		firePositions.put(Direction.LEFT, new Point2D(initCol - 1, initRow));

		bomberModel.setCellValue(CellValue.FIRE, initRow, initCol);
		this.firePositions.add(new Point2D(initCol, initRow));
		for (int i = 1; i < distance; i++) {
			firePositions.forEach((key, position) -> {
				int row = (int) position.getY();
				int col = (int) position.getX();
				CellValue cellValue = bomberModel.getCellValue(row, col);
				if (cellValue == null || cellValue == CellValue.UNBREAKABLE_WALL) {
					fireDirectionNotAllowed.add(key);
				} else if (cellValue == CellValue.BREAKABLE_WALL || cellValue == CellValue.SPEED_BONUS) {
					Bonus.randomBonus(bomberModel, row, col);
					fireDirectionNotAllowed.add(key);
				} else if (cellValue == CellValue.EMPTY || cellValue == CellValue.BOMB) {
					bomberModel.setCellValue(CellValue.FIRE, row, col);
					this.firePositions.add(position);
				}
			});
			fireDirectionNotAllowed.forEach(firePositions::remove);
			fireDirectionNotAllowed.clear();

			firePositions.replaceAll((key, value) ->
					switch (key) {
						case UP -> value.add(0, -1);
						case RIGHT -> value.add(1, 0);
						case DOWN -> value.add(0, 1);
						case LEFT -> value.add(-1, 0);
						case NONE -> value;
					}
			);
		}
		isFire = true;
		player.decreaseActiveBombs();
	}

	private void endBoom() {
		firePositions.forEach(position -> {
			int row = (int) position.getY();
			int col = (int) position.getX();
			if (bomberModel.getCellValue(row, col) != CellValue.RIP) {
				bomberModel.setCellValue(CellValue.EMPTY, row, col);
			}
		});
		isFire = false;
		this.stop();
	}

	/**
	 * Sprawdza, czy gracz został pochłonięty przez bombę i jeżeli tak, to informuję o tym BomberModel
	 *
	 * @return true - ktoś z graczy został pochłonięty bombą, false - nie
	 */
	public boolean isHitPlayer() {
		boolean isHitAnyone = bomberModel.getAlivePlayers().stream().anyMatch(player -> {
			Point2D location = player.getPlayerLocation();
			int row = (int) location.getY() / BomberView.CELL_SIZE;
			int col = (int) location.getX() / BomberView.CELL_SIZE;
			if (bomberModel.getCellValue(row, col) == CellValue.FIRE) {
				bomberModel.setCellValue(CellValue.RIP, row, col);
				hitPlayers.add(player);
				ripPositions.add(location);
				return true;
			}
			return false;
		});
		bomberModel.addRipPlayers(hitPlayers);
		return isHitAnyone;
	}

	private void clearRIP() {
		ripPositions.forEach(ripPosition -> {
			int row = (int) ripPosition.getY() / BomberView.CELL_SIZE;
			int col = (int) ripPosition.getX() / BomberView.CELL_SIZE;
			bomberModel.setCellValue(CellValue.EMPTY, row, col);
		});
	}

	private boolean isAnotherBombHit() {
		int row = (int) location.getY() / BomberView.CELL_SIZE;
		int col = (int) location.getX() / BomberView.CELL_SIZE;
		return bomberModel.getCellValue(row, col) == CellValue.FIRE;
	}
}
