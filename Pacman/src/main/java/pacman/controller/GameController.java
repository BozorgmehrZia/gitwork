package pacman.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import pacman.model.CellValue;
import pacman.model.Direction;
import pacman.model.PacmanModel;
import pacman.model.PacmanView;
import pacman.model.pacmanMap.PacmanMap;
import pacman.view.MainMenuView;
import pacman.view.WelcomeView;

import java.io.File;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class GameController implements EventHandler<KeyEvent> {
    final private static double FRAMES_PER_SECOND = 7.0;
    @FXML public Button exit;
    @FXML public Label lives;
    @FXML public Label soundLabel;
    @FXML private Label scoreLabel;
    @FXML private Label pauseLabel;
    @FXML private PacmanView pacmanView;
    private PacmanModel pacmanModel;
    public CellValue[][] grid;
    public static int initialLives;
    public static int initialScore;
    public static boolean isPaused = false;
    public static boolean isNotMuted = true;
    public static String defaultMap = "src/main/resources/pacman/maps/map1.txt";
    public static MediaPlayer themeSong = new MediaPlayer(new Media(new File("src/main/resources/pacman/Sounds/Pac-Man-Theme-Song.mp3").toURI().toString()));
    public static MediaPlayer deathSong = new MediaPlayer(new Media(new File("src/main/resources/pacman/Sounds/Death.mp3").toURI().toString()));
    private Timer timer;
    private static int ghostEatingModeCounter;
    private boolean paused;

    public GameController() {
        this.paused = false;
    }

    /**
     * Initialize and update the model and view from the first txt file and starts the timer.
     */
    public void initialize() {
        if (GameController.isNotMuted) {
            themeSong.setOnEndOfMedia(() -> themeSong.seek(Duration.ZERO));
            themeSong.play();
        }
        String file = defaultMap;
        lives.setText(String.valueOf(initialLives));
        pacmanModel = new PacmanModel();
        PacmanModel.isFirstGhostMove = true;
        startTimer();
        update(Direction.NONE);
        ghostEatingModeCounter = 50;
    }

    /**
     * Schedules the model to update based on the timer.
     */
    private void startTimer() {
        timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Platform.runLater(() -> update(PacmanModel.getCurrentDirection()));
            }
        };

        long frameTimeInMilliseconds = (long)(1000.0 / FRAMES_PER_SECOND);
        timer.schedule(timerTask, 0, frameTimeInMilliseconds);
    }

    /**
     * Steps the pacman.model.PacmanModel, updates the view, updates score and level, displays Game Over/You Won, and instructions of how to play
     * @param direction the most recently inputted direction for PacMan to move in
     */
    private void update(Direction direction) {
        if (isPaused){
            return;
        }
        grid = pacmanModel.getGrid();
        if (PacmanModel.isPacmanHit()){
            PacmanModel.setIsPacmanHit(false);
            PacmanModel.ghostsCanMove = false;
            PacmanModel.delay = 2000;
        }
        pacmanModel.step(direction);
        pacmanView.update(pacmanModel);
        scoreLabel.setText(String.format("Score : %d", this.pacmanModel.getScore()));
        soundLabel.setText(returnIsMute());
        lives.setText(String.valueOf(pacmanModel.lives));
        if (PacmanModel.isGameOver()) {
            if (GameController.isNotMuted) {
                themeSong.stop();
                deathSong.play();
            }
            pause();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("GAME OVER");
            alert.setHeaderText("Game over!");
            alert.setContentText("You can start a new game\nor return to main menu.");
            alert.show();
            if (MainMenuController.currentUser != null) {
                MainMenuController.currentUser.hasSavedMap = false;
                MainMenuController.currentUser.changeRecord(this.pacmanModel.getScore());
            }
        }
        if (pacmanModel.areAllDotsEaten()){
            isPaused = true;
        }
        //when PacMan is in ghostEatingMode, count down the ghostEatingModeCounter to reset ghostEatingMode to false when the counter is 0
        if (PacmanModel.isGhostEatingMode()) {
            ghostEatingModeCounter--;
        }
        if (ghostEatingModeCounter == 0 && PacmanModel.isGhostEatingMode()) {
            PacmanModel.setGhostEatingMode(false);
        }
    }

    /**
     * Takes in user keyboard input to control the movement of PacMan and start new games
     * @param keyEvent user's key click
     */
    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        KeyCode code = keyEvent.getCode();
        Direction direction = Direction.NONE;
        if (code == KeyCode.LEFT) {
            isPaused = false;
            direction = Direction.LEFT;
        } else if (code == KeyCode.RIGHT) {
            isPaused = false;
            direction = Direction.RIGHT;
        } else if (code == KeyCode.UP) {
            isPaused = false;
            direction = Direction.UP;
        } else if (code == KeyCode.DOWN) {
            isPaused = false;
            direction = Direction.DOWN;
        } else if (code == KeyCode.P){
            pauseLabel.setText("Game Paused");
            pause();
        } else if (code == KeyCode.R){
            if (paused) {
                pauseLabel.setText("");
                paused = false;
                startTimer();
            }
        } else {
            keyRecognized = false;
        }
        if (keyRecognized) {
            keyEvent.consume();
            pacmanModel.setCurrentDirection(direction);
        }
    }

    /**
     * Pause the timer
     */
    public void pause() {
        this.timer.cancel();
        this.paused = true;
    }

    public double getBoardWidth() {
        return PacmanView.CELL_WIDTH * this.pacmanView.getColumnCount();
    }

    public double getBoardHeight() {
        return PacmanView.CELL_WIDTH * this.pacmanView.getRowCount();
    }

    public static void setGhostEatingModeCounter() {
        ghostEatingModeCounter = 50;
    }

    public static int getGhostEatingModeCounter() {
        return ghostEatingModeCounter;
    }


    public boolean getPaused() {
        return paused;
    }

    public void saveGameAndExit(ActionEvent actionEvent) {
        if (MainMenuController.currentUser != null) {
            if (!(PacmanModel.isGameOver())) {
                MainMenuController.currentUser.hasSavedMap = true;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Exit and save game confirmation");
                alert.setContentText("Do you want to save the game?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Point2D[] ghostsPos = {
                            pacmanModel.getGhost1Location(),
                            pacmanModel.getGhost2Location(),
                            pacmanModel.getGhost3Location(),
                            pacmanModel.getGhost4Location()
                    };
                    Point2D pacmanPos = pacmanModel.getPacmanLocation();
                    PacmanMap.writeMapToFile(grid, pacmanPos, ghostsPos, pacmanModel.getScore(), pacmanModel.lives);
                }
                try {
                    new MainMenuView().start(WelcomeView.stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    new MainMenuView().start(WelcomeView.stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                new WelcomeView().start(WelcomeView.stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        themeSong.stop();
    }
    public String returnIsMute(){
        if (isNotMuted)
            return "Sound is unmute";
        return "Sound is mute";
    }
}
