import Environment.GameConfig.PacManGameConfig;
import vahy.api.experiment.SystemConfig;
import vahy.config.PaperAlgorithmConfig;

/**
 * Class that creates initial Configs for algorithm
 * These configs can be created anywhere, but I am putting all documentation in one place.
 */
public class TemplateRun {

    /**
     * Template for class that will run the experiment
     * You need three different configs.
     * SystemConfig - This holds information about for system. (such as if you want the run to be reproducible, amount of episodes etc.)
     * AlgorithmConfig - This holds information about how the algorithm works. (such as temperature, exploration rate, risk etc.)
     * GameConfig - This holds information about the first setup of the game. (this is used in initial supplier to create first state)
     */
    void run() {
        PaperAlgorithmConfig algorithmConfig = createAlgorithmConfig();
        List<PaperAlgorithmConfig> algorithmConfigList = new ArrayList<>();
        algorithmConfigList.add(algorithmConfig);
        SystemConfig systemConfig = createSystemConfig();
        TemplateProblemConfig gameConfig = createGameConfig();

        PaperExperimentBuilder<TemplateProblemConfig, TemplateAction, TemplateEnvironmentObservation, TemplateState> experiment =
                new PaperExperimentBuilder<PacManGameConfig, PacManAction, PacManEnvironmentProbabilities, PacManState>()
                        .setActionClass(TemplateAction.class)
                        .setSystemConfig(systemConfig)
                        .setAlgorithmConfigList(algorithmConfigList)
                        .setProblemConfig(gameConfig)
                        .setOpponentSupplier(TemplateRandomizedPolicySupplier::new)
                        .setProblemInstanceInitializerSupplier(TemplateInitialStateSupplier::new);

        experiment.execute();
    }

    /**
     * Creates SystemConfig
     * @return SystemConfig
     */
    private SystemConfig createSystemConfig() {
        return new SystemConfigBuilder()
                .setRandomSeed(0)
                .setStochasticStrategy(StochasticStrategy.REPRODUCIBLE)
                .setDrawWindow(false)
                .setParallelThreadsCount(Runtime.getRuntime().availableProcessors())
                .setSingleThreadedEvaluation(false)
                .setEvalEpisodeCount(1000)
                .setDumpTrainingData(false)
                .buildSystemConfig();
    }

    /**
     * Creates AlgorithmConfig
     * @return AlgorithmConfig
     */
    PaperAlgorithmConfig createAlgorithmConfig() {
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

    /**
     * Creates GameConfig
     * @return GameConfig
     */
    TemplateProblemConfig createGameConfig() {
        return new TemplateProblemConfig();
    }
}
