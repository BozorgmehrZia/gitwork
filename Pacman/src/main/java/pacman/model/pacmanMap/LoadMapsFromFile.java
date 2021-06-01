package pacman.model.pacmanMap;

import pacman.controller.MainMenuController;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LoadMapsFromFile {
    public static int returnNumberOfFiles(){
        File mapFiles = new File("src/main/resources/pacman/maps");
        String[] maps = mapFiles.list();
        assert maps != null;
        return maps.length;
    }
    public static ArrayList<ShowMap> readGameMapsFromFolder(){
        ArrayList<ShowMap> showMaps = new ArrayList<>();
        ArrayList<String> maps = new ArrayList<>();
        maps.add("src/main/resources/pacman/maps/map1.txt");
        maps.add("src/main/resources/pacman/maps/map2.txt");
        maps.add("src/main/resources/pacman/maps/map3.txt");
        String source = "src/main/resources/pacman/users/" +
                MainMenuController.currentUser.getUsername() + "FavouriteMaps.txt";
        File file = new File(source);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if (!maps.contains(line))
                    maps.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (String map : maps) {
            ShowMap showMap = new ShowMap();
            showMap.setMapFileName(map);
            showMap.setImages();
            showMap.setLayoutX(200.0);
            showMap.setLayoutY(50.0);
            showMaps.add(showMap);
        }
        return showMaps;
    }
}
