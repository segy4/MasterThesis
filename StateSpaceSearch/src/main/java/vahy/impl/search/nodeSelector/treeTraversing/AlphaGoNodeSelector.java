package vahy.impl.search.nodeSelector.treeTraversing;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.search.node.SearchNode;
import vahy.impl.model.reward.DoubleScalarReward;
import vahy.impl.search.node.nodeMetadata.AlphaGoNodeMetadata;
import vahy.impl.search.nodeSelector.AbstractTreeBasedNodeSelector;
import vahy.utils.StreamUtils;

import java.util.Comparator;
import java.util.SplittableRandom;

public class AlphaGoNodeSelector<
    TAction extends Action,
    TReward extends DoubleScalarReward,
    TObservation extends Observation,
    TState extends State<TAction, TReward, TObservation, TState>>
    extends AbstractTreeBasedNodeSelector<TAction, TReward, TObservation, AlphaGoNodeMetadata<TReward>, TState> {

    private final double cpuctParameter;
    private final SplittableRandom random;

    public AlphaGoNodeSelector(double cpuctParameter, SplittableRandom random) {
        this.cpuctParameter = cpuctParameter;
        this.random = random;
    }

    @Override
    protected TAction getBestAction(SearchNode<TAction, TReward, TObservation, AlphaGoNodeMetadata<TReward>, TState> node) {
        AlphaGoNodeMetadata<TReward> searchNodeMetadata = node.getSearchNodeMetadata();
        int totalNodeVisitCount = searchNodeMetadata.getVisitCounter();
        return node
            .getChildNodeStream()
            .collect(StreamUtils.toRandomizedMaxCollector(
                Comparator.comparing(
                    childNode -> {
                        AlphaGoNodeMetadata<TReward> childMetadata = childNode.getSearchNodeMetadata();
                        return
                            childMetadata.getEstimatedTotalReward().getValue() +
                            calculateUValue(totalNodeVisitCount, childMetadata.getPriorProbability(), childMetadata.getVisitCounter());
                    }), random))
            .getAppliedAction();
    }

    private double calculateUValue(int nodeTotalVisitCount, double priorActionProbability, int childTotalVisitCount) {
//        return cpuctParameter * priorActionProbability * Math.sqrt(nodeTotalVisitCount) / (1.0 + childTotalVisitCount);
        return cpuctParameter * priorActionProbability * (Math.sqrt(nodeTotalVisitCount) / childTotalVisitCount);
    }
}
