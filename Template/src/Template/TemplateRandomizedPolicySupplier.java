package Template;

import vahy.api.policy.Policy;
import vahy.api.policy.PolicyMode;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.policy.AbstractRandomizedPolicySupplier;
import vahy.paperGenerics.policy.PaperPolicyRecord;

import java.util.SplittableRandom;

/**
 * Class that supplies Randomized policies for algorithm.
 * You can make this class supply specific policies based on actual state, policy mode and random.
 * Random is forced into this class for purposes of the algorithm.
 */
public class TemplateRandomizedPolicySupplier extends AbstractRandomizedPolicySupplier<TemplateAction, DoubleVector, TemplateEnvironmentObservation, TemplateState, PaperPolicyRecord> {

    public TemplateRandomizedPolicySupplier(SplittableRandom random) {
        super(random);
    }

    @Override
    protected Policy<TemplateAction, DoubleVector, TemplateEnvironmentObservation, TemplateState, PaperPolicyRecord> initializePolicy_inner(TemplateState initialState, PolicyMode policyMode, SplittableRandom random) {
        return new TemplateRandomizedPolicy(random);
    }
}
