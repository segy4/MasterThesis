package Environment.Policy;

import Environment.Action.PacManAction;
import Environment.State.PacManState;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.policy.RandomizedPolicy;
import vahy.paperGenerics.policy.PaperPolicy;
import vahy.paperGenerics.policy.PaperPolicyRecord;
import vahy.utils.ImmutableTuple;
import vahy.utils.RandomDistributionUtils;

import java.util.List;
import java.util.SplittableRandom;

public class PacManEnvironmentPolicy extends RandomizedPolicy<PacManAction, DoubleVector, PacManEnvironmentProbs, PacManState, PaperPolicyRecord> implements PaperPolicy<PacManAction, DoubleVector, PacManEnvironmentProbs, PacManState> {
    protected PacManEnvironmentPolicy(SplittableRandom random) {
        super(random);
    }

    @Override
    public double[] getActionProbabilityDistribution(PacManState gameState) {
        ImmutableTuple<List<PacManAction>, List<Double>> actions = gameState.getOpponentObservation().getProbabilities();
        return actions.getSecond().stream().mapToDouble(value -> value).toArray();
    }

    @Override
    public PacManAction getDiscreteAction(PacManState gameState) {
        ImmutableTuple<List<PacManAction>, List<Double>> actions = gameState.getOpponentObservation().getProbabilities();
        return actions.getFirst().get(RandomDistributionUtils.getRandomIndexFromDistribution(actions.getSecond(), random));
    }

    @Override
    public void updateStateOnPlayedActions(List<PacManAction> opponentActionList) {
        // Do something with previous actions
    }

    @Override
    public PaperPolicyRecord getPolicyRecord(PacManState gameState) {
        double[] probs = this.getActionProbabilityDistribution(gameState);
        return new PaperPolicyRecord(probs, probs, 0.0, 0.0, 0.0, 0);
    }

    @Override
    public double[] getPriorActionProbabilityDistribution(PacManState gameState) {
        return getActionProbabilityDistribution(gameState);
    }

    @Override
    public double getEstimatedReward(PacManState gameState) {
        return 0.0;
    }

    @Override
    public double getEstimatedRisk(PacManState gameState) {
        return 0.0;
    }

    @Override
    public double getInnerRiskAllowed() {
        return 0;
    }
}
