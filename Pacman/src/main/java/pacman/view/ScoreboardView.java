package pacman.view;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pacman.controller.MainMenuController;
import pacman.model.Player;
import pacman.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardView extends Application {
    public static Stage stage;
    public TableView<Player> table = new TableView<>();
    public Label label = new Label();
    public Button button = new Button();
    public ArrayList<Player> returnTenBestPlayers(){
        ArrayList<User> sortedUsers = new ArrayList<>(User.getUsers());
        ArrayList<Player> sortedPlayers = new ArrayList<>();
        Comparator<User> UserComparator = Comparator.comparing(User::getRecord, Comparator.reverseOrder()).thenComparing(User::getId);
        List<User> sortedAccounts = sortedUsers.stream().sorted(UserComparator).collect(Collectors.toList());
        int rank = 1;
        int counter = 1;
        for (int i = 0; i < sortedAccounts.size(); i++) {
            User sortedAccount = sortedAccounts.get(i);
            sortedPlayers.add(new Player(rank, sortedAccount.getUsername(), sortedAccount.getRecord()));
            if (i < sortedAccounts.size() - 1) {
                if (sortedAccount.getRecord() != sortedAccounts.get(i + 1).getRecord()) {
                    rank += counter;
                    counter = 1;
                }
                else{
                    counter++;
                }
            }
        }
        ArrayList<Player> tenBestPlayers = new ArrayList<>();
        int i = 0;
        boolean isCurrentPlayerInTenBest = false;
        while (i < sortedPlayers.size() && i < 10){
            if (sortedPlayers.get(i).getUsername().equals(MainMenuController.currentUser.getUsername()))
                isCurrentPlayerInTenBest = true;
            tenBestPlayers.add(sortedPlayers.get(i));
            i++;
        }
        if (!isCurrentPlayerInTenBest){
            for (Player sortedPlayer : sortedPlayers) {
                if (sortedPlayer.getUsername().equals(MainMenuController.currentUser.getUsername()))
                    tenBestPlayers.add(sortedPlayer);
            }
        }
        return tenBestPlayers;
    }
    public void labelAndButtonInit(){
        label.setLayoutX(375.0);
        label.setLayoutY(55.0);
        label.setText("Scoreboard");
        label.setFont(new Font(29.0));
        button.setLayoutX(400.0);
        button.setLayoutY(560.0);
        button.setMnemonicParsing(false);
        button.setText("Back");
        button.setFont(new Font(30.0));
        button.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                try {
                    new MainMenuView().start(WelcomeView.stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Scene returnScoreboardScene(){
        Scene scene = new Scene(new Group());
        table.setEditable(true);
        table.setLayoutX(174.0);
        table.setLayoutY(118.0);
        table.setPrefHeight(419.0);
        table.setPrefWidth(540.0);
        TableColumn<Player, Integer> rateCol = new TableColumn<>("Rate");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));
        TableColumn<Player, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<Player, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        rateCol.setPrefWidth(100.0);
        usernameCol.setPrefWidth(340.0);
        scoreCol.setPrefWidth(100.0);
        table.getColumns().add(rateCol);
        table.getColumns().add(usernameCol);
        table.getColumns().add(scoreCol);
        for (Player player : returnTenBestPlayers()) {
            table.getItems().add(player);
        }
        labelAndButtonInit();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(700.0);
        anchorPane.setPrefWidth(900.0);
        anchorPane.getChildren().add(table);
        anchorPane.getChildren().add(label);
        anchorPane.getChildren().add(button);
        anchorPane.setStyle("-fx-background-color: #1ed4a0");
        ((Group) scene.getRoot()).getChildren().addAll(anchorPane);
        return scene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        ScoreboardView.stage = stage;
        stage.setTitle("Scoreboard");
        stage.setHeight(700.0);
        stage.setWidth(900.0);
        stage.setScene(returnScoreboardScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
