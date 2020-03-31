package Template;


import vahy.impl.model.observation.DoubleVector;
import vahy.impl.policy.RandomizedPolicy;
import vahy.paperGenerics.policy.PaperPolicy;
import vahy.paperGenerics.policy.PaperPolicyRecord;

import java.util.List;
import java.util.SplittableRandom;

/**
 * Class that holds OPPONENT policy for choosing OPPONENT actions
 * This class will be always created through supplier so you can add as many constructors as you need.
 * The only thing that needs to be present is the random for purposes of the algorithm.
 */
public class TemplateRandomizedPolicy extends RandomizedPolicy<TemplateAction, DoubleVector, TemplateEnvironmentObservation, TemplateState, PaperPolicyRecord>
        implements PaperPolicy<TemplateAction, DoubleVector, TemplateEnvironmentObservation, TemplateState> {

    protected TemplateRandomizedPolicy(SplittableRandom random) {
        super(random);
    }

    /**
     * Method that creates OPPONENT actions probability distribution for any given state
     * @param gameState Instance of State that we want OPPONENT actions probability distribution for
     * @return OPPONENT actions probability distribution from this given state
     */
    @Override
    public double[] getPriorActionProbabilityDistribution(TemplateState gameState) {
        return new double[0];
    }

    /**
     * Method that calculates estimated reward from a State
     * @param gameState Instance of State that we want estimated reward from
     * @return estimated reward from given state
     */
    @Override
    public double getEstimatedReward(TemplateState gameState) {
        return 0;
    }

    /**
     * Method that calculates estimated risk from a State
     * @param gameState Instance of State that we want estimated risk from
     * @return estimated risk from given state
     */
    @Override
    public double getEstimatedRisk(TemplateState gameState) {
        return 0;
    }

    /**
     * This classes own parameter that holds amount of allowed risk
     * 0 means no risk ceiling.
     * @return risk allowed
     */
    @Override
    public double getInnerRiskAllowed() {
        return 0;
    }

    /**
     * Method that creates OPPONENT actions probability distribution for any given state
     * @param gameState Instance of State that we want OPPONENT actions probability distribution for
     * @return OPPONENT actions probability distribution from this given state
     */
    @Override
    public double[] getActionProbabilityDistribution(TemplateState gameState) {
        return new double[0];
    }

    /**
     * Method that chooses one action from all possible OPPONENT actions
     * @param gameState state that we want an action for
     * @return action that was selected
     */
    @Override
    public TemplateAction getDiscreteAction(TemplateState gameState) {
        return null;
    }

    /**
     * Method that updates inner state based on played OPPONENT actions
     * @param opponentActionList list of prior actions
     */
    @Override
    public void updateStateOnPlayedActions(List<TemplateAction> opponentActionList) {

    }

    /**
     * Method to log this class
     * @param gameState state that we want to log
     * @return PolicyRecord based on given state
     */
    @Override
    public PaperPolicyRecord getPolicyRecord(TemplateState gameState) {
        return null;
    }
}
