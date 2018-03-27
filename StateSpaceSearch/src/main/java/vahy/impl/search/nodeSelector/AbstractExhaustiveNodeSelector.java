package vahy.impl.search.nodeSelector;

import vahy.api.model.Action;
import vahy.api.model.Observation;
import vahy.api.model.Reward;
import vahy.api.model.State;
import vahy.api.search.node.SearchNode;
import vahy.api.search.node.SearchNodeMetadata;
import vahy.api.search.nodeSelector.NodeSelector;

import java.util.Collection;
import java.util.LinkedList;

public abstract class AbstractExhaustiveNodeSelector<
        TAction extends Action,
        TReward extends Reward,
        TObservation extends Observation,
        TSearchNodeMetadata extends SearchNodeMetadata,
        TState extends State<TAction, TReward, TObservation>>
        implements NodeSelector<TAction, TReward, TObservation, TSearchNodeMetadata, TState> {

    protected final LinkedList<SearchNode<TAction, TReward, TObservation, TState, TSearchNodeMetadata>> nodeQueue;

    public AbstractExhaustiveNodeSelector() {
        this.nodeQueue = new LinkedList<>(); // using linked implementation here // TODO: generalize
    }

    @Override
    public void addNodes(Collection<SearchNode<TAction, TReward, TObservation, TState, TSearchNodeMetadata>> rootNodes) {
        for (SearchNode<TAction, TReward, TObservation, TState, TSearchNodeMetadata> rootNode : rootNodes) {
            this.addNode(rootNode);
        }
    }

    @Override
    public SearchNode<TAction, TReward, TObservation, TState, TSearchNodeMetadata> selectNextNode() {
        return nodeQueue.removeFirst();
    }

}