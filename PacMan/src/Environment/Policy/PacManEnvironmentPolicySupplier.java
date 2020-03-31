package Environment.Policy;

import Environment.Action.PacManAction;
import Environment.State.PacManState;
import vahy.api.policy.Policy;
import vahy.api.policy.PolicyMode;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.policy.AbstractRandomizedPolicySupplier;
import vahy.paperGenerics.policy.PaperPolicyRecord;

import java.util.SplittableRandom;

public class PacManEnvironmentPolicySupplier extends AbstractRandomizedPolicySupplier<PacManAction, DoubleVector, PacManEnvironmentProbabilities, PacManState, PaperPolicyRecord> {

    public PacManEnvironmentPolicySupplier(SplittableRandom random) {
        super(random);
    }

    @Override
    protected Policy<PacManAction, DoubleVector, PacManEnvironmentProbabilities, PacManState, PaperPolicyRecord> initializePolicy_inner(PacManState initialState, PolicyMode policyMode, SplittableRandom random) {
        return new PacManEnvironmentPolicy(random);
    }
}
