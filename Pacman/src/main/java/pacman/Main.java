package pacman;

//In Association with:
//https://github.com/jbaskauf/pacman

import pacman.model.User;
import pacman.view.WelcomeView;

public class Main {
    public static void main(String[] args) {
        User.readUsers();
        WelcomeView.main(args);
        User.writeUsers();
    }
}