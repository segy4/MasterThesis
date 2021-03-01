package vahy.impl.policy.alphazero;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.StateWrapper;
import vahy.api.model.observation.Observation;
import vahy.api.policy.PlayingDistribution;
import vahy.api.policy.PolicyRecordBase;
import vahy.api.search.node.SearchNode;
import vahy.api.search.tree.treeUpdateCondition.TreeUpdateCondition;
import vahy.impl.policy.AbstractTreeSearchPolicy;
import vahy.impl.search.tree.SearchTreeImpl;

import java.util.SplittableRandom;

public class AlphaZeroPolicy<
    TAction extends Enum<TAction> & Action,
    TObservation extends Observation<TObservation>,
    TState extends State<TAction, TObservation, TState>>
    extends AbstractTreeSearchPolicy<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> {

    public AlphaZeroPolicy(int policyId, SplittableRandom random, double explorationConstant, TreeUpdateCondition treeUpdateCondition, SearchTreeImpl<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> searchTree) {
        super(policyId, random, explorationConstant, treeUpdateCondition, searchTree);
    }

    @Override
    protected PlayingDistribution<TAction> inferenceBranch(StateWrapper<TAction, TObservation, TState> gameState) {
        return getBestAction(gameState, searchTree.getRoot());
    }

    @Override
    protected PlayingDistribution<TAction> explorationBranch(StateWrapper<TAction, TObservation, TState> gameState) {
        return getExploringAction(gameState, searchTree.getRoot());
    }

    private PlayingDistribution<TAction> getExploringAction(StateWrapper<TAction, TObservation, TState> gameState, SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> root) {
        var inGameId = gameState.getInGameEntityId();
        TAction[] actions = gameState.getAllPossibleActions();
        var actionIndex = random.nextInt(actions.length);
        var action = actions[actionIndex];
        var applied = root.getChildNodeMap().get(action);
        var metadata = applied.getSearchNodeMetadata();
        var value = metadata.getGainedReward()[inGameId] + metadata.getExpectedReward()[inGameId];
        return new PlayingDistribution<>(action, value, innerActionProbabilityDistribution(action));
    }

    private PlayingDistribution<TAction> getBestAction(StateWrapper<TAction, TObservation, TState> gameState, SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> root) {
        var inGameId = gameState.getInGameEntityId();
        var childMap = root.getChildNodeMap();

        var max = -Double.MAX_VALUE;
        TAction bestAction = null;
        for (var entry : childMap.entrySet()) {
            var expectedReward = entry.getValue().getSearchNodeMetadata().getExpectedReward()[inGameId];
            var gainedReward = entry.getValue().getSearchNodeMetadata().getGainedReward()[inGameId];
            var actionValue = expectedReward + gainedReward;
            if(actionValue > max) {
                max = actionValue;
                bestAction = entry.getKey();
            }
        }
        return new PlayingDistribution<TAction>(bestAction, max, innerActionProbabilityDistribution(bestAction));
//        return root.getChildNodeStream().max(Comparator.comparing(x -> x.getSearchNodeMetadata().getExpectedReward()[gameState.getInGameEntityIdWrapper()])).orElseThrow().getAppliedAction();
    }
//
//    private TAction getBestAction(StateWrapper<TAction, TObservation, TState> gameState, SearchNode<TAction, TObservation, AlphaZeroNodeMetadata<TAction>, TState> root) {
//        return root.getChildNodeStream().max(Comparator.comparing(x -> x.getSearchNodeMetadata().getExpectedReward()[gameState.getInGameEntityIdWrapper()])).orElseThrow().getAppliedAction();
//    }


    @Override
    public PolicyRecordBase getPolicyRecord(StateWrapper<TAction, TObservation, TState> gameState) {
        if(DEBUG_ENABLED) {
            checkStateRoot(gameState);
        }
        return new PolicyRecordBase(playingDistribution.getDistribution(), playingDistribution.getExpectedReward());
    }

    private double[] innerActionProbabilityDistribution(TAction selectedAction) {
        var distribution = new double[countOfAllActions];
        var actionIndex = selectedAction.ordinal();
        distribution[actionIndex] = 1.0;
        return distribution;
    }
}
