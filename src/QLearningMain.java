import java.io.File;
import java.io.IOException;
import java.util.*;

public class QLearningMain {

    public static void main(String[] args) {

        String filename = args[0];
        float timeToRun = Float.parseFloat(args[1]);
        float desiredProb = Float.parseFloat(args[2]);
        float reward = Float.parseFloat(args[3]);
        int rows = 0, cols = 0;

        HashMap<Coordinate, Integer> gridMap = new HashMap<>();
        HashMap<Coordinate, HashMap<Action, Float>> qValues = new HashMap<>();
        List<String []> elements = new ArrayList<>();

        try {
            File myObj = new File(filename);
            Scanner scanner = new Scanner(myObj);
            while (scanner.hasNextLine()) {
                rows += 1;
                String data = scanner.nextLine();
                String[] values = data.split("\\t");
                elements.add(values);
                cols = values.length;
            }
            int [][] grid = createGrid(elements, rows, cols);
            gridMap = createGridMap(grid);
            scanner.close();
        } catch (IOException i) {
            System.out.println("File Exception");
        }
    }

    /**
     * Creates a 2D-array of ints of the grid provided from the file
     * @param values the value at each coordinate
     * @param row the number of rows
     * @param col the number of columns
     * @return 2D-array representation of the grid
     */
    public static int[][] createGrid(List<String []> values, int row, int col) {
        int [][] grid = new int [row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                grid [i][j] = Integer.parseInt(values.get(i)[j]);
            }
        }
        return grid;
    }

    /**
     * Converts the 2D-array of ints into a HashMap of the grid
     * @param grid the 2D-array representation of the grid
     * @return the map representation of the grid
     */
    public static HashMap<Coordinate, Integer> createGridMap(int [][] grid){
        HashMap<Coordinate, Integer> gridMap = new HashMap<>();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                gridMap.put(new Coordinate(col, row), grid[row][col]);
            }
        }
        return gridMap;
    }

    public void runQLearning(List<String []> elements, HashMap<Coordinate, HashMap<Action, Float>> qValues, HashMap<Coordinate, Integer> gridMap){
        //for some time t
        int startY = (int) Math.random() * elements.size();
        int startX = (int) Math.random() * elements.get(0).length;

        while(elements.get(startY)[startX] != "0"){
            startY = (int) Math.random() * elements.size();
            startX = (int) Math.random() * elements.get(0).length;
        }

        Coordinate startCoordinate = new Coordinate(startX, startY);

        Agent agent = new Agent();

        int randMove = (int) Math.floor(Math.random() * 4);

        Action startAction = new Action[]{Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT}[randMove];

        //while havent reached the goal state
        agent.qFunction(startCoordinate, startAction, qValues);

        //take move
        Action move = agent.getMove(startCoordinate, startAction, qValues);

        //move to next state
        // if(out of bounds)
        // then dont change coordinate but change qValue at location move to bad
        Coordinate newStart = startCoordinate.move(move);

        if(!gridMap.containsKey(newStart)){
            //recalculate q values
        }
        else{
            startCoordinate = newStart;
        }



    }
}
