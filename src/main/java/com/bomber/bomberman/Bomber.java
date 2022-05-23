package com.bomber.bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class Bomber extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bomber.fxml"));
        Parent root = loader.load();
        stage.setTitle("Bomberman");
        Controller controller = loader.getController();
//        root.setOnKeyPressed(controller);
        double sceneWidth = controller.getBoardWidth() + 20.0;
        double sceneHeight = controller.getBoardHeight() + 100.0;
        stage.setScene(new Scene(root, sceneWidth, sceneHeight));
        stage.show();
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch();
    }
}