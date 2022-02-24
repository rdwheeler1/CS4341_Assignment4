import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Clock;
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
            initializeQValues(gridMap, qValues);

            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) / 1000 < timeToRun){
                runQLearning(gridMap, qValues, desiredProb, reward);
            }

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
        HashMap<Action, Float> actionMap = new HashMap<>();

        for (Action action : actions) {
            actionMap.put(action, 0.0F);
        }

        for (Coordinate key : gridMap.keySet()) {
            qValues.put(key, actionMap);
        }
    }

//    public static void runQLearning(HashMap<Coordinate, Integer> gridMap,
//                                    HashMap<Coordinate, HashMap<Action, Float>> qValues, float desiredProb,
//                                    float reward) {
//        Agent agent = new Agent(reward, 1.0F);
//
//        Random random = new Random();
//        PriorityQueue<Coordinate> currentCoordinate = new PriorityQueue<>();
//        List<Coordinate> coordinateKeyList = new ArrayList<>(gridMap.keySet());
//        Coordinate randomStartCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));
//
//        if (gridMap.get(randomStartCoordinate) != 0) {
//            randomStartCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));
//        }
//
//        System.out.println("RANDOM START: (" + randomStartCoordinate.getX() + "," +
//                randomStartCoordinate.getY() + ")");
//
//        int randMove = (int) Math.floor(Math.random() * 4);
//        Action startAction = new Action[]{Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT}[randMove];
//        Action prevMove = startAction;
//
//        System.out.println(gridMap.get(randomStartCoordinate));
//        currentCoordinate.add(randomStartCoordinate);
//
//        //while havent reached the goal state
//        while (gridMap.get(randomStartCoordinate) == 0) {
//
//            //Action bestAction = agent.getBestAction(randomStartCoordinate, qValues);
//            agent.qFunction(randomStartCoordinate, prevMove, qValues);
//
//            //take move
//            Action move = agent.getMove(randomStartCoordinate, prevMove, qValues);
//
//            //move to next state
//            // if(out of bounds)
//            // then dont change coordinate but change qValue at location move to bad
//            Coordinate newStart = randomStartCoordinate.move(move);
//
//            if (!gridMap.containsKey(newStart)) {
////                qValues.get(randomStartCoordinate).put(prevMove,Float.MIN_VALUE);
//            } else {
//                agent.move(gridMap, randomStartCoordinate, desiredProb, prevMove, currentCoordinate);
//                agent.updateQValue();
//                randomStartCoordinate = newStart;
//                prevMove = move;
//            }
//        }
//        System.out.println(randomStartCoordinate.getX() + " ," + randomStartCoordinate.getY());
//    }

    public static void runQLearning(HashMap<Coordinate, Integer> gridMap, HashMap<Coordinate,
                HashMap<Action, Float>> qValues, float desiredProb, float reward) {
        Agent agent = new Agent(reward, 1.0F);
        Random random = new Random();

        //list of coordinates with top being current coordinate
        PriorityQueue<Coordinate> coordinateQueue = new PriorityQueue<>();


        List<Coordinate> coordinateKeyList = new ArrayList<>(gridMap.keySet());
        Coordinate currentCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));

        if (gridMap.get(currentCoordinate) != 0) {
            currentCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));
        }

        System.out.println("RANDOM START: (" + currentCoordinate.getX() + "," +
                currentCoordinate.getY() + ")");

         int randMove = (int) Math.floor(Math.random() * 4);
        Action currentAction = new Action[]{Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT}[randMove];

        System.out.println(gridMap.get(currentCoordinate));

        //while havent reached a goal state explore
        while(gridMap.get(currentCoordinate) == 0){

            //choose best action from here (with epsilon value)
            Action nextAction = agent.getBestAction(currentCoordinate,qValues);

            //take that action
            Coordinate prevCoordinate = currentCoordinate.copy();
            currentCoordinate = agent.move(currentCoordinate,desiredProb,nextAction);

            //update previous state with max values from state im on now
            agent.updateQValue(prevCoordinate, currentCoordinate, nextAction, qValues);

        }
    }

}
