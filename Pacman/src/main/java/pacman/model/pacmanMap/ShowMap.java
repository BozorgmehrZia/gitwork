package pacman.model.pacmanMap;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pacman.model.CellValue;


public class ShowMap extends Group {
    public final static double CELL_WIDTH = 25.0;
    @FXML private int rowCount = 21;
    @FXML private int columnCount = 19;
    @FXML private String mapFileName;
    private ImageView[][] cellViews;
    private final Image pacmanRightImage;
    private final Image ghost1Image;
    private final Image ghost2Image;
    private final Image ghost3Image;
    private final Image ghost4Image;
    private final Image wallImage;
    private final Image bigDotImage;
    private final Image smallDotImage;

    /**
     * Initializes the values of the image instance variables from files
     */
    public ShowMap() {
        this.pacmanRightImage = new Image(getClass().getResourceAsStream("/pacman/images/pacmanRight.gif"));
        this.ghost1Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost1.gif"));
        this.ghost2Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost2.gif"));
        this.ghost3Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost3.gif"));
        this.ghost4Image = new Image(getClass().getResourceAsStream("/pacman/images/ghost4.gif"));
        this.wallImage = new Image(getClass().getResourceAsStream("/pacman/images/wall.png"));
        this.bigDotImage = new Image(getClass().getResourceAsStream("/pacman/images/whiteDot.png"));
        this.smallDotImage = new Image(getClass().getResourceAsStream("/pacman/images/smallDot.png"));
    }

    /** Updates the view to reflect the state of the model
     *
     */
    public void setImages() {
        PacmanMap.isSavedMap = false;
        PacmanMap.readMapFromFile(mapFileName);
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
        for (int row = 0; row < this.rowCount; row++){
            for (int column = 0; column < this.columnCount; column++){
                CellValue value = getCellValue(row, column);
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
            }
        }
        this.cellViews[9][9].setImage(this.pacmanRightImage);
        this.cellViews[1][1].setImage(this.ghost1Image);
        this.cellViews[1][17].setImage(this.ghost2Image);
        this.cellViews[19][1].setImage(this.ghost3Image);
        this.cellViews[19][17].setImage(this.ghost4Image);
    }
    /**
     * @return the Cell Value of cell (row, column)
     */
    public CellValue getCellValue(int row, int column) {
        assert row >= 0 && row < PacmanMap.grid.length && column >= 0 && column < PacmanMap.grid[0].length;
        return PacmanMap.grid[row][column];
    }

    public void setMapFileName(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    public String getMapFileName() {
        return mapFileName;
    }
}
