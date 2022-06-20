package com.bomber.bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;

public class Bomberman extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL resource = getClass().getResource("bomberman_start.fxml");
        assert resource != null;
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Bomberman");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}