import javax.print.attribute.standard.PresentationDirection;
import java.lang.invoke.DirectMethodHandle$Holder;
import java.util.HashMap;

public class Agent {

    // TODO: Possibly move function to Main(?)
    // TODO: Add Action as a parameter
    public void qFunction(Coordinate state, Action action, HashMap<Coordinate, HashMap<Action, Float>> qValues){
        // TODO: Add Q(s, a) equation
        // New Q(s, a) = Current Q(s, a) + alpha(Reward + gamma * max(Q(s', a')) - Q(s, a))
        qEquation(state, action, qValues);
    }

    public void qEquation(Coordinate s, Action a, HashMap<Coordinate, HashMap<Action, Float>> qValues){

        float currentQValue = qValues.get(s).get(a);
        float alpha = 0.4F;
        float reward = 0.4F;
        float gamma = 0.4F;
        float max = 0.0F;

        if(a == Action.UP){
            max = Math.fma(qValues.get(s).get(Action.RIGHT), qValues.get(s).get(Action.LEFT),
                    qValues.get(s).get(Action.UP));
        }
        else if (a == Action.DOWN){
            max = Math.fma(qValues.get(s).get(Action.RIGHT), qValues.get(s).get(Action.LEFT),
                    qValues.get(s).get(Action.DOWN));
        }
        else if(a == Action.LEFT){
            max = Math.fma(qValues.get(s).get(Action.DOWN), qValues.get(s).get(Action.LEFT),
                    qValues.get(s).get(Action.UP));
        }
        else if(a == Action.RIGHT){
            max = Math.fma(qValues.get(s).get(Action.RIGHT), qValues.get(s).get(Action.DOWN),
                    qValues.get(s).get(Action.UP));
        }

        float newQValue = currentQValue + alpha * (reward + gamma * max - currentQValue);

        qValues.get(s).put(a,newQValue);

    }

    public void move (float desiredProb, String action) {
    }

    public float getMaxQ(GridWorld qMap, Coordinate nextState, String nextAction) {
        float currentQValue = 0;
        return currentQValue;
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
