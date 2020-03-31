package Environment.State;

import java.util.Objects;
import java.util.SplittableRandom;

public class PacManStaticPart {
    private final double bigBallReward;
    private final double smallBallReward;
    private final double stepPenalty;
    private final double trapProbability;
    private final double oppositeMoveProbability;
    private final double randomMoveProbability;
    private final PacManEnemyType enemyType;
    private SplittableRandom random;

    public PacManStaticPart(double bigBallReward, double smallBallReward, double stepPenalty, double trapProbability, double oppositeMoveProbability, double randomMoveProbability, PacManEnemyType enemyType, SplittableRandom random) {
        this.bigBallReward = bigBallReward;
        this.smallBallReward = smallBallReward;
        this.stepPenalty = stepPenalty;
        this.trapProbability = trapProbability;
        this.oppositeMoveProbability = oppositeMoveProbability;
        this.randomMoveProbability = randomMoveProbability;
        this.enemyType = enemyType;
        this.random = random;
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

    public SplittableRandom getRandom() {
        return random;
    }

    public double getTrapProbability() {
        return trapProbability;
    }

    public double getOppositeMoveProbability() {
        return oppositeMoveProbability;
    }

    public double getRandomMoveProbability() {
        return randomMoveProbability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacManStaticPart that = (PacManStaticPart) o;
        return Double.compare(that.bigBallReward, bigBallReward) == 0 &&
                Double.compare(that.smallBallReward, smallBallReward) == 0 &&
                Double.compare(that.stepPenalty, stepPenalty) == 0 &&
                enemyType == that.enemyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bigBallReward, smallBallReward, stepPenalty, enemyType);
    }
}
