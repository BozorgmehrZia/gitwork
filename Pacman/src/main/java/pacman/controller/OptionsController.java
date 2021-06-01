package pacman.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import pacman.view.MainMenuView;
import pacman.model.User;
import pacman.view.WelcomeView;

import java.util.Optional;

public class OptionsController extends ActionEvent {

    public PasswordField oldPasswordField;
    public PasswordField newPasswordField;

    public void back(ActionEvent actionEvent) throws Exception {
        new MainMenuView().start(WelcomeView.stage);
    }
    public void changePassword(ActionEvent actionEvent){
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        User currentUser = MainMenuController.currentUser;
        if (!currentUser.getPassword().equals(oldPassword)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Password change failed!");
            alert.setContentText("Old password is incorrect!");
            alert.show();
        } else if (oldPassword.equals(newPassword)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Password change failed!");
            alert.setContentText("Please enter a new password!");
            alert.show();
        } else {
            currentUser.setPassword(newPassword);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Password change successful!");
            alert.setContentText("Password successfully changed!");
            alert.show();
        }

    }
    public void deleteAccount(ActionEvent actionEvent) throws Exception {
        User currentUser = MainMenuController.currentUser;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete account confirmation");
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
            informationAlert.setHeaderText("Delete account successful!");
            informationAlert.setContentText("account successfully deleted!");
            informationAlert.show();
            currentUser.deleteAccount();
            new WelcomeView().start(WelcomeView.stage);
        }
    }

    public void mute(MouseEvent mouseEvent) {
        GameController.isNotMuted = !GameController.isNotMuted;
    }
}
