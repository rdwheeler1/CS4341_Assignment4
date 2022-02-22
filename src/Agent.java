public class Agent {
    private int xCoord;
    private int yCoord;

    // TODO: Possibly move function to Main(?)
    // TODO: Add Action as a parameter
    public void qFunction(Coordinate state, String action){
        // TODO: Add Q(s, a) equation
        // New Q(s, a) = Current Q(s, a) + alpha(Reward + gamma * max(Q(s', a')) - Q(s, a))

    }

    public void move (float desiredProb, String action) {

    }

    public float getMaxQ(GridWorld qMap, Coordinate nextState, String nextAction) {
        float currentQValue = 0;
        return currentQValue;
    }

    public boolean isDeflected(Coordinate state, String action) {
        boolean result = false;
        return result;
    }
}
