package vahy.examples.bomberman;

import vahy.api.episode.PolicyCategoryInfo;
import vahy.api.episode.PolicyShuffleStrategy;
import vahy.api.experiment.ProblemConfig;
import vahy.impl.RoundBuilder;
import vahy.impl.episode.InvalidInstanceSetupException;
import vahy.utils.ImmutableTuple;

import java.io.IOException;
import java.util.List;

public class BomberManConfig extends ProblemConfig {

    private final int goldReward;
    private final int stepPenalty;

    private final int bombsPerPlayer;
    private final int bombRange;
    private final int bombCountDown;

    private final int playerLivesAtStart;
    private final int playerCount;

    private final double goldRespawnProbability;

    private final char[][] gameMatrix;

    public BomberManConfig(int maximalStepCountBound,
                           boolean isModelKnown,
                           int goldReward,
                           int stepPenalty,
                           int bombsPerPlayer,
                           int bombRange,
                           int bombCountDown,
                           int playerLivesAtStart,
                           int playerCount,
                           double goldRespawnProbability,
                           BomberManInstance bomberManInstance,
                           PolicyShuffleStrategy policyShuffleStrategy) throws IOException, InvalidInstanceSetupException {
        this(maximalStepCountBound,
            isModelKnown,
            goldReward,
            stepPenalty,
            bombsPerPlayer,
            bombRange,
            bombCountDown,
            playerLivesAtStart,
            playerCount,
            goldRespawnProbability,
            bomberManInstance.getAsPlayground(),
            policyShuffleStrategy
            );
    }

    protected BomberManConfig(int maximalStepCountBound,
                              boolean isModelKnown,
                              int goldReward,
                              int stepPenalty,
                              int bombsPerPlayer,
                              int bombRange,
                              int bombCountDown,
                              int playerLivesAtStart,
                              int playerCount,
                              double goldRespawnProbability,
                              ImmutableTuple<char[][], Integer> gamePlayground,
                              PolicyShuffleStrategy policyShuffleStrategy) {
        super(maximalStepCountBound,
            isModelKnown,
            gamePlayground.getSecond() + 1,
            1,
            List.of(
                new PolicyCategoryInfo(false, RoundBuilder.ENVIRONMENT_CATEGORY_ID, gamePlayground.getSecond() + 1),
                new PolicyCategoryInfo(true, RoundBuilder.ENVIRONMENT_CATEGORY_ID + 1, playerCount)),
            policyShuffleStrategy);

        this.goldReward = goldReward;
        this.stepPenalty = stepPenalty;
        this.bombsPerPlayer = bombsPerPlayer;
        this.bombRange = bombRange;
        this.bombCountDown = bombCountDown;
        this.playerLivesAtStart = playerLivesAtStart;
        this.playerCount = playerCount;
        this.goldRespawnProbability = goldRespawnProbability;
        this.gameMatrix = gamePlayground.getFirst();
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getStepPenalty() {
        return stepPenalty;
    }

    public int getBombsPerPlayer() {
        return bombsPerPlayer;
    }

    public int getBombRange() {
        return bombRange;
    }

    public int getBombCountDown() {
        return bombCountDown;
    }

    public int getPlayerLivesAtStart() {
        return playerLivesAtStart;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public double getGoldRespawnProbability() {
        return goldRespawnProbability;
    }

    public char[][] getGameMatrix() {
        return gameMatrix;
    }

    @Override
    public String toLog() {
        return null;
    }

    @Override
    public String toFile() {
        return null;
    }
}
