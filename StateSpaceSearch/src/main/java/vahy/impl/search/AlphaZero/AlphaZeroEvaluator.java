package vahy.impl.search.AlphaZero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.predictor.Predictor;
import vahy.api.predictor.TrainablePredictor;
import vahy.api.search.node.SearchNode;
import vahy.api.search.node.factory.SearchNodeFactory;
import vahy.api.search.nodeEvaluator.NodeEvaluator;
import vahy.impl.model.observation.DoubleVector;
import vahy.utils.EnumUtils;
import vahy.utils.RandomDistributionUtils;

import java.util.Arrays;

public class AlphaZeroEvaluator<
    TAction extends Enum<TAction> & Action,
    TObservation extends DoubleVector,
    TState extends State<TAction, TObservation, TState>>
    implements NodeEvaluator<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> {

    protected static final Logger logger = LoggerFactory.getLogger(AlphaZeroEvaluator.class);
    protected static final boolean TRACE_ENABLED = logger.isTraceEnabled();

    public static final int Q_VALUE_INDEX = 0;

    private final SearchNodeFactory<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> searchNodeFactory;
    private final AlphaZeroNodeMetadataFactory<TAction, TObservation, TState> searchNodeMetadataFactory;
    private final TrainablePredictor predictor;
    private final boolean isModelKnown;
    private Predictor<TState> perfectEnvironmentPredictor;

    public AlphaZeroEvaluator(SearchNodeFactory<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> searchNodeFactory,
                              TrainablePredictor predictor,
                              boolean isModelKnown)
    {
        this.searchNodeFactory = searchNodeFactory;
        this.searchNodeMetadataFactory = (AlphaZeroNodeMetadataFactory<TAction, TObservation, TState>) searchNodeFactory.getSearchNodeMetadataFactory();
        this.predictor = predictor;
        this.isModelKnown = isModelKnown;
    }

    @Override
    public int evaluateNode(SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> selectedNode) {
        if(selectedNode.isFinalNode()) {
            throw new IllegalStateException("Final node cannot be expanded.");
        }
        if(selectedNode.getSearchNodeMetadata().isEvaluated()) {
            throw new IllegalStateException("Node is already evaluated");
        }
        var expandedNodes = 0;
        if(selectedNode.isRoot()) {
            expandedNodes += evaluateNode_inner(selectedNode);
            selectedNode.unmakeLeaf();
        }
        TAction[] allPossibleActions = selectedNode.getAllPossibleActions();
        if(TRACE_ENABLED) {
            logger.trace("Expanding node [{}] with possible actions: [{}] ", selectedNode, Arrays.toString(allPossibleActions));
        }
        var childNodeMap = selectedNode.getChildNodeMap();
        for (TAction nextAction : allPossibleActions) {
            var stateRewardReturn = selectedNode.applyAction(nextAction);
            var childNode = searchNodeFactory.createNode(stateRewardReturn, selectedNode, nextAction);
            childNodeMap.put(nextAction, childNode);
            expandedNodes += evaluateNode_inner(childNode);
        }
        if(!selectedNode.isFinalNode()) {
            selectedNode.unmakeLeaf();
        }
        return expandedNodes;
    }

    private int evaluateNode_inner(SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> selectedNode) {

        var prediction = predictor.apply(selectedNode.getStateWrapper().getObservation());
        var entityInGameCount = selectedNode.getStateWrapper().getTotalEntityCount();

        var metadata = selectedNode.getSearchNodeMetadata();
        var expectedReward = metadata.getExpectedReward();

        System.arraycopy(prediction, 0, expectedReward, 0, expectedReward.length);

        if(!selectedNode.isFinalNode()) {
            var totalActionCount = searchNodeMetadataFactory.getTotalActionCount();
            double[] distribution = new double[totalActionCount];
            TAction[] allPossibleActions = selectedNode.getAllPossibleActions();
            if(selectedNode.getStateWrapper().isEnvironmentEntityOnTurn() && isModelKnown) {
                useKnownModelPredictor(selectedNode, distribution, allPossibleActions);
            } else {
                System.arraycopy(prediction, entityInGameCount, distribution, 0, distribution.length);
                boolean[] mask = EnumUtils.createMask(allPossibleActions, totalActionCount);
                RandomDistributionUtils.applyMaskToRandomDistribution(distribution, mask);
            }

            var childPriorProbabilities = metadata.getChildPriorProbabilities();
            for (TAction key : allPossibleActions) {
                childPriorProbabilities.put(key, distribution[key.ordinal()]);
            }
        }
        return 1;
    }

    private void useKnownModelPredictor(SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> selectedNode, double[] distribution, TAction[] allPossibleActions) {
        if(perfectEnvironmentPredictor == null) {
            perfectEnvironmentPredictor = selectedNode.getStateWrapper().getKnownModelWithPerfectObservationPredictor();
        }
        var modelPrediction = perfectEnvironmentPredictor.apply(selectedNode.getStateWrapper().getWrappedState());

        if(modelPrediction.length != allPossibleActions.length) {
            throw new IllegalStateException("Inconsistency between array lengths");
        }
        for (int i = 0; i < modelPrediction.length; i++) {
            distribution[allPossibleActions[i].ordinal()] = modelPrediction[i];
        }
    }
}
