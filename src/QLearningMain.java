import java.io.File;
import java.io.IOException;
import java.util.*;

public class QLearningMain {

    public static void main(String[] args) {

        // TODO: Add more test grids to test
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
            initializeQValues(gridMap, qValues);
            long startTime = System.currentTimeMillis();
            long elapsedTime;

            do {
                runQLearning(gridMap, qValues, desiredProb, reward);
                elapsedTime = (new Date()).getTime() - startTime;
            } while (elapsedTime < timeToRun * 1000);

            printPolicy(rows,cols,qValues, gridMap);
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

    /**
     * Initializes the Q-values of the map
     * @param gridMap the map representation of the grid
     * @param qValues the Q-values of the map
     */
    public static void initializeQValues(HashMap<Coordinate, Integer> gridMap,
                                         HashMap<Coordinate, HashMap<Action, Float>> qValues) {
        Action [] actions = {Action.UP, Action.LEFT, Action.RIGHT, Action.DOWN};
        Action [] goalActions = {Action.UP, Action.LEFT, Action.RIGHT, Action.DOWN};

        for (Coordinate key : gridMap.keySet()) {
            HashMap<Action, Float> actionMap = new HashMap<>();

            for (Action action : actions) {
                actionMap.put(action, 0.0F);
            }
            if (gridMap.get(key) == 0){
                qValues.put(key, actionMap);
            }
            else{
                HashMap<Action, Float> goalActionMap = new HashMap<>();
                for (Action action: goalActions){
                    goalActionMap.put(action, gridMap.get(key).floatValue());
                }
                qValues.put(key, goalActionMap);
            }
        }
    }

    // TODO: Final submission: output the policy (the path of our grid)
    public static void runQLearning(HashMap<Coordinate, Integer> gridMap, HashMap<Coordinate,
                HashMap<Action, Float>> qValues, float desiredProb, float reward) {
        Agent agent = new Agent(reward, 0.9F);
        Random random = new Random();

        List<Coordinate> coordinateKeyList = new ArrayList<>(gridMap.keySet());
        Coordinate currentCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));

        if (gridMap.get(currentCoordinate) != 0) {
            currentCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));
        }

        System.out.println("RANDOM START: (" + currentCoordinate.getX() + "," +
                currentCoordinate.getY() + ")");

        int randMove = (int) Math.floor(Math.random() * 4);
        Action currentAction = new Action[]{Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT}[randMove];

        //while havent reached a goal state explore
        while(gridMap.get(currentCoordinate) == 0){

            Coordinate previousMove = currentCoordinate.copy();

            //take that action
            Coordinate tempCoordinate = agent.move(currentCoordinate, desiredProb, currentAction);

            agent.updateQValue(previousMove,tempCoordinate,agent.getActionTaken(),qValues);
            if(gridMap.containsKey(tempCoordinate)){
                currentCoordinate = tempCoordinate;
            }
            currentAction = agent.getAction(currentCoordinate,qValues);
        }
        System.out.println("End Coordinate: (" + currentCoordinate.getX() + ","
                + currentCoordinate.getY() + ")");
        printQValuesCoordinates(qValues);
    }

    public static void printQValuesCoordinates(HashMap<Coordinate, HashMap<Action, Float>> hashMapSlots) {
        for (Map.Entry<Coordinate, HashMap<Action, Float>> qEntry : hashMapSlots.entrySet()) {
            System.out.println("At coordinate (" + qEntry.getKey().getX() + "," + qEntry.getKey().getY() + ") the q values are:");
            printQValues(qEntry.getValue());
        }
    }

    public static void printQValues(HashMap<Action, Float> floatVales){
        for (Map.Entry<Action, Float> qEntry : floatVales.entrySet()) {
            System.out.println(qEntry.getKey() + ": " + qEntry.getValue());
        }
        System.out.println();
    }

    public static void printPolicy(int row, int col, HashMap<Coordinate, HashMap<Action, Float>> qValues, HashMap<Coordinate, Integer> gridMap) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                float maxQ = Collections.max(qValues.get(new Coordinate(j, i)).values());
                if (gridMap.get(new Coordinate(j, i)) != 0) {
                    System.out.print(" O ");
                } else {
                    for (Map.Entry<Action, Float> qEntry : qValues.get(new Coordinate(j, i)).entrySet()) {
                        if (qEntry.getValue() == maxQ) {
                            if (qEntry.getKey().equals(Action.UP)) {
                                System.out.print(" ^ ");
                                break;
                            } else if (qEntry.getKey().equals(Action.DOWN)) {
                                System.out.print(" v ");
                                break;
                            } else if (qEntry.getKey().equals(Action.LEFT)) {
                                System.out.print(" < ");
                                break;
                            } else if (qEntry.getKey().equals(Action.RIGHT)) {
                                System.out.print(" > ");
                                break;
                            }
                        }
                    }
                }
            }
            System.out.println();
        }
    }
}
