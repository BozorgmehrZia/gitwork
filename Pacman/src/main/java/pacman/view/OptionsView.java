package pacman.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class OptionsView extends Application {
    public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        OptionsView.stage = stage;
        URL url = new File("src/main/resources/pacman/fxml/options.fxml").toURI().toURL();
        Parent parent = FXMLLoader.load(url);
        stage.setTitle("Options");
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
