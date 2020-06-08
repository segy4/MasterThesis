package vahy.impl.search.AlphaGo;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.search.node.SearchNode;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.search.nodeSelector.AbstractSamplingNodeSelector;

import java.util.SplittableRandom;

public class AlphaGoNodeSelector<TAction extends Enum<TAction> & Action, TObservation extends DoubleVector, TState extends State<TAction, TObservation, TState>>
    extends AbstractSamplingNodeSelector<TAction, TObservation, AlphaGoNodeMetadata<TAction>, TState> {

    private final double cpuctParameter;
    private final double[] valueArray;
    private final int[] indexArray;

    public AlphaGoNodeSelector(SplittableRandom random, double cpuctParameter, int maxBranchingCount) {
        super(random);
        this.cpuctParameter = cpuctParameter;
        this.indexArray = new int[maxBranchingCount];
        this.valueArray = new double[maxBranchingCount];
    }


    private TAction getBestAction_inner(SearchNode<TAction, TObservation, AlphaGoNodeMetadata<TAction>, TState> node) {
        TAction[] possibleActions = node.getAllPossibleActions();
        var searchNodeMap = node.getChildNodeMap();
        var inGameEntityIdOnTurn = node.getStateWrapper().getInGameEntityOnTurnId();

        double max = -Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < possibleActions.length; i++) {
            var metadata = searchNodeMap.get(possibleActions[i]).getSearchNodeMetadata();
            double value = metadata.getExpectedReward()[inGameEntityIdOnTurn] + metadata.getGainedReward()[inGameEntityIdOnTurn];
            if(max < value) {
                max = value;
            }
            if(min > value) {
                min = value;
            }
            valueArray[i] = value;
        }

        int maxIndex = -1;
        double maxValue = -Double.MAX_VALUE;

        int totalNodeVisitCount = node.getSearchNodeMetadata().getVisitCounter();
        int maxIndexCount = 0;
        if(max != min) {
            var norm = max - min;
            for (int i = 0; i < possibleActions.length; i++) {
                var metadata = searchNodeMap.get(possibleActions[i]).getSearchNodeMetadata();
                var vValue = (valueArray[i] - min) / norm;
                var uValue = cpuctParameter * metadata.getPriorProbability() * Math.sqrt(totalNodeVisitCount / (1.0 + metadata.getVisitCounter()));
                var quValue = vValue + uValue;
//                logger.trace("Index: [{}], qValue[{}]", i, quValue);
                if(quValue > maxValue) {
                    maxIndex = i;
                    maxValue = quValue;
                    maxIndexCount = 0;
                } else if(quValue == maxValue) {
                    if(maxIndexCount == 0) {
                        indexArray[0] = maxIndex;
                        indexArray[1] = i;
                        maxIndexCount = 2;
                    } else {
                        indexArray[maxIndexCount] = i;
                        maxIndexCount++;
                    }
                }
            }
        } else {
            for (int i = 0; i < possibleActions.length; i++) {
                var metadata = searchNodeMap.get(possibleActions[i]).getSearchNodeMetadata();
                var quValue = cpuctParameter * metadata.getPriorProbability() *  Math.sqrt(totalNodeVisitCount / (1.0 + metadata.getVisitCounter()));
//                logger.trace("Index: [{}], qValue[{}]", i, quValue);
                if(quValue > maxValue) {
                    maxIndex = i;
                    maxValue = quValue;
                    maxIndexCount = 0;
                } else if(quValue == maxValue) {
                    if(maxIndexCount == 0) {
                        indexArray[0] = maxIndex;
                        indexArray[1] = i;
                        maxIndexCount = 2;
                    } else {
                        indexArray[maxIndexCount] = i;
                        maxIndexCount++;
                    }
                }
            }
        }
        if(maxIndexCount == 0) {
            return possibleActions[maxIndex];
        } else {
            return possibleActions[indexArray[random.nextInt(maxIndexCount)]];
        }
    }

    private TAction getBestAction(SearchNode<TAction, TObservation, AlphaGoNodeMetadata<TAction>, TState> node) {
        if(node.getStateWrapper().isEnvironmentEntityOnTurn()) {
            return sampleAction(node);
        } else {
            return getBestAction_inner(node);
        }
    }

    @Override
    public SearchNode<TAction, TObservation, AlphaGoNodeMetadata<TAction>, TState> selectNextNode(SearchNode<TAction, TObservation, AlphaGoNodeMetadata<TAction>, TState> root) {
        var node = root;
        while(!node.isLeaf()) {
            node = node.getChildNodeMap().get(getBestAction(node));
        }
        return node;
    }
}
