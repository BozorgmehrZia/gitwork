package pacman.model;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PacmanView extends Group {
    public final static double CELL_WIDTH = 25.0;

    @FXML
    private int rowCount;
    @FXML private int columnCount;
    private ImageView[][] cellViews;
    private final Image pacmanRightImage;
    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image ghost1Image;
    private final Image ghost2Image;
    private final Image ghost3Image;
    private final Image ghost4Image;
    private final Image blueGhostImage;
    private final Image wallImage;
    private final Image bigDotImage;
    private final Image smallDotImage;

    /**
     * Initializes the values of the image instance variables from files
     */
    public PacmanView() {
        this.pacmanRightImage = new Image(getClass().getResourceAsStream("/pacman/images/pacmanRight.gif"));
        this.pacmanUpImage = new Image(getClass().getResourceAsStream("/pacman/images/pacmanUp.gif"));
        this.pacmanDownImage = new Image(getClass().getResourceAsStream("/pacman/images/pacmanDown.gif"));
        this.pacmanLeftImage = new Image(getClass().getResourceAsStream("/pacman/images/pacmanLeft.gif"));
        this.ghost1Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost1.gif"));
        this.ghost2Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost2.gif"));
        this.ghost3Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost3.gif"));
        this.ghost4Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost4.gif"));
        this.blueGhostImage = new Image(getClass().getResourceAsStream("/pacman/images/blueGhost.gif"));
        this.wallImage = new Image(getClass().getResourceAsStream("/pacman/images/wall.png"));
        this.bigDotImage = new Image(getClass().getResourceAsStream("/pacman/images/whiteDot.png"));
        this.smallDotImage = new Image(getClass().getResourceAsStream("/pacman/images/smallDot.png"));
    }

    /**
     * Constructs an empty grid of ImageViews
     */
    private void initializeGrid() {
        if (this.rowCount > 0 && this.columnCount > 0) {
            this.cellViews = new ImageView[this.rowCount][this.columnCount];
            for (int row = 0; row < this.rowCount; row++) {
                for (int column = 0; column < this.columnCount; column++) {
                    ImageView imageView = new ImageView();
                    imageView.setX((double)column * CELL_WIDTH);
                    imageView.setY((double)row * CELL_WIDTH);
                    imageView.setFitWidth(CELL_WIDTH);
                    imageView.setFitHeight(CELL_WIDTH);
                    this.cellViews[row][column] = imageView;
                    this.getChildren().add(imageView);
                }
            }
        }
    }

    /** Updates the view to reflect the state of the model
     *
     */
    public void update(PacmanModel model) {
        assert model.getRowCount() == this.rowCount && model.getColumnCount() == this.columnCount;
        //for each ImageView, set the image to correspond with the pacman.model.CellValue of that cell
        for (int row = 0; row < this.rowCount; row++){
            for (int column = 0; column < this.columnCount; column++){
                CellValue value = model.getCellValue(row, column);
                if (value == CellValue.WALL) {
                    this.cellViews[row][column].setImage(this.wallImage);
                }
                else if (value == CellValue.BIG_DOT) {
                    this.cellViews[row][column].setImage(this.bigDotImage);
                }
                else if (value == CellValue.SMALL_DOT) {
                    this.cellViews[row][column].setImage(this.smallDotImage);
                }
                else {
                    this.cellViews[row][column].setImage(null);
                }
                //check which direction PacMan is going in and display the corresponding image
                if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && (PacmanModel.getLastDirection() == Direction.RIGHT || PacmanModel.getLastDirection() == Direction.NONE)) {
                    this.cellViews[row][column].setImage(this.pacmanRightImage);
                }
                else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == Direction.LEFT) {
                    this.cellViews[row][column].setImage(this.pacmanLeftImage);
                }
                else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == Direction.UP) {
                    this.cellViews[row][column].setImage(this.pacmanUpImage);
                }
                else if (row == model.getPacmanLocation().getX() && column == model.getPacmanLocation().getY() && PacmanModel.getLastDirection() == Direction.DOWN) {
                    this.cellViews[row][column].setImage(this.pacmanDownImage);
                }
                //display blue ghosts in ghostEatingMode, otherwise display regular ghost images.
                setGhostsImages(model, row, column, PacmanModel.isGhostEatingMode());
            }
        }
    }

    private void setGhostsImages(PacmanModel model, int row, int column, boolean isInGhostMode) {
        if (!isInGhostMode) {
            if (row == model.getGhost1Location().getX() && column == model.getGhost1Location().getY()) {
                this.cellViews[row][column].setImage(this.ghost1Image);
            }
            if (row == model.getGhost2Location().getX() && column == model.getGhost2Location().getY()) {
                this.cellViews[row][column].setImage(this.ghost2Image);
            }
            if (row == model.getGhost3Location().getX() && column == model.getGhost3Location().getY()) {
                this.cellViews[row][column].setImage(this.ghost3Image);
            }
            if (row == model.getGhost4Location().getX() && column == model.getGhost4Location().getY()) {
                this.cellViews[row][column].setImage(this.ghost4Image);
            }
        } else {
            if (row == model.getGhost1Location().getX() && column == model.getGhost1Location().getY()) {
                this.cellViews[row][column].setImage(this.blueGhostImage);
            }
            if (row == model.getGhost2Location().getX() && column == model.getGhost2Location().getY()) {
                this.cellViews[row][column].setImage(this.blueGhostImage);
            }
            if (row == model.getGhost3Location().getX() && column == model.getGhost3Location().getY()) {
                this.cellViews[row][column].setImage(this.blueGhostImage);
            }
            if (row == model.getGhost4Location().getX() && column == model.getGhost4Location().getY()) {
                this.cellViews[row][column].setImage(this.blueGhostImage);
            }
        }
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.initializeGrid();
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        this.initializeGrid();
    }
}
