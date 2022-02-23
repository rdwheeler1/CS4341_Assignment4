import java.util.*;

public class Agent {

    // TODO: Add epsilon-greedy exploration
    public void qFunction(Coordinate state, Action action, HashMap<Coordinate, HashMap<Action, Float>> qValues,
                          float reward){
        // New Q(s, a) = Current Q(s, a) + alpha(Reward + gamma * max(Q(s', a')) - Q(s, a))
        qEquation(state, action, qValues, reward);
    }

    public void qEquation(Coordinate currentState, Action action, HashMap<Coordinate,
            HashMap<Action, Float>> qValues, float reward){

        HashMap<Action, Float> qValueMap = qValues.get(currentState);
        float currentQValue = qValues.get(currentState).get(action);
        float alpha = 0.1F;
        float gamma = 0.9F;
        float maxQValue = Collections.max(qValueMap.values());

        for (Map.Entry<Action, Float> qEntry : qValueMap.entrySet()) {
            if (qEntry.getValue() == maxQValue) {
                action = qEntry.getKey();
            }
        }

//        if(a == Action.UP){
//            maxQValue = Math.fma(qValues.get(s).get(Action.RIGHT), qValues.get(s).get(Action.LEFT),
//                    qValues.get(s).get(Action.UP));
//        }
//        else if (a == Action.DOWN){
//            maxQValue = Math.fma(qValues.get(s).get(Action.RIGHT), qValues.get(s).get(Action.LEFT),
//                    qValues.get(s).get(Action.DOWN));
//        }
//        else if(a == Action.LEFT){
//            maxQValue = Math.fma(qValues.get(s).get(Action.DOWN), qValues.get(s).get(Action.LEFT),
//                    qValues.get(s).get(Action.UP));
//        }
//        else if(a == Action.RIGHT){
//            maxQValue = Math.fma(qValues.get(s).get(Action.RIGHT), qValues.get(s).get(Action.DOWN),
//                    qValues.get(s).get(Action.UP));
//        }

        float newQValue = currentQValue + alpha * (reward + (gamma * maxQValue) - currentQValue);
        qValues.get(currentState).put(action, newQValue);
        System.out.println("NEW: QVALUE: " + qValues.get(currentState).get(action));
    }

    /**
     * Gets the best Action based on current state and current Q-values
     * @param currentState the current state of the agent
     * @param qValues the current Q-values of the board
     * @return the best Action the agent will take
     */
    public Action getBestAction(Coordinate currentState, HashMap<Coordinate, HashMap<Action, Float>> qValues) {
        Action bestAction = null;
        HashMap<Action, Float> qValueMap = qValues.get(currentState);
        float maxQ = Collections.max(qValueMap.values());

        for (Map.Entry<Action, Float> qEntry : qValueMap.entrySet()) {
            if (qEntry.getValue() == maxQ) {
                bestAction = qEntry.getKey();
            }
        }
        return bestAction;
    }

    /**
     * Moves the agent to either the desired Action or a deflected Action
     * @param gridMap the map representation of the grid
     * @param currentState the current state of the agent
     * @param desiredProb the desired probability of moving in the desired Action
     * @param action the desired Action
     */
    public void move (HashMap<Coordinate, Integer> gridMap, Coordinate currentState, float desiredProb, Action action) {
        Random random = new Random();
        float chance = random.nextFloat(1);
        float deflectionChance = (1 - desiredProb) / 2;
        Coordinate newState = null;

        // Takes the desired action
        if (chance < desiredProb) {
            newState = switch (action) {
                case UP -> new Coordinate(currentState.getX(), currentState.getY() - 1);
                case DOWN -> new Coordinate(currentState.getX(), currentState.getY() + 1);
                case LEFT -> new Coordinate(currentState.getX() - 1, currentState.getY());
                case RIGHT -> new Coordinate(currentState.getX() + 1, currentState.getY());
            };
        // Takes a deflected action
        } else if (chance < desiredProb + deflectionChance) {
            newState = switch (deflectLeft(action)) {
                case UP -> new Coordinate(currentState.getX(), currentState.getY() - 1);
                case DOWN -> new Coordinate(currentState.getX(), currentState.getY() + 1);
                case LEFT -> new Coordinate(currentState.getX() - 1, currentState.getY());
                case RIGHT -> new Coordinate(currentState.getX() + 1, currentState.getY());
            };
        // Takes the other deflected action
        } else if (chance < (desiredProb + deflectionChance) + deflectionChance) {
            newState = switch (deflectRight(action)) {
                case UP -> new Coordinate(currentState.getX(), currentState.getY() - 1);
                case DOWN -> new Coordinate(currentState.getX(), currentState.getY() + 1);
                case LEFT -> new Coordinate(currentState.getX() - 1, currentState.getY());
                case RIGHT -> new Coordinate(currentState.getX() + 1, currentState.getY());
            };
        }
        gridMap.put(newState, gridMap.get(newState));
    }

