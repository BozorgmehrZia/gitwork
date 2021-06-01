package pacman.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pacman.controller.MainMenuController;

import java.io.File;
import java.net.URL;

public class WelcomeView extends Application {
    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        MainMenuController.currentUser = null;
        WelcomeView.stage = stage;
        URL url = new File("src/main/resources/pacman/fxml/welcomePage.fxml").toURI().toURL();
        Parent parent = FXMLLoader.load(url);
        stage.setTitle("Welcome");
        stage.setHeight(700.0);
        stage.setWidth(900.0);
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
