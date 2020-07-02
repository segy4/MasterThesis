package vahy.impl.policy.alphazero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.search.node.SearchNode;
import vahy.api.search.update.TreeUpdater;
import vahy.impl.model.reward.DoubleVectorRewardAggregator;

public class AlphaZeroTreeUpdater<
    TAction extends Enum<TAction> & Action,
    TObservation extends Observation,
    TState extends State<TAction, TObservation, TState>>
    implements TreeUpdater<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> {

    private static final Logger logger = LoggerFactory.getLogger(AlphaZeroTreeUpdater.class);
    public static final boolean TRACE_ENABLED = logger.isTraceEnabled();

    @Override
    public void updateTree(SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> expandedNode) {
        int i = 0;
        var stateWrapper = expandedNode.getStateWrapper();
        var nodeMetadata = expandedNode.getSearchNodeMetadata();
        var cumulativeReward = nodeMetadata.getCumulativeReward();
        double[] estimatedLeafReward = stateWrapper.isFinalState() ?
            DoubleVectorRewardAggregator.aggregate(DoubleVectorRewardAggregator.emptyReward(nodeMetadata.getCumulativeReward().length), cumulativeReward) :
            DoubleVectorRewardAggregator.aggregate(nodeMetadata.getExpectedReward(), cumulativeReward);

        while (!expandedNode.isRoot()) {
            updateNode(expandedNode, estimatedLeafReward);
            expandedNode = expandedNode.getParent();
            i++;
        }
        updateNode(expandedNode, estimatedLeafReward);
        if(TRACE_ENABLED) {
            logger.trace("Traversing updated traversed [{}] tree levels", i);
        }
    }

    private void updateNode(SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> expandedNode, double[] estimatedLeafReward) {
        AlphaZeroNodeMetadata<TAction> searchNodeMetadata = expandedNode.getSearchNodeMetadata();
        searchNodeMetadata.increaseVisitCounter();
        var totalEstimations = searchNodeMetadata.getSumOfTotalEstimations();
        var cumulativeRewards = searchNodeMetadata.getCumulativeReward();
        if(searchNodeMetadata.getVisitCounter() == 1) {
            System.arraycopy(estimatedLeafReward, 0, totalEstimations, 0, estimatedLeafReward.length);
        } else {
            for (int i = 0; i < totalEstimations.length; i++) {
                totalEstimations[i] += estimatedLeafReward[i];
//                totalEstimations[i] += estimatedLeafReward[i] - cumulativeRewards[i]; // TODO: look at this time invariance
            }
        }
        var expectedRewards = searchNodeMetadata.getExpectedReward();
        var visitCounter = searchNodeMetadata.getVisitCounter();
        for (int i = 0; i < expectedRewards.length; i++) {
            expectedRewards[i] = totalEstimations[i] / visitCounter;
        }
    }
}
