package vahy.integration;

public class IntegrationTest {

//    @BeforeTest
//    public void cleanUpNativeLibraries() {
//        ThirdPartBinaryUtils.cleanUpNativeTempFiles();
//    }
//
//    @DataProvider(name = "myTest")
//    public static Object[][] experimentSettings() {
//        return new Object[][] {
//            {createExperiment_03(), 50, 0.055},
//            {createExperiment_05(), 280, 0.0},
//            {createExperiment_07(), 50, 0.0},
//        };
//    }
//
//    @Test(dataProvider = "myTest")
//    public void benchmarkSolutionTest(ImmutableTuple<GameConfig, ExperimentSetup> setup,
//                                      double minExpectedReward,
//                                      double maxRiskHitRatio) throws NotValidGameStringRepresentationException, IOException {
//        SplittableRandom random = new SplittableRandom(setup.getSecond().getRandomSeed());
//        var experiment = new Experiment();
//        experiment.run(setup, random);
//
//        var results = experiment.getResults().get(0);
//
//        Assert.assertTrue(results.getCalculatedResultStatistics().getTotalPayoffAverage() >= minExpectedReward, "Avg reward is: [" + results.getCalculatedResultStatistics().getTotalPayoffAverage() + "] but expected at least: [" + minExpectedReward + "]");
//        Assert.assertTrue(results.getCalculatedResultStatistics().getRiskHitRatio() <= maxRiskHitRatio, "Risk hit ratio is: [" + results.getCalculatedResultStatistics().getRiskHitRatio() + "] but expected at most: [" + maxRiskHitRatio + "]");
//    }
//
//    public static ImmutableTuple<GameConfig, ExperimentSetup> createExperiment_03() {
//        GameConfig gameConfig = new ConfigBuilder()
//            .reward(100)
//            .noisyMoveProbability(0.0)
//            .stepPenalty(10)
//            .trapProbability(0.1)
//            .stateRepresentation(StateRepresentation.COMPACT)
//            .buildConfig();
//
//        var algorithmConfig = new AlgorithmConfigBuilder()
//            .randomSeed(0)
//            .hallwayInstance(HallwayInstance.BENCHMARK_03)
//            //MCTS
//            .cpuctParameter(3)
//            .treeUpdateConditionFactory(new FixedUpdateCountTreeConditionFactory(50))
//            //.mcRolloutCount(1)
//            //NN
//            .trainingBatchSize(0)
//            .trainingEpochCount(0)
//            // REINFORCEMENT
//            .discountFactor(1)
//            .batchEpisodeCount(100)
//            .stageCount(100)
//            .maximalStepCountBound(1000)
//            .trainerAlgorithm(TrainerAlgorithm.EVERY_VISIT_MC)
//            .approximatorType(ApproximatorType.HASHMAP)
//            .selectorType(SelectorType.UCB)
//            .evaluatorType(EvaluatorType.RALF)
//            .evalEpisodeCount(10000)
//            .globalRiskAllowed(0.05)
//            .riskSupplier(() -> 0.05)
//            .explorationConstantSupplier(() -> 0.2)
//            .temperatureSupplier(() -> 2.0)
//            .setInferenceExistingFlowStrategy(InferenceExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW)
//            .setInferenceNonExistingFlowStrategy(InferenceNonExistingFlowStrategy.MAX_UCB_VISIT)
//            .setExplorationExistingFlowStrategy(ExplorationExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW_BOLTZMANN_NOISE)
//            .setExplorationNonExistingFlowStrategy(ExplorationNonExistingFlowStrategy.SAMPLE_UCB_VISIT)
//            .setFlowOptimizerType(FlowOptimizerType.HARD_HARD)
//            .setSubTreeRiskCalculatorTypeForKnownFlow(SubTreeRiskCalculatorType.FLOW_SUM)
//            .setSubTreeRiskCalculatorTypeForUnknownFlow(SubTreeRiskCalculatorType.PRIOR_SUM)
//            .buildAlgorithmConfig();
//        return new ImmutableTuple<>(algorithmConfig, systemConfig);
//    }
//
//    public static ImmutableTuple<GameConfig, ExperimentSetup> createExperiment_05() {
//
//        GameConfig gameConfig = new ConfigBuilder()
//            .reward(100)
//            .noisyMoveProbability(0.1)
//            .stepPenalty(1)
//            .trapProbability(1)
//            .stateRepresentation(StateRepresentation.COMPACT)
//            .buildConfig();
//
//        var algorithmConfig = new AlgorithmConfigBuilder()
//            .randomSeed(0)
//            .hallwayInstance(HallwayInstance.BENCHMARK_05)
//            //MCTS
//            .cpuctParameter(3)
//            .treeUpdateConditionFactory(new FixedUpdateCountTreeConditionFactory(100))
//            //.mcRolloutCount(1)
//            //NN
//            .trainingBatchSize(1)
//            .trainingEpochCount(10)
//            // REINFORCEMENT
//            .discountFactor(1)
//            .batchEpisodeCount(100)
//            .stageCount(100)
//            .maximalStepCountBound(1000)
//            .trainerAlgorithm(TrainerAlgorithm.EVERY_VISIT_MC)
//            .approximatorType(ApproximatorType.HASHMAP)
//            .replayBufferSize(10000)
//            .selectorType(SelectorType.UCB)
//            .evaluatorType(EvaluatorType.RALF)
//            .evalEpisodeCount(1000)
//            .globalRiskAllowed(0.00)
//            .riskSupplier(() -> 0.00)
//            .explorationConstantSupplier(() -> 0.2)
//            .temperatureSupplier(() -> 1.5)
//            .setInferenceExistingFlowStrategy(InferenceExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW)
//            .setInferenceNonExistingFlowStrategy(InferenceNonExistingFlowStrategy.MAX_UCB_VISIT)
//            .setExplorationExistingFlowStrategy(ExplorationExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW_BOLTZMANN_NOISE)
//            .setExplorationNonExistingFlowStrategy(ExplorationNonExistingFlowStrategy.SAMPLE_UCB_VISIT)
//            .setFlowOptimizerType(FlowOptimizerType.HARD_HARD)
//            .setSubTreeRiskCalculatorTypeForKnownFlow(SubTreeRiskCalculatorType.FLOW_SUM)
//            .setSubTreeRiskCalculatorTypeForUnknownFlow(SubTreeRiskCalculatorType.PRIOR_SUM)
//            .buildAlgorithmConfig();
//        return new ImmutableTuple<>(algorithmConfig, systemConfig);
//    }
//
//    public static ImmutableTuple<GameConfig, ExperimentSetup> createExperiment_07() {
//        GameConfig gameConfig = new ConfigBuilder()
//            .reward(100)
//            .noisyMoveProbability(0.4)
//            .stepPenalty(1)
//            .trapProbability(1)
//            .stateRepresentation(StateRepresentation.COMPACT)
//            .buildConfig();
//
//        var algorithmConfig = new AlgorithmConfigBuilder()
//            .randomSeed(0)
//            .hallwayInstance(HallwayInstance.BENCHMARK_07)
//            //MCTS
//            .cpuctParameter(3)
//            .treeUpdateConditionFactory(new FixedUpdateCountTreeConditionFactory(100))
//            //.mcRolloutCount(1)
//            //NN
//            .trainingBatchSize(0)
//            .trainingEpochCount(0)
//            // REINFORCEMENT
//            .discountFactor(1)
//            .batchEpisodeCount(100)
//            .stageCount(10)
//            .maximalStepCountBound(1000)
//            .trainerAlgorithm(TrainerAlgorithm.EVERY_VISIT_MC)
//            .approximatorType(ApproximatorType.HASHMAP)
//            .replayBufferSize(10000)
//            .selectorType(SelectorType.UCB)
//            .evaluatorType(EvaluatorType.RALF)
//            .evalEpisodeCount(1000)
//            .globalRiskAllowed(0.00)
//            .riskSupplier(() -> 0.00)
//            .explorationConstantSupplier(() -> 0.0)
//            .temperatureSupplier(() -> 0.0)
//            .setInferenceExistingFlowStrategy(InferenceExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW)
//            .setInferenceNonExistingFlowStrategy(InferenceNonExistingFlowStrategy.MAX_UCB_VISIT)
//            .setExplorationExistingFlowStrategy(ExplorationExistingFlowStrategy.SAMPLE_OPTIMAL_FLOW_BOLTZMANN_NOISE)
//            .setExplorationNonExistingFlowStrategy(ExplorationNonExistingFlowStrategy.SAMPLE_UCB_VISIT)
//            .setFlowOptimizerType(FlowOptimizerType.HARD_HARD)
//            .setSubTreeRiskCalculatorTypeForKnownFlow(SubTreeRiskCalculatorType.PRIOR_SUM)
//            .setSubTreeRiskCalculatorTypeForUnknownFlow(SubTreeRiskCalculatorType.PRIOR_SUM)
//            .buildAlgorithmConfig();
//        return new ImmutableTuple<>(algorithmConfig, systemConfig);
//    }
}
