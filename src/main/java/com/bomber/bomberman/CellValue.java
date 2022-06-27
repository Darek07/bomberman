package com.bomber.bomberman;

/**
 * Oznaczenie komórki na planszy
 */
public enum CellValue {
	/**
	 * Zwykła pusta komórka, po której może przemieszczać się gracz
	 */
	EMPTY,
	/**
	 * Ściania, którą można zniszczyć wybuchem bomby
	 */
	BREAKABLE_WALL,
	/**
	 * Nie zniszczalna ściana
	 */
	UNBREAKABLE_WALL,
	/**
	 * Komórka, na której stoi bomba
	 */
	BOMB,
	/**
	 * Komórka, na której płonie ogień od wybuchu bomby
	 */
	FIRE,
	/**
	 * Komórka, na której umarł gracz od wybuchu bomby
	 */
	RIP,
	/**
	 * Bonus, zwiększający prędkość gracza
	 */
	SPEED_BONUS,
	/**
	 * Bonus, zwiększający maksymalną ilość aktywnych bomb, które może postawić gracza
	 */
	BOMB_BONUS
}
