package com.bomber.bomberman;

import javafx.geometry.Point2D;

/**
 * Kierunek ruchu
 */
public enum Direction {
	/**
	 * W górę
	 */
	UP(0, -1),
	/**
	 * W prawo
	 */
	RIGHT(1, 0),
	/**
	 * W dół
	 */
	DOWN(0, 1),
	/**
	 * W lewo
	 */
	LEFT(-1, 0),
	/**
	 * Żaden
	 */
	NONE(0, 0);

	final Point2D velocity;

	Direction(int x, int y) {
		this.velocity = new Point2D(x, y);
	}
}
