package pacman.model;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import pacman.controller.GameController;
import pacman.model.pacmanMap.PacmanMap;

import java.io.File;
import java.util.Random;


public class PacmanModel {
    @FXML
    private int rowCount;
    @FXML private int columnCount;
    private CellValue[][] grid;
    private int score = GameController.initialScore;
    private int dotCount;
    public int lives;
    public int numberOfEatenGhost;
    public static boolean isFirstGhostMove = true;
    public static boolean ghostsCanMove = false;
    public static int delay;
    private static boolean isPacmanHit = false;
    private static boolean gameOver;
    private static boolean ghostEatingMode;
    private Point2D pacmanLocation;
    private Point2D pacmanVelocity;
    private Point2D ghost1Location;
    private Point2D ghost1Velocity;
    private Point2D ghost2Location;
    private Point2D ghost2Velocity;
    private Point2D ghost3Location;
    private Point2D ghost3Velocity;
    private Point2D ghost4Location;
    private Point2D ghost4Velocity;
    private Point2D ghost1TempoLocation;
    private Point2D ghost2TempoLocation;
    private Point2D ghost3TempoLocation;
    private Point2D ghost4TempoLocation;
    private static Direction lastDirection;
    private static Direction currentDirection;

    /**
     * Start a new game upon initialization
     */
    public PacmanModel() {
        this.startNewGame();
    }


    public void initializeGame(String fileName) {
        PacmanMap.readMapFromFile(fileName);
        grid = PacmanMap.grid;
        this.dotCount = PacmanMap.dotCount;
        rowCount = PacmanMap.rowCount;
        columnCount = PacmanMap.columnCount;
        pacmanLocation = new Point2D(PacmanMap.pacmanPos[0], PacmanMap.pacmanPos[1]);
        pacmanVelocity = new Point2D(0,0);
        ghost1Location = new Point2D(PacmanMap.ghost1Pos[0],PacmanMap.ghost1Pos[1]);
        ghost1TempoLocation = ghost1Location;
        ghost1Velocity = new Point2D(-1, 0);
        ghost2Location = new Point2D(PacmanMap.ghost2Pos[0],PacmanMap.ghost2Pos[1]);
        ghost2TempoLocation = ghost2Location;
        ghost2Velocity = new Point2D(-1, 0);
        ghost3Location = new Point2D(PacmanMap.ghost3Pos[0],PacmanMap.ghost3Pos[1]);
        ghost3TempoLocation = ghost3Location;
        ghost3Velocity = new Point2D(-1, 0);
        ghost4Location = new Point2D(PacmanMap.ghost4Pos[0],PacmanMap.ghost4Pos[1]);
        ghost4TempoLocation = ghost4Location;
        ghost4Velocity = new Point2D(-1, 0);
        currentDirection = Direction.NONE;
        lastDirection = Direction.NONE;
    }

    /** Initialize values of instance variables and initialize level map
     */
    public void startNewGame() {
        gameOver = false;
        ghostEatingMode = false;
        ghostsCanMove = false;
        delay = 2000;
        this.dotCount = 0;
        score = 0;
        lives = GameController.initialLives;
        initializeGame(GameController.defaultMap);
    }

