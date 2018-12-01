package vahy.impl.search.nodeSelector;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.model.reward.Reward;
import vahy.api.search.node.SearchNode;
import vahy.api.search.node.SearchNodeMetadata;
import vahy.api.search.nodeSelector.NodeSelector;

public abstract class AbstractTreeBasedNodeSelector<
    TAction extends Action,
    TReward extends Reward,
    TObservation extends Observation,
    TSearchNodeMetadata extends SearchNodeMetadata<TReward>,
    TState extends State<TAction, TReward, TObservation, TState>>
    implements NodeSelector<TAction, TReward, TObservation, TSearchNodeMetadata, TState> {

    protected SearchNode<TAction, TReward, TObservation, TSearchNodeMetadata, TState> root;

    protected abstract TAction getBestAction(SearchNode<TAction, TReward, TObservation, TSearchNodeMetadata, TState> node);

    @Override
    public void setNewRoot(SearchNode<TAction, TReward, TObservation, TSearchNodeMetadata, TState> root) {
        this.root = root;
    }

    protected void checkRoot() {
        if(root == null) {
            throw new IllegalStateException("Root was not initialized");
        }
    }

    public SearchNode<TAction, TReward, TObservation, TSearchNodeMetadata, TState> selectNextNode() {
        checkRoot();
        SearchNode<TAction, TReward, TObservation, TSearchNodeMetadata, TState> node = root;
        while(!node.isLeaf()) {
            TAction bestAction = getBestAction(node);
            node = node.getChildNodeMap().get(bestAction);
        }
        return node;
    }
}
