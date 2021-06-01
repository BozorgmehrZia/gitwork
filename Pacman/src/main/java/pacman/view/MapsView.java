package pacman.view;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pacman.controller.MainMenuController;
import pacman.model.pacmanMap.LoadMapsFromFile;
import pacman.model.pacmanMap.MapGenerator;
import pacman.model.pacmanMap.ShowMap;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MapsView extends Application {
    public static Stage stage;
    public static TabPane tabPane = new TabPane();
    public void addNewTab(){
        String fileName = MapGenerator.returnNewMapFile();
        MapGenerator.run();
        Tab tab = new Tab();
        int x = tabPane.getTabs().size() + 1;
        tab.setText("Map " + x);
        ShowMap showMap = new ShowMap();
        showMap.setMapFileName(fileName);
        showMap.setImages();
        showMap.setLayoutX(200.0);
        showMap.setLayoutY(50.0);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(showMap);
        tab.setContent(anchorPane);
        tabPane.getTabs().add(tab);
    }

    public void initializeTabPane(){
        ArrayList<ShowMap> showMaps = LoadMapsFromFile.readGameMapsFromFolder();
        for (int i = 0; i < showMaps.size(); i++) {
            Tab tab = new Tab();
            int x = i + 1;
            tab.setText("Map " + x);
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(showMaps.get(i));
            tab.setContent(anchorPane);
            tabPane.getTabs().add(tab);
        }
    }
    public Button returnNewMapButton(double x1, double y1){
        Button createNewMapButton = new Button();
        createNewMapButton.setFont(new Font(25));
        createNewMapButton.setLayoutX(x1);
        createNewMapButton.setText(" Create new\nrandom map");
        createNewMapButton.setLayoutY(y1);
        createNewMapButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addNewTab();
            }
        });
        return createNewMapButton;
    }
    public Button returnAddFavouritesButton(double x1, double y2, double width){
        Button addToFavouritesButton = new Button();
        addToFavouritesButton.setFont(new Font(25));
        addToFavouritesButton.setLayoutX(x1);
        addToFavouritesButton.setText("Add map to\n favourites");
        addToFavouritesButton.setLayoutY(y2);
        addToFavouritesButton.setPrefWidth(width + 178);
        addToFavouritesButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int index = tabPane.getSelectionModel().getSelectedIndex() + 1;
                String source = "src/main/resources/pacman/users/" +
                        MainMenuController.currentUser.getUsername() + "FavouriteMaps.txt";
                String text = "src/main/resources/pacman/maps/map" + index + ".txt";
                try {
                    String content = new String(Files.readAllBytes(Paths.get(source)));
                    if (!content.contains(text)){
                        FileWriter fileWriter = new FileWriter(source);
                        if (!content.equals(""))
                            content += "\n";
                        fileWriter.write(content + text);
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return addToFavouritesButton;
    }
    public Button returnSetCurrentButton(double x2, double y2){
        Button setToCurrentMapButton = new Button();
        setToCurrentMapButton.setFont(new Font(25));
        setToCurrentMapButton.setLayoutX(x2);
        setToCurrentMapButton.setText("  Set this to\ncurrent map");
        setToCurrentMapButton.setLayoutY(y2);
        setToCurrentMapButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int index = tabPane.getSelectionModel().getSelectedIndex() + 1;
                MainMenuController.currentUser.defaultMap = "src/main/resources/pacman/maps/map" + index + ".txt";
            }
        });
        return setToCurrentMapButton;
    }
    public Button returnBackButton(double x2, double y1, double width, double height){
        Button backButton = new Button();
        backButton.setFont(new Font(30));
        backButton.setLayoutX(x2);
        backButton.setText("Back");
        backButton.setLayoutY(y1);
        backButton.setPrefWidth(width + 170);
        backButton.setPrefHeight(height + 80);
        backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    tabPane = new TabPane();
                    new MainMenuView().start(WelcomeView.stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return backButton;
    }
    public void addButtons(AnchorPane anchorPane){
        double x1 = 10.0;
        double x2 = 690.0;
        double y1 = 325.0;
        double y2 = 225.0;
        Button createNewMapButton = returnNewMapButton(x1, y1);
        anchorPane.getChildren().add(createNewMapButton);
        Button addToFavouritesButton = returnAddFavouritesButton(x1, y2, createNewMapButton.getPrefWidth());
        anchorPane.getChildren().add(addToFavouritesButton);
        Button setToCurrentMapButton = returnSetCurrentButton(x2, y2);
        anchorPane.getChildren().add(setToCurrentMapButton);
        Button backButton = returnBackButton(x2, y1, setToCurrentMapButton.getWidth(), setToCurrentMapButton.getHeight());
        anchorPane.getChildren().add(backButton);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MapsView.stage = stage;
        stage.setTitle("Maps");
        stage.setHeight(700.0);
        stage.setWidth(900.0);
        Scene scene = new Scene(new Group());
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(700.0);
        anchorPane.setPrefWidth(900.0);
        anchorPane.setStyle("-fx-background-color: black");
        tabPane.setPrefWidth(900.0);
        tabPane.setTabMinWidth(100.0);
        initializeTabPane();
        anchorPane.getChildren().add(tabPane);
        addButtons(anchorPane);
        ((Group) scene.getRoot()).getChildren().addAll(anchorPane);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
