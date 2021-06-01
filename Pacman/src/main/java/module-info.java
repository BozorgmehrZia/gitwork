module HW3.Part1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;
    opens pacman to com.google.gson, javafx.fxml;
    opens pacman.model to com.google.gson, javafx.fxml, javafx.base;
    opens pacman.controller to javafx.fxml;
    exports pacman.view to javafx.graphics;
    exports pacman.controller to javafx.fxml;
    exports pacman.model to javafx.fxml;
    exports pacman;
}