package Environment.State;

import java.util.Objects;

public class PacManEnemyPosition {
    private int x;
    private int y;

    public PacManEnemyPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public PacManEnemyPosition(PacManEnemyPosition position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    public void setXandY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "X: " + String.valueOf(x) + " Y: " + String.valueOf(y);
    }

    public boolean isSame(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacManEnemyPosition that = (PacManEnemyPosition) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
