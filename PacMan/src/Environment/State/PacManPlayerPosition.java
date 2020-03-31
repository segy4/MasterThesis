package Environment.State;

public class PacManPlayerPosition extends PacManEnemyPosition {
    public PacManPlayerPosition(int x, int y) {
        super(x, y);
    }

    public PacManPlayerPosition(PacManPlayerPosition playerPosition) {
        super(playerPosition);
    }
}
