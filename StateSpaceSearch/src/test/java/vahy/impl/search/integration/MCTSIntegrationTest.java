package vahy.impl.search.integration;

import org.testng.annotations.Test;
import vahy.api.model.reward.RewardAggregator;
import vahy.api.search.node.SearchNode;
import vahy.api.search.node.factory.SearchNodeFactory;
import vahy.api.search.node.factory.SearchNodeMetadataFactory;
import vahy.api.search.nodeEvaluator.NodeEvaluator;
import vahy.impl.model.observation.DoubleVectorialObservation;
import vahy.impl.model.reward.DoubleScalarReward;
import vahy.impl.model.reward.DoubleScalarRewardAggregator;
import vahy.impl.search.MCTS.MonteCarloTreeSearchMetadata;
import vahy.impl.search.node.SearchNodeImpl;
import vahy.impl.search.node.factory.SearchNodeBaseFactoryImpl;
import vahy.impl.search.MCTS.MonteCarloTreeSearchMetadataFactory;
import vahy.impl.search.MCTS.MonteCarloEvaluator;
import vahy.impl.search.MCTS.ucb1.Ucb1NodeSelector;
import vahy.impl.search.tree.SearchTreeImpl;
import vahy.impl.search.MCTS.MonteCarloTreeSearchUpdater;
import vahy.testDomain.model.TestAction;
import vahy.testDomain.model.TestState;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.SplittableRandom;

public class MCTSIntegrationTest {

    @Test
    public void testMCTSAlgorithm() {

        SplittableRandom random = new SplittableRandom(0);

        RewardAggregator<DoubleScalarReward> rewardAggregator = new DoubleScalarRewardAggregator();
        SearchNode<TestAction, DoubleScalarReward, DoubleVectorialObservation, MonteCarloTreeSearchMetadata<DoubleScalarReward>, TestState> root = new SearchNodeImpl<>(
            new TestState(Arrays.asList('Z')),
            new MonteCarloTreeSearchMetadata<>(new DoubleScalarReward(0.0), new DoubleScalarReward(0.0), new DoubleScalarReward(0.0)),
            new LinkedHashMap<>()
        );

        SearchNodeMetadataFactory<TestAction, DoubleScalarReward, DoubleVectorialObservation, MonteCarloTreeSearchMetadata<DoubleScalarReward>, TestState> metadataFactory =
            new MonteCarloTreeSearchMetadataFactory<>(rewardAggregator);
        SearchNodeFactory<TestAction, DoubleScalarReward, DoubleVectorialObservation, MonteCarloTreeSearchMetadata<DoubleScalarReward>, TestState> nodeFactory =
            new SearchNodeBaseFactoryImpl<>(metadataFactory);

        NodeEvaluator<TestAction, DoubleScalarReward, DoubleVectorialObservation, MonteCarloTreeSearchMetadata<DoubleScalarReward>, TestState> nodeEvaluator =
            new MonteCarloEvaluator<>(nodeFactory, random, rewardAggregator, 1.0, 1);

        SearchTreeImpl<TestAction, DoubleScalarReward, DoubleVectorialObservation, MonteCarloTreeSearchMetadata<DoubleScalarReward>, TestState> searchTree = new SearchTreeImpl<>(
            root,
            new Ucb1NodeSelector<>(random, 1.0),
            new MonteCarloTreeSearchUpdater<>(),
            nodeEvaluator
        );

        for (int i = 0; i < 100; i++) {
            searchTree.updateTree();
        }

        System.out.println("asdf");


    }

}
