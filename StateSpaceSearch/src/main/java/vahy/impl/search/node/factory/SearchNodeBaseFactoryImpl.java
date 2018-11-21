package vahy.impl.search.node.factory;

import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.StateRewardReturn;
import vahy.api.model.observation.Observation;
import vahy.api.model.reward.Reward;
import vahy.api.search.node.SearchNode;
import vahy.api.search.node.SearchNodeMetadata;
import vahy.api.search.node.factory.SearchNodeFactory;
import vahy.api.search.node.factory.SearchNodeMetadataFactory;
import vahy.impl.search.node.SearchNodeImpl;

import java.util.LinkedHashMap;

public class SearchNodeBaseFactoryImpl<
    TAction extends Action,
    TReward extends Reward,
    TObservation extends Observation,
    TSearchNodeMetadata extends SearchNodeMetadata<TReward>,
    TState extends State<TAction, TReward, TObservation, TState>>
    implements SearchNodeFactory<TAction, TReward, TObservation, TSearchNodeMetadata, TState> {

    private final SearchNodeMetadataFactory<TAction, TReward, TObservation, TSearchNodeMetadata, TState> searchNodeMetadataFactory;

    public SearchNodeBaseFactoryImpl(SearchNodeMetadataFactory<TAction, TReward, TObservation, TSearchNodeMetadata, TState> searchNodeMetadataFactory) {
        this.searchNodeMetadataFactory = searchNodeMetadataFactory;
    }

    @Override
    public SearchNode<TAction, TReward, TObservation, TSearchNodeMetadata, TState> createNode(
        StateRewardReturn<TAction, TReward, TObservation, TState> stateRewardReturn,
        SearchNode<TAction, TReward, TObservation, TSearchNodeMetadata, TState> parent,
        TAction action) {
        return new SearchNodeImpl<>(
            stateRewardReturn.getState(),
            searchNodeMetadataFactory.createSearchNodeMetadata(parent, stateRewardReturn),
            new LinkedHashMap<>(),
            parent,
            action);
    }
}
