package Environment.GameConfig;

import Environment.State.PacManCell;
import Environment.State.PacManEnemyPosition;
import Environment.State.PacManEnemyType;
import Environment.State.PacManPlayerPosition;
import vahy.api.experiment.ProblemConfig;

import java.util.List;

public class PacManGameConfig extends ProblemConfig {

    private final double bigBallReward;
    private final double smallBallReward;
    private final double stepPenalty;
    private final PacManEnemyType enemyType;
    private final List<List<PacManCell>> gameBoard;
    private final List<PacManEnemyPosition> enemyPositions;
    private final PacManPlayerPosition playerPosition;
    private final double trapChance;
    private final double oppositeMoveChance;
    private final double randomMoveChance;



    public PacManGameConfig(int maximalStepCountBound,
                            double bigBallReward,
                            double smallBallReward,
                            double stepPenalty,
                            PacManEnemyType enemyType,
                            List<List<PacManCell>> gameBoard,
                            List<PacManEnemyPosition> enemyPositions,
                            PacManPlayerPosition playerPosition,
                            double trapChance,
                            double oppositeMoveChance,
                            double randomMoveChance) {
        super(maximalStepCountBound);
        this.bigBallReward = bigBallReward;
        this.smallBallReward = smallBallReward;
        this.stepPenalty = stepPenalty;
        this.enemyType = enemyType;
        this.gameBoard = gameBoard;
        this.enemyPositions = enemyPositions;
        this.playerPosition = playerPosition;
        this.trapChance = trapChance;
        this.oppositeMoveChance = oppositeMoveChance;
        this.randomMoveChance = randomMoveChance;
    }

    public double getBigBallReward() {
        return bigBallReward;
    }

    public double getSmallBallReward() {
        return smallBallReward;
    }

    public double getStepPenalty() {
        return stepPenalty;
    }

    public PacManEnemyType getEnemyType() {
        return enemyType;
    }

    public List<List<PacManCell>> getGameBoard() {
        return gameBoard;
    }

    public List<PacManEnemyPosition> getEnemyPositions() {
        return enemyPositions;
    }

    public PacManPlayerPosition getPlayerPosition() {
        return playerPosition;
    }

    public double getTrapChance() {
        return trapChance;
    }

    public double getOppositeMoveChance() {
        return oppositeMoveChance;
    }

    public double getRandomMoveChance() {
        return randomMoveChance;
    }

    private String enemyPositionsString() {
        if (enemyPositions.size() == 0) {
            return "Enemies are traps." + System.lineSeparator() +
                    "Trap chance: " + String.valueOf(getTrapChance()) + System.lineSeparator() +
                    "Opposite move chance: " + String.valueOf(getOppositeMoveChance()) + System.lineSeparator() +
                    "Random move chance: " + String.valueOf(getRandomMoveChance()) + System.lineSeparator();
        }
        StringBuilder returnString = new StringBuilder();
        returnString.append(System.lineSeparator());
        for (PacManEnemyPosition pos : enemyPositions) {
            returnString.append("Enemy: ");
            returnString.append(pos.toString());
            returnString.append(System.lineSeparator());
        }
        return returnString.toString();
    }

    @Override
    public String toString() {
        return super.toString() +
                "Big ball reward: " + bigBallReward + System.lineSeparator() +
                "Small ball reward: " + smallBallReward + System.lineSeparator() +
                "Step penalty: " + stepPenalty + System.lineSeparator() +
                "Player position: " + playerPosition.toString() + System.lineSeparator() +
                "Enemy type: " + enemyType.toString() + System.lineSeparator() +
                "Enemy positions: " + enemyPositionsString();
    }

    @Override
    public String toLog() {
        return toString();
    }

    @Override
    public String toFile() {
        return toString();
    }
}
