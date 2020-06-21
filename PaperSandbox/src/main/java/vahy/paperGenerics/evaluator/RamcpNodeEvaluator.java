package vahy.paperGenerics.evaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.paperGenerics.PaperStateWrapper;
import vahy.api.model.Action;
import vahy.api.model.observation.Observation;
import vahy.api.predictor.Predictor;
import vahy.api.search.node.SearchNode;
import vahy.api.search.node.factory.SearchNodeFactory;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.model.reward.DoubleScalarRewardAggregator;
import vahy.paperGenerics.PaperState;
import vahy.paperGenerics.metadata.PaperMetadata;
import vahy.utils.ImmutableTriple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class RamcpNodeEvaluator<
    TAction extends Enum<TAction> & Action,
    TOpponentObservation extends Observation,
    TSearchNodeMetadata extends PaperMetadata<TAction>,
    TState extends PaperState<TAction, DoubleVector, TState>>
    extends MonteCarloNodeEvaluator<TAction, TSearchNodeMetadata, TState> {

    private final Logger logger = LoggerFactory.getLogger(RamcpNodeEvaluator.class.getName());

    @Deprecated
    public RamcpNodeEvaluator(SearchNodeFactory<TAction, DoubleVector, TSearchNodeMetadata, TState> searchNodeFactory,
                              Predictor<TState> knownModel,
                              TAction[] allPlayerActions,
                              TAction[] allOpponentActions,
                              SplittableRandom random,
                              double discountFactor) {
        super(searchNodeFactory, knownModel, allPlayerActions, allOpponentActions, random, discountFactor);
        throw new UnsupportedOperationException("Ok so ... class [" + RamcpNodeEvaluator.class + "] is deprecated. Needs to be fixed. Issue: how to sample unknown opponent ");
    }

    @Override
    protected ImmutableTriple<Double, Boolean, Integer> runRandomWalkSimulation(SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> node) {
        var parent = node;

        List<Double> rewardList = new ArrayList<>();
        var nodeCounter = 0;
        List<SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState>> nodeList = new ArrayList<>();
        var wrappedState = node.getStateWrapper();
        while (!parent.isFinalNode()) {

            initializeChildNodePrioriProbabilityMap(parent);

            var action = getNextAction(wrappedState);
            var stateRewardReturn = wrappedState.applyAction(action);
            var nextNode = searchNodeFactory.createNode(stateRewardReturn, parent, action);
            nodeList.add(nextNode);
            rewardList.add(stateRewardReturn.getReward());

            wrappedState = nextNode.getStateWrapper();
            parent = nextNode;
            nodeCounter++;
        }
        if(!((PaperStateWrapper<TAction, DoubleVector, TState>)parent.getStateWrapper()).isRiskHit()) {
            nodeCounter += createSuccessfulBranch(node, nodeList);
        }  else {
            node.getChildNodeMap().clear();
            node.getSearchNodeMetadata().getChildPriorProbabilities().clear();
        }
        return new ImmutableTriple<>(DoubleScalarRewardAggregator.aggregateDiscount(rewardList, discountFactor), ((PaperStateWrapper<TAction, DoubleVector, TState>)wrappedState).isRiskHit(), nodeCounter);
    }

    private void initializeChildNodePrioriProbabilityMap(SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> node) {
        var allPossibleActions = node.getStateWrapper().getAllPossibleActions();
        var childNodePriorProbabilitiesMap = node.getSearchNodeMetadata().getChildPriorProbabilities();

        if(node.isPlayerTurn()) {
            for (TAction possibleAction : allPossibleActions) {
                childNodePriorProbabilitiesMap.put(possibleAction, priorProbabilities[0]);
            }
        } else {
//            evaluateOpponentNode(node, childNodePriorProbabilitiesMap, null);
        }
    }

    private int createSuccessfulBranch(SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> node,
                                       List<SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState>> nodeList) {
        if(node.isFinalNode()) {
            return 0;
        }
        var nodeCounter = 0;
        var parent = node;
        for (SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> entryNode : nodeList) {
            nodeCounter += addNextNode(parent, entryNode, entryNode.getAppliedAction());
            parent = entryNode;
        }

        var reward = parent.getSearchNodeMetadata().getCumulativeReward();
        var risk = ((PaperStateWrapper<TAction, DoubleVector, TState>)parent.getStateWrapper()).isRiskHit() ? 1.0 : 0.0;
        parent = parent.getParent();
        if(!parent.isRoot()) {
            while(!parent.equals(node)) {
                updateNode(parent, reward, risk);
                parent = parent.getParent();
            }
        }
        return nodeCounter;
    }

    private int addNextNode(SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> parent,
                             SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> child,
                             TAction action) {
        TAction[] allPossibleActions = parent.getAllPossibleActions();
        Map<TAction, SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState>> childNodeMap = parent.getChildNodeMap();
        int nodeCounter = 0;
        for (TAction nextAction : allPossibleActions) {
            childNodeMap.put(nextAction, action.equals(nextAction) ? child : createSideNode(parent, nextAction));
            nodeCounter++;
        }
        return nodeCounter;
    }

    private void updateNode(SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> updatedNode,
                            double estimatedLeafReward,
                            double estimatedRisk) {
        PaperMetadata<TAction> searchNodeMetadata = updatedNode.getSearchNodeMetadata();
        searchNodeMetadata.increaseVisitCounter();

        if(updatedNode.isFinalNode()) {
            if(searchNodeMetadata.getVisitCounter() == 1) {
                searchNodeMetadata.setSumOfTotalEstimations(0.0);
                searchNodeMetadata.setSumOfRisk(estimatedRisk);
            }
        } else {
            if(searchNodeMetadata.getVisitCounter() == 1) {
                searchNodeMetadata.setSumOfTotalEstimations(searchNodeMetadata.getPredictedReward());
                searchNodeMetadata.setSumOfRisk(estimatedRisk);
            } else {
                searchNodeMetadata.setSumOfTotalEstimations(searchNodeMetadata.getSumOfTotalEstimations() + (estimatedLeafReward - searchNodeMetadata.getCumulativeReward()));
                searchNodeMetadata.setSumOfRisk(searchNodeMetadata.getSumOfRisk() + estimatedRisk);
            }
            searchNodeMetadata.setExpectedReward(searchNodeMetadata.getSumOfTotalEstimations() / searchNodeMetadata.getVisitCounter());
            searchNodeMetadata.setPredictedRisk(searchNodeMetadata.getSumOfRisk() / searchNodeMetadata.getVisitCounter());
        }
    }

    private SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> createSideNode(
        SearchNode<TAction, DoubleVector, TSearchNodeMetadata, TState> parent,
        TAction nextAction) {
        var stateRewardReturn = parent.applyAction(nextAction);
        var childNode = searchNodeFactory.createNode(stateRewardReturn, parent, nextAction);
        var searchNodeMetadata = childNode.getSearchNodeMetadata();

        searchNodeMetadata.setPredictedReward(0.0);
        searchNodeMetadata.setExpectedReward(0.0);
        searchNodeMetadata.setSumOfRisk(1.0);
        searchNodeMetadata.setPredictedRisk(1.0);
        searchNodeMetadata.setSumOfTotalEstimations(0.0);

        initializeChildNodePrioriProbabilityMap(childNode);

        return childNode;
    }


}
