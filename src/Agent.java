import java.util.*;

public class Agent {

    private final float reward;
    private final float gamma;
    private final float stepSize;
    private final float epsilon;
    private Action actionTaken;

    public Agent(float reward){
        this.reward = reward;
        this.gamma = 0.9F;
        this.stepSize = 0.1F;
        this.epsilon = 0.1F;
        this.actionTaken = null;
    }

    public Action getActionTaken() {
        return actionTaken;
    }

    /**
     * Gets the best Action based on current state and current Q-values
     * @param currentState the current state of the agent
     * @param qValues the current Q-values of the board
     * @return the best Action the agent will take
     */
    public Action getAction(Coordinate currentState, HashMap<Coordinate, HashMap<Action, Float>> qValues) {
        Action bestAction = null;
        HashMap<Action, Float> qValueMap = qValues.get(currentState);
        float maxQ = Collections.max(qValueMap.values());
        float randomChance = (float) Math.random() * 1;

        if(randomChance < epsilon){
            int randMove = (int) Math.floor(Math.random() * 4);
            Action currentAction = new Action[]{Action.UP, Action.DOWN, Action.LEFT, Action.RIGHT}[randMove];
            bestAction = currentAction;
        }
        else{
            for (Map.Entry<Action, Float> qEntry : qValueMap.entrySet()) {
                if (qEntry.getValue() == maxQ) {
                    bestAction = qEntry.getKey();
                    break;
                }
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
        if(qValues.containsKey(givenState)){
            HashMap<Action, Float> qValueMap = qValues.get(givenState);
            return Collections.max(qValueMap.values());
        }
        else{
            return -10F;
        }
    }

    public void updateQValue(Coordinate updatedState, Coordinate newState, Action prevAction, HashMap<Coordinate, HashMap<Action, Float>> qValues){
        float updatedValue = qValues.get(updatedState).get(prevAction);
        float newValue;
        if(!qValues.containsKey(newState)){
            newValue = -10F;
        }
        else{
            newValue = getHighestQValue(newState, qValues);
        }
        qValues.get(updatedState).replace(prevAction, calculateQValue(updatedValue, newValue));
    }

    public float calculateQValue(float oldValue, float newValue){
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
            switch (action) {
                case UP:
                    newState = new Coordinate(currentState.getX(), currentState.getY() - 1);
                    actionTaken = Action.UP;
                    break;
                case DOWN:
                    newState = new Coordinate(currentState.getX(), currentState.getY() + 1);
                    actionTaken = Action.DOWN;
                    break;
                case LEFT:
                    newState = new Coordinate(currentState.getX() - 1, currentState.getY());
                    actionTaken = Action.LEFT;
                    break;
                case RIGHT:
                    newState = new Coordinate(currentState.getX() + 1, currentState.getY());
                    actionTaken = Action.RIGHT;
                    break;
            }
        // Takes a deflected action
        } else if (chance < desiredProb + deflectionChance) {
            switch (deflectLeft(action)) {
                case UP:
                    newState = new Coordinate(currentState.getX(), currentState.getY() - 1);
                    actionTaken = Action.UP;
                    break;
                case DOWN:
                    newState = new Coordinate(currentState.getX(), currentState.getY() + 1);
                    actionTaken = Action.DOWN;
                    break;
                case LEFT:
                    newState = new Coordinate(currentState.getX() - 1, currentState.getY());
                    actionTaken = Action.LEFT;
                    break;
                case RIGHT:
                    newState = new Coordinate(currentState.getX() + 1, currentState.getY());
                    actionTaken = Action.RIGHT;
                    break;
            }
        // Takes the other deflected action
        } else if (chance < (desiredProb + deflectionChance) + deflectionChance) {
            switch (deflectRight(action)) {
                case UP:
                    newState = new Coordinate(currentState.getX(), currentState.getY() - 1);
                    actionTaken = Action.UP;
                    break;
                case DOWN:
                    newState = new Coordinate(currentState.getX(), currentState.getY() + 1);
                    actionTaken = Action.DOWN;
                    break;
                case LEFT:
                    newState = new Coordinate(currentState.getX() - 1, currentState.getY());
                    actionTaken = Action.LEFT;
                    break;
                case RIGHT:
                    newState = new Coordinate(currentState.getX() + 1, currentState.getY());
                    actionTaken = Action.RIGHT;
                    break;
            }
        }
        return newState;
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
}
