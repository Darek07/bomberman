package com.bomber.bomberman;

import javafx.geometry.Point2D;

public enum Direction {
	UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0), NONE(0, 0);
	final Point2D velocity;

	Direction(int x, int y) {
		this.velocity = new Point2D(x, y);
	}
}
