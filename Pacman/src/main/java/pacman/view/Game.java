package pacman.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pacman.controller.GameController;

import java.io.File;
import java.net.URL;

public class Game extends Application {
    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception{
        Game.stage = stage;
        URL url = new File("src/main/resources/pacman/fxml/pacman.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        stage.setTitle("PacMan");
        GameController gameController = loader.getController();
        root.setOnKeyPressed(gameController);
        double sceneWidth = gameController.getBoardWidth() + 200.0;
        double sceneHeight = gameController.getBoardHeight() + 100.0;
        stage.setScene(new Scene(root, sceneWidth, sceneHeight));
        stage.show();
        root.requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