    /** Initialize the level map for the next level
     *
     */
    public void startNextGame() {
        lives++;
        ghostsCanMove = false;
        delay = 2000;
        ghostEatingMode = false;
        initializeGame(GameController.defaultMap);
    }
    /**
     * Updates the model to reflect the movement of PacMan and the ghosts and the change in state of any objects eaten
     * during the course of these movements. Switches game state to or from ghost-eating mode.
     */
    public void step(Direction direction) {
        if (gameOver)
            return;
        movePacman(direction);
        //if PacMan is on a small dot, delete small dot
        CellValue pacmanLocationCellValue = grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()];
        if (pacmanLocationCellValue == CellValue.SMALL_DOT) {
            grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()] = CellValue.EMPTY;
            this.dotCount--;
            score += 5;
        }
        //if PacMan is on a big dot, delete big dot and change game state to ghost-eating mode and initialize the counter
        if (pacmanLocationCellValue == CellValue.BIG_DOT) {
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File("src/main/resources/pacman/Sounds/Fruit.mp3").toURI().toString()));
            if (GameController.isNotMuted)
                mediaPlayer.play();
            grid[(int) pacmanLocation.getX()][(int) pacmanLocation.getY()] = CellValue.EMPTY;
            this.dotCount--;
            numberOfEatenGhost = 0;
            ghostEatingMode = true;
            GameController.setGhostEatingModeCounter();
        }
        if (!ghostsCanMove) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            ghostsCanMove = true;
                        }
                    },
                    delay
            );
        }
        if (ghostsCanMove) {
            //send ghost back to ghostHome if PacMan is on a ghost in ghost-eating mode
            eatGhostInGhostMode();
            if (gameOver)
                return;
            //move ghosts and checks again if ghosts or PacMan are eaten (repeating these checks helps account for even/odd numbers of squares between ghosts and PacMan)
            moveGhosts();
            eatGhostInGhostMode();
        }
        if (areAllDotsEaten()) {
            System.out.println("");
            GameController.isPaused = true;
            pacmanVelocity = new Point2D(0,0);
            startNextGame();
        }
    }

    /**
     * Move PacMan based on the direction indicated by the user (based on keyboard input from the pacman.Controller)
     * @param direction the most recently inputted direction for PacMan to move in
     */
    public void movePacman(Direction direction) {
        Point2D potentialPacmanVelocity = changeVelocity(direction);
        Point2D potentialPacmanLocation = pacmanLocation.add(potentialPacmanVelocity);
        //if PacMan goes offscreen, wrap around
        potentialPacmanLocation = setGoingOffscreenNewLocation(potentialPacmanLocation);
        //determine whether PacMan should change direction or continue in its most recent direction
        //if most recent direction input is the same as previous direction input, check for walls
        if (direction.equals(lastDirection)) {
            //if moving in the same direction would result in hitting a wall, stop moving
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                pacmanVelocity = changeVelocity(Direction.NONE);
            }
            else {
                pacmanVelocity = potentialPacmanVelocity;
                pacmanLocation = potentialPacmanLocation;
            }
        }
        //if most recent direction input is not the same as previous input, check for walls and corners before going in a new direction
        else {
            //if PacMan would hit a wall with the new direction input, check to make sure he would not hit a different wall if continuing in his previous direction
            if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL){
                potentialPacmanVelocity = changeVelocity(lastDirection);
                potentialPacmanLocation = pacmanLocation.add(potentialPacmanVelocity);
                //if changing direction would hit another wall, stop moving
                if ((int) potentialPacmanLocation.getX() >= 0 && (int) potentialPacmanLocation.getY() >= 0) {
                    if (grid[(int) potentialPacmanLocation.getX()][(int) potentialPacmanLocation.getY()] == CellValue.WALL) {
                        pacmanVelocity = changeVelocity(Direction.NONE);
                    } else {
                        pacmanVelocity = changeVelocity(lastDirection);
                        pacmanLocation = pacmanLocation.add(pacmanVelocity);
                    }
                }
            }
            //otherwise, change direction and keep moving
            else {
                pacmanVelocity = potentialPacmanVelocity;
                pacmanLocation = potentialPacmanLocation;
                setLastDirection(direction);
            }
        }
    }

    /**
     * Move ghosts to follow PacMan as established in moveAGhost() method
     */
    public void moveGhosts() {
        Point2D[] ghost1Data = moveAGhost(ghost1Velocity, ghost1TempoLocation);
        Point2D[] ghost2Data = moveAGhost(ghost2Velocity, ghost2TempoLocation);
        Point2D[] ghost3Data = moveAGhost(ghost3Velocity, ghost3TempoLocation);
        Point2D[] ghost4Data = moveAGhost(ghost4Velocity, ghost4TempoLocation);
        ghost1Velocity = ghost1Data[0];
        ghost1TempoLocation = ghost1Data[1];
        ghost1Location = returnIntOfPoint(ghost1TempoLocation);
        //
        ghost2Velocity = ghost2Data[0];
        ghost2TempoLocation = ghost2Data[1];
        ghost2Location = returnIntOfPoint(ghost2TempoLocation);
        //
        ghost3Velocity = ghost3Data[0];
        ghost3TempoLocation = ghost3Data[1];
        ghost3Location = returnIntOfPoint(ghost3TempoLocation);
        //
        ghost4Velocity = ghost4Data[0];
        ghost4TempoLocation = ghost4Data[1];
        ghost4Location = returnIntOfPoint(ghost4TempoLocation);
    }

    /**
     * Move a ghost to follow PacMan if he is in the same row or column, or move away from PacMan if in ghostEatingMode, otherwise move randomly when it hits a wall.
     * @param velocity the current velocity of the specified ghost
     * @param location the current location of the specified ghost
     */
    public Point2D[] moveAGhost(Point2D velocity, Point2D location){
        Direction[] directions;
        if (!ghostEatingMode) {
            directions = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        } else {
            directions = new Direction[]{Direction.DOWN, Direction.UP, Direction.RIGHT, Direction.LEFT};
        }
        if (location.getY() == pacmanLocation.getY()) {
            if (location.getX() > pacmanLocation.getX())
                velocity = changeVelocityGhost(directions[0]);
            else
                velocity = changeVelocityGhost(directions[1]);
        }
        //check if ghost is in PacMan's row and move towards him
        else if (location.getX() == pacmanLocation.getX()) {
            if (location.getY() > pacmanLocation.getY())
                velocity = changeVelocityGhost(directions[2]);
            else
                velocity = changeVelocityGhost(directions[3]);
        }
        //move in a consistent random direction until it hits a wall, then choose a new random direction
        Random generator = new Random();
        Point2D potentialLocation = location.add(velocity);
        potentialLocation = setGoingOffscreenNewLocation(potentialLocation);
        while (grid[(int) potentialLocation.getX()][(int) potentialLocation.getY()] == CellValue.WALL) {
            int randomNum = generator.nextInt(4);
            Direction direction = intToDirection(randomNum);
            velocity = changeVelocityGhost(direction);
            potentialLocation = location.add(velocity);
        }
        location = potentialLocation;
        return new Point2D[]{velocity, location};
    }


    /**
     * Wrap around the game board if the object's location would be off screen
     * @param objectLocation the specified object's location
     * @return Point2D new wrapped-around location
     */
    public Point2D setGoingOffscreenNewLocation(Point2D objectLocation) {
        //if object goes offscreen on the right
        if (objectLocation.getY() >= columnCount) {
            objectLocation = new Point2D(objectLocation.getX(), 0);
        }
        //if object goes offscreen on the left
        if (objectLocation.getY() < 0) {
            objectLocation = new Point2D(objectLocation.getX(), columnCount - 1);
        }
        return objectLocation;
    }

    /**
     * Connects each pacman.model.Direction to an integer 0-3
     * @param x an integer
     * @return the corresponding pacman.model.Direction
     */
    public Direction intToDirection(int x){
        if (x == 0){
            return Direction.LEFT;
        }
        else if (x == 1){
            return Direction.RIGHT;
        }
        else if(x == 2){
            return Direction.UP;
        }
        else{
            return Direction.DOWN;
        }
    }

    /**
     * Resets ghosts' location and velocity to its home state
     */
    public void sendGhost1Home() {
        System.out.println("");
        ghost1Location = new Point2D(1, 1);
        ghost1TempoLocation = ghost1Location;
        ghost1Velocity = new Point2D(-0.5, 0);
    }
    public void sendGhost2Home() {
        System.out.println("");
        ghost2Location = new Point2D(1, 17);
        ghost2TempoLocation = ghost2Location;
        ghost2Velocity = new Point2D(-0.5, 0);
    }
    public void sendGhost3Home() {
        System.out.println("");
        ghost3Location = new Point2D(19, 1);
        ghost3TempoLocation = ghost3Location;
        ghost3Velocity = new Point2D(-0.5, 0);
    }
    public void sendGhost4Home() {
        System.out.println("");
        ghost4Location = new Point2D(19, 17);
        ghost4TempoLocation = ghost4Location;
        ghost4Velocity = new Point2D(-0.5, 0);
    }
    public void increaseScore(){
        ghostsCanMove = false;
        numberOfEatenGhost++;
        score += 200 * numberOfEatenGhost;
    }
    public void eatGhost(){
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File("src/main/resources/pacman/Sounds/Chomp.mp3").toURI().toString()));
        if (GameController.isNotMuted)
            mediaPlayer.play();
        increaseScore();
    }

    private void eatGhostInGhostMode() {
        if (ghostEatingMode) {
            delay = 5000;
            if (pacmanLocation.equals(ghost1Location)) {
                eatGhost();
                sendGhost1Home();
            } else if (pacmanLocation.equals(ghost2Location)) {
                eatGhost();
                sendGhost2Home();
            } else if (pacmanLocation.equals(ghost3Location)) {
                eatGhost();
                sendGhost3Home();
            } else if (pacmanLocation.equals(ghost4Location)) {
                eatGhost();
                sendGhost4Home();
            }
        }
        //game over if PacMan is eaten by a ghost
        else {
            if ( (!isPacmanHit) && (pacmanLocation.equals(ghost1Location) || pacmanLocation.equals(ghost2Location)
                    || pacmanLocation.equals(ghost3Location) || pacmanLocation.equals(ghost4Location))) {
                if (lives == 1) {
                    GameController.isPaused = true;
                    gameOver = true;
                    lives = 0;
                    pacmanVelocity = new Point2D(0, 0);
                } else {
                    MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File("src/main/resources/pacman/Sounds/Ghost.mp3").toURI().toString()));
                    if (GameController.isNotMuted)
                        mediaPlayer.play();
                    GameController.isPaused = true;
                    lives--;
                    ghostsCanMove = false;
                    delay = 2000;
                    isPacmanHit = true;
                }
            }
        }
    }

    /**
     * Connects each direction to Point2D velocity vectors (Left = (-1,0), Right = (1,0), Up = (0,-1), Down = (0,1))
     */
    public Point2D changeVelocity(Direction direction){
        if(direction == Direction.LEFT){
            return new Point2D(0,-1);
        }
        else if(direction == Direction.RIGHT){
            return new Point2D(0,1);
        }
        else if(direction == Direction.UP){
            return new Point2D(-1,0);
        }
        else if(direction == Direction.DOWN){
            return new Point2D(1,0);
        }
        else{
            return new Point2D(0,0);
        }
    }
    public Point2D changeVelocityGhost(Direction direction){
        if(direction == Direction.LEFT){
            return new Point2D(0,-0.5);
        }
        else if(direction == Direction.RIGHT){
            return new Point2D(0,0.5);
        }
        else if(direction == Direction.UP){
            return new Point2D(-0.5,0);
        }
        else if(direction == Direction.DOWN){
            return new Point2D(0.5,0);
        }
        else{
            return new Point2D(0,0);
        }
    }
    public Point2D returnIntOfPoint(Point2D point2D){
        int x = (int) point2D.getX();
        int y = (int) point2D.getY();
        return new Point2D(x, y);
    }

    public static boolean isGhostEatingMode() {
        return ghostEatingMode;
    }

    public static void setGhostEatingMode(boolean ghostEatingModeBool) {
        ghostEatingMode = ghostEatingModeBool;
    }


    /**
     * When all dots are eaten, level is complete
     * @return boolean
     */
    public boolean areAllDotsEaten() {
        return this.dotCount == 0;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setIsPacmanHit(boolean isPacmanHit) {
        PacmanModel.isPacmanHit = isPacmanHit;
    }

    public static boolean isPacmanHit() {
        return isPacmanHit;
    }

    public CellValue[][] getGrid() {
        return grid;
    }

    /**
     * @return the Cell Value of cell (row, column)
     */
    public CellValue getCellValue(int row, int column) {
        assert row >= 0 && row < this.grid.length && column >= 0 && column < this.grid[0].length;
        return this.grid[row][column];
    }

    public static Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction direction) {
        currentDirection = direction;
    }

    public static Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(Direction direction) {
        lastDirection = direction;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addToScore(int points) {
        this.score += points;
    }


    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public Point2D getPacmanLocation() {
        return pacmanLocation;
    }

    public void setPacmanLocation(Point2D pacmanLocation) {
        this.pacmanLocation = pacmanLocation;
    }
    //1
    public Point2D getGhost1Location() {
        return ghost1Location;
    }

    public void setGhost1Location(Point2D ghost1Location) {
        this.ghost1Location = ghost1Location;
    }
    //2
    public Point2D getGhost2Location() {
        return ghost2Location;
    }

    public void setGhost2Location(Point2D ghost2Location) {
        this.ghost2Location = ghost2Location;
    }
    //3
    public Point2D getGhost3Location() {
        return ghost3Location;
    }

    public void setGhost3Location(Point2D ghost3Location) {
        this.ghost3Location = ghost3Location;
    }
    //4
    public Point2D getGhost4Location() {
        return ghost4Location;
    }

    public void setGhost4Location(Point2D ghost4Location) {
        this.ghost4Location = ghost4Location;
    }

    public Point2D getPacmanVelocity() {
        return pacmanVelocity;
    }

    public void setPacmanVelocity(Point2D velocity) {
        this.pacmanVelocity = velocity;
    }
    //1
    public Point2D getGhost1Velocity() {
        return ghost1Velocity;
    }

    public void setGhost1Velocity(Point2D ghost1Velocity) {
        this.ghost1Velocity = ghost1Velocity;
    }
    //2
    public Point2D getGhost2Velocity() {
        return ghost2Velocity;
    }

    public void setGhost2Velocity(Point2D ghost2Velocity) {
        this.ghost2Velocity = ghost2Velocity;
    }
    //3
    public Point2D getGhost3Velocity() {
        return ghost3Velocity;
    }

    public void setGhost3Velocity(Point2D ghost3Velocity) {
        this.ghost3Velocity = ghost3Velocity;
    }
    //4
    public Point2D getGhost4Velocity() {
        return ghost4Velocity;
    }

    public void setGhost4Velocity(Point2D ghost4Velocity) {
        this.ghost4Velocity = ghost4Velocity;
    }
}
