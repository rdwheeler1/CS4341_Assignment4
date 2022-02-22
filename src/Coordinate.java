import java.util.Objects;

public class Coordinate {
    private int x, y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coordinate move(Action a){
        Coordinate newCoord = null;
        switch (a){
            case UP -> newCoord = new Coordinate(this.x, this.y++);
            case DOWN -> newCoord = new Coordinate(this.x, this.y--);
            case RIGHT -> newCoord = new Coordinate(this.x++, this.y);
            case LEFT -> newCoord = new Coordinate(this.x--, this.y);
        }
        return newCoord;
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
