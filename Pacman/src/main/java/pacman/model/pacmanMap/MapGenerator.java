package pacman.model.pacmanMap;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MapGenerator {
    //Code from HW1 question 3
    public static String returnNewMapFile(){
        int x = LoadMapsFromFile.returnNumberOfFiles() + 1;
        return "src/main/resources/pacman/maps/map" + x + ".txt";
    }
    public static void writeMazeArrayToFile(char[][] maze) {
        try {
            FileWriter fileWriter = new FileWriter(returnNewMapFile());
            for (char[] chars : maze) {
                String row = Arrays.toString(chars).replace(",", "")
                        .replace("[", "")
                        .replace("]", "")
                        + "\n";
                fileWriter.write(row);
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void removeWallAndBigDot(char[][] maze){
        int wallNumbers = 0;
        for (int i = 1; i < 20; i++) {
            for (int j = 1; j < 18; j++) {
                if (maze[i][j] == 'W')
                    wallNumbers++;
            }
        }
        int t = (int) (wallNumbers * 0.2);
        int counter = 0;
        Random random = new Random();
        while (counter < t){
            int i = random.nextInt(19) + 1;
            int j = random.nextInt(17) + 1;
            if (maze[i][j] == 'W'){
                maze[i][j] = 'S';
                counter++;
            }
        }
        counter = 0;
        while (counter < 4){
            int i = random.nextInt(20) + 1;
            int j = random.nextInt(18) + 1;
            if (maze[i][j] == 'S'){
                maze[i][j] = 'B';
                counter++;
            }
        }
    }

    public static char[][] mazeInitialize(int height, int width, int[][] horizontalWalls, int[][] verticalWalls) {
        char[][] maze = new char[height][width];
        Arrays.fill(maze[0], 'W');
        Arrays.fill(maze[height - 1], 'W');
        for (int i = 1; i < height - 1; i++) {
            maze[i][0] = 'W';
            maze[i][width - 1] = 'W';
        }
        for (int i = 1; i < height - 1; i++)
            for (int j = 1; j < width - 1; j++) {
                if (i % 2 == 0 && j % 2 == 0)
                    maze[i][j] = 'W';
                else
                    maze[i][j] = 'S';
            }
        for (int i = 2; i < height - 2; i += 2)
            for (int j = 1; j < width - 1; j += 2) {
                if (verticalWalls[i / 2 - 1][j / 2] == 0)
                    maze[i][j] = 'W';
                else
                    maze[i][j] = 'S';
            }
        for (int i = 1; i < height - 1; i += 2)
            for (int j = 2; j < width - 2; j += 2) {
                if (horizontalWalls[i / 2][j / 2 - 1] == 0)
                    maze[i][j] = 'W';
                else
                    maze[i][j] = 'S';
            }
        maze[1][1] = '1';
        maze[1][17] = '2';
        maze[19][1] = '3';
        maze[19][17] = '4';
        maze[9][9] = 'P';
        removeWallAndBigDot(maze);
        return maze;
    }

    public static int generateRandomDirections(int number) {
        ArrayList<Integer> randomNumbers = new ArrayList<>();
        for (int i = 0; i < number; i++)
            randomNumbers.add(i);
        Collections.shuffle(randomNumbers);
        return randomNumbers.get(0);
    }

    public static int returnNumberOfAvailablePaths(int[] directions, int x, int y, int height, int width, int[][] visited) {
        int numberOfPaths = 0;

        if (y > 0 && visited[y - 1][x] == 0) {
            directions[numberOfPaths] = 0; //North
            numberOfPaths++;
        }
        if (x < width - 1 && visited[y][x + 1] == 0) {
            directions[numberOfPaths] = 1; //East
            numberOfPaths++;
        }
        if (y < height - 1 && visited[y + 1][x] == 0) {
            directions[numberOfPaths] = 2; //South
            numberOfPaths++;
        }
        if (x > 0 && visited[y][x - 1] == 0) {
            directions[numberOfPaths] = 3; //West
            numberOfPaths++;
        }

        return numberOfPaths;
    }

    public static void setWalls(int x, int y, int height, int width, int[][] verticalWalls, int[][] horizontalWalls, int[][] visited) {
        int[] directions = new int[4];
        int numberOfAvailablePaths;

        visited[y][x] = 1;

        numberOfAvailablePaths = returnNumberOfAvailablePaths(directions, x, y, height, width, visited);

        while (numberOfAvailablePaths > 0) {
            int randomNumber = generateRandomDirections(numberOfAvailablePaths);
            switch (directions[randomNumber]) {
                case 0:
                    verticalWalls[y - 1][x] = 1;
                    setWalls(x, y - 1, height, width, verticalWalls, horizontalWalls, visited);
                    break;
                case 1:
                    horizontalWalls[y][x] = 1;
                    setWalls(x + 1, y, height, width, verticalWalls, horizontalWalls, visited);
                    break;
                case 2:
                    verticalWalls[y][x] = 1;
                    setWalls(x, y + 1, height, width, verticalWalls, horizontalWalls, visited);
                    break;
                case 3:
                    horizontalWalls[y][x - 1] = 1;
                    setWalls(x - 1, y, height, width, verticalWalls, horizontalWalls, visited);
                    break;
            }

            numberOfAvailablePaths = returnNumberOfAvailablePaths(directions, x, y, height, width, visited);
        }
    }

    public static void run() {
        int m = 10;
        int n = 9;
        int t = 1;
        int height = 2 * m + 1;
        int width = 2 * n + 1;
        for (int c = 0; c < t; c++) {
            int[][] visited = new int[m][n];
            int[][] horizontalWalls = new int[m][n - 1];        // horizontal E-W paths in the maze
            int[][] verticalWalls = new int[m - 1][n];        // vertical N-S paths in the maze
            setWalls(0, 0, m, n, verticalWalls, horizontalWalls, visited);
            char[][] maze = mazeInitialize(height, width, horizontalWalls, verticalWalls);
            writeMazeArrayToFile(maze);
        }
    }
}
