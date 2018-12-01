package vahy.paperGenerics;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.search.node.SearchNode;
import vahy.impl.model.reward.DoubleReward;
import vahy.impl.search.nodeSelector.AbstractTreeBasedNodeSelector;
import vahy.utils.ImmutableTuple;
import vahy.utils.StreamUtils;

import java.util.Comparator;
import java.util.OptionalDouble;
import java.util.SplittableRandom;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public class PaperNodeSelector<
    TAction extends Action,
    TReward extends DoubleReward,
    TObservation extends Observation,
    TState extends State<TAction, TReward, TObservation, TState>>
    extends AbstractTreeBasedNodeSelector<TAction, TReward, TObservation, PaperMetadata<TAction, TReward>, TState> {

    public static final double TOLERANCE = Math.pow(10, -15);

    private final double cpuctParameter;
    private final SplittableRandom random;

    public PaperNodeSelector(double cpuctParameter, SplittableRandom random) {
        this.cpuctParameter = cpuctParameter;
        this.random = random;
    }

    private double getExtremeElement(SearchNode<TAction, TReward, TObservation, PaperMetadata<TAction, TReward>, TState> node,
                                     Function<DoubleStream, OptionalDouble> function,
                                     String nonExistingElementMessage) {
        return function.apply(node
            .getChildNodeStream()
            .mapToDouble(x -> x.getSearchNodeMetadata().getExpectedReward().getValue())
        ).orElseThrow(() -> new IllegalStateException(nonExistingElementMessage));
    }

    @Override
    protected TAction getBestAction(SearchNode<TAction, TReward, TObservation, PaperMetadata<TAction, TReward>, TState> node) {
        int totalNodeVisitCount = node.getSearchNodeMetadata().getVisitCounter();

        final double max = getExtremeElement(node, DoubleStream::max, "Maximum Does not exists");
        final double min = getExtremeElement(node, DoubleStream::min, "Minimum Does not exists");

        TAction bestAction = node
            .getChildNodeStream()
            .map(x -> {
                TAction action = x.getAppliedAction();
                double uValue = calculateUValue(x.getSearchNodeMetadata().getPriorProbability(), x.getSearchNodeMetadata().getVisitCounter(), totalNodeVisitCount);
                double qValue = x.getSearchNodeMetadata().getExpectedReward().getValue() == 0 ? 0 :
                    (x.getSearchNodeMetadata().getExpectedReward().getValue() - min) /
                        (Math.abs(max - min) < TOLERANCE ? max : (max - min));

                return new ImmutableTuple<>(action, qValue + uValue);
            })
            .collect(StreamUtils.toRandomizedMaxCollector(Comparator.comparing(ImmutableTuple::getSecond), random))
            .getFirst();
        return bestAction;
    }

    private double calculateUValue(double priorProbability, int childVisitCount, int nodeTotalVisitCount) {
        return cpuctParameter * priorProbability * Math.sqrt(nodeTotalVisitCount) / (1.0 + childVisitCount);
    }
}
