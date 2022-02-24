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
            initializeQValues(gridMap, qValues);
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) / 1000 < timeToRun){
                runQLearning(gridMap, qValues, desiredProb, reward, rows, cols);
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

    public static void runQLearning(HashMap<Coordinate, Integer> gridMap,
                                    HashMap<Coordinate, HashMap<Action, Float>> qValues, float desiredProb,
                                    float reward, int row, int col) {
        Agent agent = new Agent();
        Random random = new Random();
        List<Coordinate> coordinateKeyList = new ArrayList<>(gridMap.keySet());
        Coordinate randomStartCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));

        if (gridMap.get(randomStartCoordinate) != 0) {
            randomStartCoordinate = coordinateKeyList.get(random.nextInt(coordinateKeyList.size()));
        }

        System.out.println("RANDOM START: (" + randomStartCoordinate.getX() + "," +
                randomStartCoordinate.getY() + ")");

        int randMove = (int) Math.floor(Math.random() * 4);
        Action startAction = new Action[]{Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT}[randMove];
        Action prevMove = startAction;

        System.out.println(gridMap.get(randomStartCoordinate));

        //while havent reached the goal state
        while (gridMap.get(randomStartCoordinate) == 0) {

            //Action bestAction = agent.getBestAction(randomStartCoordinate, qValues);
            agent.qFunction(randomStartCoordinate, prevMove, qValues, reward);

            //take move
            Action move = agent.getMove(randomStartCoordinate, prevMove, qValues);

            //move to next state
            // if(out of bounds)
            // then dont change coordinate but change qValue at location move to bad
            // TODO: If out of bounds, choose the next best action from current state.
            // TODO: If it doesn't get the best one, infinite loop between states
            if (isOutOfBounds(row, col, randomStartCoordinate, move)) {
                move = getVerifiedAction(row, col, randomStartCoordinate, move);
            }

            Coordinate newStart = randomStartCoordinate.move(move);
            agent.move(gridMap, randomStartCoordinate, desiredProb, prevMove);
            randomStartCoordinate = newStart;
            prevMove = move;
        }
        System.out.println(randomStartCoordinate.getX() + "," + randomStartCoordinate.getY());
    }

    /**
     * Verifies that the agent does not go out-of-bounds
     * @param row the number of rows
     * @param col the number of columns
     * @param state the current state of the agent
     * @param action the action that the agent plans to take
     * @return true if the agent goes out-of-bounds, otherwise false
     */
    public static boolean isOutOfBounds(int row, int col, Coordinate state, Action action) {
        boolean result = false;
        // Upper Bound
        if (action == Action.UP) {
            if (state.getY() - 1 < 0) {
                if (state.getX() == col - 1) {
                    result = true;
                } else if (state.getX() == 0) {
                    result = true;
                } else {
                    result = true;
                }
            }
        // Lower Bound
        } else if (action == Action.DOWN) {
            if (state.getY() + 1 > row - 1) {
                if (state.getX() == 0) {
                    result = true;
                } else if (state.getX() == col - 1) {
                    result = true;
                } else {
                    result = true;
                }
            }
        // Left Bound
        } else if (action == Action.LEFT) {
            if (state.getX()  - 1 < 0) {
                if (state.getY() ==  0) {
                    result = true;
                } else if (state.getY() == row - 1) {
                    result = true;
                } else  {
                    result = true;
                }
            }
        // Right Bound
        } else if (action == Action.RIGHT) {
            if (state.getX() + 1 > col - 1) {
                if (state.getY() == 0) {
                    result = true;
                } else if (state.getY() == row - 1) {
                    result = true;
                } else {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Takes the action that doesn't have the agent go out-of-bounds
     * @param row the number of rows
     * @param col the number of columns
     * @param state the current state of the agent
     * @param action the action the agents plans to take
     * @return the action that the agent will take that doesn't have it go out-of-bounds
     */
    public static Action getVerifiedAction(int row, int col, Coordinate state, Action action) {
        Action nextAction = null;
        // Upper Bound
        if (action == Action.UP) {
            if (state.getY() - 1 < 0) {
                if (state.getX() == col - 1) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.DOWN, Action.LEFT}[randMove];
                } else if (state.getX() == 0) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.DOWN, Action.RIGHT}[randMove];
                } else {
                    int randMove = (int) Math.floor(Math.random() * 3);
                    nextAction = new Action[]{Action.DOWN, Action.LEFT, Action.RIGHT}[randMove];
                }
            }
        // Lower Bound
        } else if (action == Action.DOWN) {
            if (state.getY() + 1 > row - 1) {
                if (state.getX() == 0) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.UP, Action.RIGHT}[randMove];
                } else if (state.getX() == col - 1) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.UP, Action.LEFT}[randMove];
                } else {
                    int randMove = (int) Math.floor(Math.random() * 3);
                    nextAction = new Action[]{Action.UP, Action.LEFT, Action.RIGHT}[randMove];
                }
            }
        // Left Bound
        } else if (action == Action.LEFT) {
            if (state.getX()  - 1 < 0) {
                if (state.getY() ==  0) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.DOWN, Action.RIGHT}[randMove];
                } else if (state.getY() == row - 1) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.UP, Action.RIGHT}[randMove];
                } else  {
                    int randMove = (int) Math.floor(Math.random() * 3);
                    nextAction = new Action[]{Action.UP, Action.DOWN, Action.RIGHT}[randMove];
                }
            }
        // Right Bound
        } else if (action == Action.RIGHT) {
            if (state.getX() + 1 > col - 1) {
                if (state.getY() == 0) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.DOWN, Action.LEFT}[randMove];
                } else if (state.getY() == row - 1) {
                    int randMove = (int) Math.floor(Math.random() * 2);
                    nextAction = new Action[]{Action.UP, Action.LEFT}[randMove];
                } else {
                    int randMove = (int) Math.floor(Math.random() * 3);
                    nextAction = new Action[]{Action.UP, Action.DOWN, Action.LEFT}[randMove];
                }
            }
        }
        return nextAction;
    }
}
