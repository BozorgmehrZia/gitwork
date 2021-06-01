package pacman.model;

public class Player {
    //Player is only for scoreboard
    private int rate;
    private String username;
    private int score;

    public Player(int rate, String username, int score) {
        setRate(rate);
        setUsername(username);
        setScore(score);
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
