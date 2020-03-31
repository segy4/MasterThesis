package Environment.Policy;

import Environment.Action.PacManAction;
import Environment.State.PacManState;
import vahy.api.policy.Policy;
import vahy.api.policy.PolicyMode;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.policy.AbstractRandomizedPolicySupplier;
import vahy.paperGenerics.policy.PaperPolicyRecord;

import java.util.SplittableRandom;

public class PacManPolicySupplier extends AbstractRandomizedPolicySupplier<PacManAction, DoubleVector, PacManEnvironmentProbs, PacManState, PaperPolicyRecord> {

    public PacManPolicySupplier(SplittableRandom random) {
        super(random);
    }

    @Override
    protected Policy<PacManAction, DoubleVector, PacManEnvironmentProbs, PacManState, PaperPolicyRecord> initializePolicy_inner(PacManState initialState, PolicyMode policyMode, SplittableRandom random) {
        return new PacManEnvironmentPolicy(random);
    }
}
