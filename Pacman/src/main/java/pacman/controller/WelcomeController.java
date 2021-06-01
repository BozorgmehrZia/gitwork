package pacman.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pacman.view.Game;
import pacman.view.MainMenuView;
import pacman.model.User;
import pacman.view.WelcomeView;
import pacman.model.pacmanMap.PacmanMap;

import java.util.Optional;

public class WelcomeController extends ActionEvent {
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;

    @FXML
    public Button login;
    @FXML
    public Button Register;
    @FXML
    public Button newAnonymousGame;

    @FXML
    public void register(ActionEvent actionEvent){
        String username = usernameField.getText();
        String password = passwordField.getText();
        User user = User.getUserByUsername(username);
        if (username.isEmpty() && password.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Register failed!");
            alert.setContentText("Please fill the fields!");
            alert.show();
        } else if (user != null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Register failed!");
            alert.setContentText("User already exists!");
            alert.show();
        } else {
            new User(username, password);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("User successfully created!");
            alert.setContentText("Now you can login!");
            alert.show();
        }

    }
    @FXML
    public void login(ActionEvent actionEvent) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        User user = User.getUserByUsername(username);
        if (username.isEmpty() && password.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login failed!");
            alert.setContentText("Please fill the fields!");
            alert.show();
        } else if (user == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login failed!");
            alert.setContentText("User does not exist!");
            alert.show();
        } else if (!(user.getPassword().equals(password))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Login failed!");
            alert.setContentText("Password is incorrect!");
            alert.show();
        } else {
            MainMenuController.currentUser = user;
            new MainMenuView().start(WelcomeView.stage);
        }
    }
    @FXML
    public void startNewAnonymousGame(ActionEvent actionEvent) throws Exception {
        PacmanMap.isSavedMap = false;
        GameController.isPaused = false;
        GameController.initialLives = setInitialLives();
        GameController.initialScore = 0;
        new Game().start(WelcomeView.stage);
    }

    public static int setInitialLives(){
        Spinner<Integer> liveChooser = new Spinner<>(2, 5, 5);
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Pacman lives choose");
        dialog.setHeaderText("Please choose pacman initial lives");
        dialog.getDialogPane().setContent(liveChooser);
        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return liveChooser.getValue();
            }
            return null;
        });
        Optional<Integer> result = dialog.showAndWait();
        return result.orElse(5);
    }


}