    public Action getMove(Coordinate startCoord, Action prevAction , HashMap<Coordinate, HashMap<Action, Float>> qValues){
        Action move = null;

        if(prevAction == Action.UP){
            float a1 = qValues.get(startCoord).get(Action.RIGHT);
            float a2 = qValues.get(startCoord).get(Action.LEFT);
            float a3 = qValues.get(startCoord).get(Action.UP);

            float max = Math.fma(a1,a2,a3);

            if(max == a1){
                move = Action.RIGHT;
            }
            else if(max == a2){
                move = Action.LEFT;
            }
            else if(max ==a3){
                move = Action.UP;
            }

        }
        else if (prevAction == Action.DOWN){
            float a1 = qValues.get(startCoord).get(Action.RIGHT);
            float a2 = qValues.get(startCoord).get(Action.LEFT);
            float a3 = qValues.get(startCoord).get(Action.DOWN);

            float max = Math.fma(a1,a2,a3);

            if(max == a1){
                move = Action.RIGHT;
            }
            else if(max == a2){
                move = Action.LEFT;
            }
            else if(max ==a3){
                move = Action.DOWN;
            }
        }
        else if(prevAction == Action.LEFT){
            float a1 = qValues.get(startCoord).get(Action.DOWN);
            float a2 = qValues.get(startCoord).get(Action.LEFT);
            float a3 = qValues.get(startCoord).get(Action.UP);

            float max = Math.fma(a1,a2,a3);

            if(max == a1){
                move = Action.DOWN;
            }
            else if(max == a2){
                move = Action.LEFT;
            }
            else if(max ==a3){
                move = Action.UP;
            }
        }
        else if(prevAction == Action.RIGHT){
            float a1 = qValues.get(startCoord).get(Action.DOWN);
            float a2 = qValues.get(startCoord).get(Action.LEFT);
            float a3 = qValues.get(startCoord).get(Action.UP);

            float max = Math.fma(a1,a2,a3);

            if(max == a1){
                move = Action.DOWN;
            }
            else if(max == a2){
                move = Action.LEFT;
            }
            else if(max ==a3){
                move = Action.UP;
            }
        }

        move = isDeflected(move);

        return move;
    }

    public Action deflectLeft(Action a){
        Action newAction = a;
        switch (a){
            case LEFT -> newAction = Action.DOWN;
            case RIGHT -> newAction = Action.UP;
            case DOWN -> newAction = Action.RIGHT;
            case UP -> newAction = Action.LEFT;
        }
        return newAction;
    }

    public Action deflectRight(Action a){
        Action newAction = a;
        switch (a){
            case LEFT -> newAction = Action.UP;
            case RIGHT -> newAction = Action.DOWN;
            case DOWN -> newAction = Action.LEFT;
            case UP -> newAction = Action.RIGHT;
        }
        return newAction;
    }

    public Action isDeflected(Action move) {
        int deflectionChance = (int) Math.random() * 10;
        Action returnMove = move;

        if(deflectionChance < 5){
            returnMove = deflectLeft(move);
        }
        else if(deflectionChance < 10){
            returnMove = deflectRight(move);
        }
        return returnMove;
    }
}
