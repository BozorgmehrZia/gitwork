package pacman.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import pacman.model.User;
import pacman.model.pacmanMap.PacmanMap;
import pacman.view.*;

public class MainMenuController extends ActionEvent {
    public static User currentUser;
    public Button startNewGame;
    public Button scoreboard;
    public Button maps;
    public Button continueSavedGame;
    public Button back;
    public Button options;

    public void startNewGame(ActionEvent actionEvent) throws Exception {
        PacmanMap.isSavedMap = false;
        GameController.isPaused = false;
        GameController.initialLives = WelcomeController.setInitialLives();
        GameController.initialScore = 0;
        MainMenuController.currentUser.hasSavedMap = false;
        GameController.defaultMap = MainMenuController.currentUser.defaultMap;
        new Game().start(WelcomeView.stage);
    }
    public void startSavedGame(ActionEvent actionEvent) throws Exception {
        if (MainMenuController.currentUser.hasSavedMap) {
            GameController.isPaused = false;
            PacmanMap.isSavedMap = true;
            GameController.defaultMap = MainMenuController.currentUser.returnSavedFileName();
            new Game().start(WelcomeView.stage);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Continue saved game failed!");
            alert.setContentText("You don't have a saved game!\nPlease start a new game!");
            alert.show();
        }
    }
    public void gotoMaps(ActionEvent actionEvent) throws Exception {
        new MapsView().start(WelcomeView.stage);
    }
    public void gotoScoreboard(ActionEvent actionEvent) throws Exception {
        new ScoreboardView().start(WelcomeView.stage);
    }
    public void options(ActionEvent actionEvent) throws Exception {
        new OptionsView().start(WelcomeView.stage);
    }
    public void back(ActionEvent actionEvent) throws Exception {
        new WelcomeView().start(WelcomeView.stage);
    }
}
