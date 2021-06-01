package pacman.model.pacmanMap;

import javafx.geometry.Point2D;
import pacman.model.CellValue;
import pacman.controller.GameController;
import pacman.controller.MainMenuController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PacmanMap {
    public static boolean isSavedMap;
    public static int rowCount = 21;
    public static int columnCount = 19;
    public static int dotCount;
    public static CellValue[][] grid = new CellValue[rowCount][columnCount];
    public static int[] pacmanPos = new int[2];
    public static int[] ghost1Pos = new int[2];
    public static int[] ghost2Pos = new int[2];
    public static int[] ghost3Pos = new int[2];
    public static int[] ghost4Pos = new int[2];
    /**
     * Configure the grid CellValues based on the txt file and place PacMan and ghosts at their starting locations.
     * "W" indicates a wall, "E" indicates an empty square, "B" indicates a big dot, "S" indicates
     * a small dot, "1" or "2" indicates the ghosts home, and "P" indicates Pacman's starting position.
     */
    public static void readMapFromFile(String fileName) {
        dotCount = 0;
        File file = new File(fileName);
        try {
            Scanner scanner = new Scanner(file);
            int row = 0;
            while (row < rowCount) {
                int column = 0;
                String line = scanner.nextLine();
                Scanner lineScanner = new Scanner(line);
                while (column < columnCount) {
                    String value = lineScanner.next();
                    CellValue thisValue;
                    if (value.equals("W")) {
                        thisValue = CellValue.WALL;
                    } else if (value.equals("S")) {
                        thisValue = CellValue.SMALL_DOT;
                        dotCount++;
                    } else if (value.equals("B")) {
                        thisValue = CellValue.BIG_DOT;
                        dotCount++;
                    } else {
                        thisValue = CellValue.EMPTY;
                    }
                    grid[row][column] = thisValue;
                    column++;
                }
                row++;
            }
            initializeLocations(scanner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void initializeLocations(Scanner scanner){
        if (isSavedMap){
            int[][] pos = new int[6][];
            int i = 0;
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] posStrings = line.split(",");
                pos[i] = new int[]{
                        (int) Double.parseDouble(posStrings[0]),
                        (int) Double.parseDouble(posStrings[1])
                };
                i++;
            }
            pacmanPos = pos[0];
            ghost1Pos = pos[1];
            ghost2Pos = pos[2];
            ghost3Pos = pos[3];
            ghost4Pos = pos[4];
            GameController.initialLives = pos[5][1];
            GameController.initialScore = pos[5][0];
        } else {
            pacmanPos = new int[]{9, 9};
            ghost1Pos = new int[]{1, 1};
            ghost2Pos = new int[]{1, 17};
            ghost3Pos = new int[]{19, 1};
            ghost4Pos = new int[]{19, 17};
        }
    }

    public static String cellValueToString(CellValue[][] grid, int i, int j){
        if (grid[i][j] == CellValue.WALL)
            return "W";
        else if (grid[i][j] == CellValue.EMPTY)
            return "E";
        else if (grid[i][j] == CellValue.BIG_DOT)
            return "B";
        else if (grid[i][j] == CellValue.SMALL_DOT)
            return "S";
        return "";
    }
    public static String point2DPosToString(Point2D pos){
        return pos.getX() + "," + pos.getY() + "\n";
    }
    public static void writeMapToFile(CellValue[][] grid, Point2D pacmanPos, Point2D[] ghostsPos, int score, int initialLives){
        String fileName = "src/main/resources/pacman/users/" +
                MainMenuController.currentUser.getUsername() + "SavedMap.txt";
        StringBuilder map = new StringBuilder();
        for (int i = 0; i < rowCount; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < columnCount - 1; j++) {
                row.append(cellValueToString(grid, i, j));
                row.append(" ");
            }
            row.append(cellValueToString(grid, i, columnCount - 1));
            row.append("\n");
            map.append(row);
        }
        map.append(point2DPosToString(pacmanPos));
        for (Point2D ghostPos : ghostsPos) {
            map.append(point2DPosToString(ghostPos));
        }
        String string = score + "," + initialLives + "\n";
        map.append(string);
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(map.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static CellValue[][] returnGrid(){
        return grid;
    }

}
