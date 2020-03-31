package Environment.Policy;

import Environment.Action.PacManAction;
import vahy.api.model.observation.FixedModelObservation;
import vahy.utils.ImmutableTuple;

import java.util.List;

public class PacManEnvironmentProbabilities implements FixedModelObservation<PacManAction> {

    private final ImmutableTuple<List<PacManAction>, List<Double>> probabilities;

    public PacManEnvironmentProbabilities(ImmutableTuple<List<PacManAction>, List<Double>> probabilities) {
        this.probabilities = probabilities;
    }

    @Override
    public ImmutableTuple<List<PacManAction>, List<Double>> getProbabilities() {
        return probabilities;
    }
}
