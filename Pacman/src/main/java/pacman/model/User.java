package pacman.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class User {
    private static ArrayList<User> users = new ArrayList<>();
    private static int idCounter;
    private int id;
    private String username;
    private String password;
    private int record;
    public boolean hasSavedMap = false;
    public String defaultMap = "src/main/resources/pacman/maps/map1.txt";
    public User(String username, String password){
        setUsername(username);
        setPassword(password);
        users.add(this);
        try {
            createUserFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public int getRecord() {
        return record;
    }

    public static ArrayList<User> getUsers() {
        return users;
    }
    public static User getUserByUsername(String username){
        for (User user : getUsers()) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }
    public void deleteAccount(){
        users.remove(this);
    }
    public static void writeUsers(){
        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/pacman/users/users.json");
            fileWriter.write(new Gson().toJson(users));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void readUsers(){
        try {
            String json = new String(Files.readAllBytes(Paths.get("src/main/resources/pacman/users/users.json")));
            ArrayList<User> userArrayList;
            userArrayList = new Gson().fromJson(json, new TypeToken<List<User>>(){}.getType());
            if (userArrayList == null)
                userArrayList = new ArrayList<>();
            users.addAll(userArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void changeRecord(int record){
        if (record > getRecord()) {
            setRecord(record);
            setId(idCounter);
            idCounter++;
        }
    }

    public static int getIdCounter() {
        return idCounter;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDefaultMap() {
        return defaultMap;
    }

    public String returnFavouriteMapsFileName(){
        return "src/main/resources/pacman/users/" + getUsername() + "FavouriteMaps.txt";
    }
    public String returnSavedFileName(){
        return "src/main/resources/pacman/users/" + getUsername() + "SavedMap.txt";
    }

    public void createUserFile() throws IOException {
        String favouritesFileName = returnFavouriteMapsFileName();
        File favouritesFile = new File(favouritesFileName);
        favouritesFile.createNewFile();
        String savedMapFileName = returnSavedFileName();
        File savedMapFile = new File(savedMapFileName);
        savedMapFile.createNewFile();
    }
}
