package Mains.Tests;

import Environment.GameConfig.PacManGameConfig;
import Environment.GameConfig.PacManGameConfigBuilder;
import Environment.State.PacManEnemyType;
import Mains.Benchmarks.PacManMain;
import Mains.Instances.PacManGameInstance;
import vahy.api.experiment.SystemConfig;
import vahy.api.learning.ApproximatorType;
import vahy.api.learning.dataAggregator.DataAggregationAlgorithm;
import vahy.config.AlgorithmConfigBuilder;
import vahy.config.EvaluatorType;
import vahy.config.PaperAlgorithmConfig;
import vahy.config.SelectorType;
import vahy.impl.search.tree.treeUpdateCondition.FixedUpdateCountTreeConditionFactory;
import vahy.paperGenerics.policy.flowOptimizer.FlowOptimizerType;
import vahy.paperGenerics.policy.riskSubtree.SubTreeRiskCalculatorType;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.ExplorationExistingFlowStrategy;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.ExplorationNonExistingFlowStrategy;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.InferenceExistingFlowStrategy;
import vahy.paperGenerics.policy.riskSubtree.strategiesProvider.InferenceNonExistingFlowStrategy;

import java.util.function.Supplier;

public class PacManTest00 extends PacManMain {

    public static void main(String[] args) {
        PacManTest00 test = new PacManTest00();
        test.run();
    }

    @Override
    public PaperAlgorithmConfig createAlgorithmConfig() {
        int batchSize = 100;
        return new AlgorithmConfigBuilder()
                //MCTS
                .cpuctParameter(3)
                .treeUpdateConditionFactory(new FixedUpdateCountTreeConditionFactory(100))
                .batchEpisodeCount(100)
                .stageCount(100)
                .trainerAlgorithm(DataAggregationAlgorithm.EVERY_VISIT_MC)
                .approximatorType(ApproximatorType.HASHMAP_LR)
                .learningRate(0.1)
                .evaluatorType(EvaluatorType.RALF)
                .selectorType(SelectorType.UCB)
                .globalRiskAllowed(0.0)
                .explorationConstantSupplier(new Supplier<Double>() {
                    private int callCount = 0;
                    @Override
                    public Double get() {
                        callCount++;
                        double x = Math.exp(-callCount / 10000.0) / 2;
                        if(callCount % batchSize == 0) {
                            logger.info("Exploration constant: [{}] in call: [{}]", x, callCount);
                        }
                        return x;
//                    return 1.0;
                    }
                })
                .temperatureSupplier(new Supplier<Double>() {
                    private int callCount = 0;
                    @Override
                    public Double get() {
                        callCount++;
                        double x = Math.exp(-callCount / 20000.0) * 10;
                        if(callCount % batchSize == 0) {
                            logger.info("Temperature constant: [{}] in call: [{}]", x, callCount);
                        }
                        return x;
//                    return 1.5;
                    }
                })
                .riskSupplier(() -> 0.0)
                .setInferenceExistingFlowStrategy(InferenceExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW)
                .setInferenceNonExistingFlowStrategy(InferenceNonExistingFlowStrategy.MAX_UCB_VISIT)
                .setExplorationExistingFlowStrategy(ExplorationExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW_BOLTZMANN_NOISE)
                .setExplorationNonExistingFlowStrategy(ExplorationNonExistingFlowStrategy.SAMPLE_UCB_VISIT)
                .setFlowOptimizerType(FlowOptimizerType.HARD_HARD)
                .setSubTreeRiskCalculatorTypeForKnownFlow(SubTreeRiskCalculatorType.FLOW_SUM)
                .setSubTreeRiskCalculatorTypeForUnknownFlow(SubTreeRiskCalculatorType.MINIMAL_RISK_REACHABILITY)
                .buildAlgorithmConfig();
    }

    @Override
    public PacManGameConfig createGameConfig() {
        return new PacManGameConfigBuilder()
                .enemyType(PacManEnemyType.TRAPS)
                .bigBallReward(10.0)
                .smallBallReward(1.0)
                .gameString(PacManGameInstance.TEST00)
                .maximalStepCountBound(50)
                .oppositeMoveChance(0.2)
                .randomMoveChance(0.2)
                .trapChance(0.0)
                .build();
    }
}
