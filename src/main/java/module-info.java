module com.bomber.bomberman {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bomber.bomberman to javafx.fxml;
    exports com.bomber.bomberman;
}