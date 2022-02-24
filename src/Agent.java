import java.util.*;

public class Agent {

    private float reward;
    private float gamma;
    private float stepSize;

    public Agent(float reward, float gamma){
        this.reward = reward;
        this.gamma = gamma;
        this.stepSize = 0.1F;
    }

    // TODO: Add epsilon-greedy exploration
    public void qFunction(Coordinate state, Action action, HashMap<Coordinate, HashMap<Action, Float>> qValues){
        // New Q(s, a) = Current Q(s, a) + alpha(Reward + gamma * max(Q(s', a')) - Q(s, a))
        qEquation(state, action, qValues);
    }

    public void qEquation(Coordinate currentState, Action action, HashMap<Coordinate,
            HashMap<Action, Float>> qValues){

        HashMap<Action, Float> qValueMap = qValues.get(currentState);
        HashMap<Action, Float> copyOfCurrentQMap = qValues.get(currentState);
        float currentQValue = qValues.get(currentState).get(action);
        float alpha = reward;
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

        if (qValues.get(currentState).get(action) == currentQValue) {
            HashMap<Action, Float> actionMap = new HashMap<>();
            for (Action actionKey  : copyOfCurrentQMap.keySet()) {
                if (actionKey == action) {
                    actionMap.put(actionKey, newQValue);
                } else {
                    actionMap.put(actionKey, copyOfCurrentQMap.get(actionKey));
                }
            }
            qValues.put(currentState, actionMap);
        }

        qValues.get(currentState).put(action, newQValue);
        System.out.println("NEW: QVALUE: " + qValues.get(currentState));
        System.out.println("TEST: " + qValues.get(new Coordinate(0,0)));

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
     * return the highest q value of a given state
     * @param givenState given state
     * @param qValues map of queue values
     * @return the highest q value
     */
    public float getHighestQValue(Coordinate givenState, HashMap<Coordinate, HashMap<Action, Float>> qValues) {
        Action bestAction = null;
        HashMap<Action, Float> qValueMap = qValues.get(givenState);
        float maxQ = Collections.max(qValueMap.values());

        return maxQ;
    }

    public void updateQValue(Coordinate updatedState, Coordinate newState, Action prevAction, HashMap<Coordinate, HashMap<Action, Float>> qValues){
        float updatedValue = qValues.get(updatedState).get(prevAction);
        float newValue = getHighestQValue(newState, qValues);

        qValues.get(updatedState).replace(prevAction, calculateQValue(updatedValue, newValue));
    }

    public float calculateQValue(float oldValue, float newValue){
        //New Q(s, a) = Current Q(s, a) + alpha(Reward + gamma * max(Q(s', a')) - Q(s, a))
        return (oldValue + stepSize * (reward + gamma * (newValue) - oldValue));
    }

    /**
     * Moves the agent to either the desired Action or a deflected Action
     * @param currentState the current state of the agent
     * @param desiredProb the desired probability of moving in the desired Action
     * @param action the desired Action
     */
    public Coordinate move (Coordinate currentState, float desiredProb, Action action) {
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
        return newState;
    }

    public Action getMove(Coordinate startCoord, Action prevAction , HashMap<Coordinate, HashMap<Action, Float>> qValues){
        Action move = null;

        if(prevAction == Action.UP){
            float a1 = qValues.get(startCoord).get(Action.RIGHT);
            float a2 = qValues.get(startCoord).get(Action.LEFT);
            float a3 = qValues.get(startCoord).get(Action.UP);

            float max = Math.max(a1,a2);
            max = Math.max(max,a3);

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

            float max = Math.max(a1,a2);
            max = Math.max(max,a3);

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

            float max = Math.max(a1,a2);
            max = Math.max(max,a3);

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

            float max = Math.max(a1,a2);
            max = Math.max(max,a3);

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
            case LEFT:
                newAction = Action.DOWN;
                break;
            case RIGHT:
                newAction = Action.UP;
                break;
            case DOWN:
                newAction = Action.RIGHT;
                break;
            case UP:
                newAction = Action.LEFT;
                break;
        }
        return newAction;
    }

    public Action deflectRight(Action a){
        Action newAction = a;
        switch (a){
            case LEFT:
                newAction = Action.UP;
                break;
            case RIGHT:
                newAction = Action.DOWN;
                break;
            case DOWN:
                newAction = Action.LEFT;
                break;
            case UP:
                newAction = Action.RIGHT;
                break;
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
