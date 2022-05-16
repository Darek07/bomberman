package com.bomber.bomberman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;

public class Bomber extends Application {

    public static final int CELL_SIZE = 44;
    public static final int WIDTH = 24;
    public static final int HEIGHT = 16;

//    private final Square[][] board = new Square[HEIGHT][WIDTH];

    private final Group cellsGroup = new Group();

    private void readMap(String mapFile) throws FileNotFoundException   {
        try(FileReader fr = new FileReader(mapFile);
            BufferedReader in = new BufferedReader(fr)) {

            Cell cell;

            for (int y = 0; in.ready(); y++) {
                String line = in.readLine();
                for (int x = 0; x < line.length(); x++) {
                    switch (line.charAt(x)) {
                        case '#':
                            cell = new Cell(CellType.UNBREAKABLE, x, y);
                            break;
                        case '&':
                            cell = new Cell(CellType.BREAKABLE, x, y);
                            break;
                        case '%':
                            cell = new Cell(CellType.FLOOR, x, y);
                            break;
                        default:
                            cell = null;
                    }

                    cellsGroup.getChildren().add(cell);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not open map file");
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Parent initBoard() throws FileNotFoundException {
        Pane field = new Pane();
        field.setPrefSize(CELL_SIZE * WIDTH, CELL_SIZE * HEIGHT);
        field.getChildren().addAll(cellsGroup);

        readMap(Bomber.class.getResource("map.txt").getFile());

        return field;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(initBoard());
        stage.setTitle("Bomberman");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}