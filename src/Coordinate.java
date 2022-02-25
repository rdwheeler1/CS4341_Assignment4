import java.util.Objects;

public class Coordinate {
    private final int x;
    private final int y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coordinate copy(){
        int x = this.x;
        int y = this.y;
        return new Coordinate(x,y);
    }

    // TODO: Make sure we don't need this before submission
    public Coordinate move(Action a){
        int x = this.x;
        int y = this.y;
        switch (a) {
            case UP -> y--;
            case DOWN -> y++;
            case RIGHT -> x++;
            case LEFT -> x--;
        }
        return new Coordinate(x,y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
