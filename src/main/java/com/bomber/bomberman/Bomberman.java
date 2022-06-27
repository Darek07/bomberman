package com.bomber.bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;

/**
 * Gra Bomberman dla jednego do trzech graczy.
 * Na początku gry pojawia się okno startowe z możliwością wpisywania imion graczy,
 * ilości rund, ilości graczy, czasu na jedną rundę oraz wyboru mapy.
 * Gra toczy się przez określoną liczbę rund.
 * Na każdą rundę przydzielany jest określony czas.
 * W tym czasie każdy gracz może się poruszać oraz umieszczać bomby na planszy.
 * Bomba po wybuchu niszczy zniszczalne ściany, jak i graczy, którzy wpadają pod jej ogień.
 * Po zniszczeniu ściany może pojawić się bonus, który może zostać zebrany przez gracza lub zniszczony przez bombę.
 * Istnieją dwa rodzaje bonusów:
 * zwiększenie prędkości oraz
 * zwiększenie maksymalnej liczby aktywnych bomb (na początku każdy gracz może podłożyć tylko jedną aktywną bombę).
 * Rundę wygrywa gracz, który pozostaje przy życiu,
 * w przeciwnym razie zwycięzca nie jest określony.
 * Grę wygrywa gracz z największą liczbą wygranych.
 *
 * @author Dariusz Tsydzik
 * @author Valeria Samoilenko
 * @author Weronika Kretowicz
 */
public class Bomberman extends Application {

	/**
	 * Uruchomienie Javafx oraz okna startowego
	 *
	 * @param stage
	 * @throws IOException
	 */
	@Override
	public void start(Stage stage) throws IOException {
		URL resource = getClass().getResource("bomberman_start.fxml");
		assert resource != null;
		FXMLLoader loader = new FXMLLoader(resource);
		Parent root = loader.load();

		Scene scene = new Scene(root);
		stage.setTitle("Bomberman");
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}

	/**
	 * Uruchomienie gry
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		launch();
	}
}