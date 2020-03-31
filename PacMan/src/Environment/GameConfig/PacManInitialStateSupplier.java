package Environment.GameConfig;

import Environment.Action.PacManAction;
import Environment.State.PacManState;
import Environment.State.PacManStaticPart;
import Environment.Policy.PacManEnvironmentProbs;
import vahy.impl.episode.AbstractInitialStateSupplier;
import vahy.impl.model.observation.DoubleVector;

import java.util.SplittableRandom;

public class PacManInitialStateSupplier extends AbstractInitialStateSupplier<PacManGameConfig, PacManAction, DoubleVector, PacManEnvironmentProbs, PacManState> {


    public PacManInitialStateSupplier(PacManGameConfig problemConfig, SplittableRandom random) {
        super(problemConfig, random);
    }

    @Override
    protected PacManState createState_inner(PacManGameConfig problemConfig, SplittableRandom random) {
        return new PacManState(true,
                false,
                problemConfig.getPlayerPosition(),
                problemConfig.getPlayerPosition(),
                problemConfig.getEnemyPositions(),
                0,
                problemConfig.getGameBoard(),
                new PacManStaticPart(problemConfig.getBigBallReward(),
                        problemConfig.getSmallBallReward(),
                        problemConfig.getStepPenalty(),
                        problemConfig.getTrapChance(),
                        problemConfig.getOppositeMoveChance(),
                        problemConfig.getRandomMoveChance(),
                        problemConfig.getEnemyType(),
                        random));
    }
}
