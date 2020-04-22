package Mains.Benchmarks;

import Environment.Action.PacManAction;
import Environment.GameConfig.PacManGameConfig;
import Environment.GameConfig.PacManInitialStateSupplier;
import Environment.Policy.PacManEnvironmentProbabilities;
import Environment.Policy.PacManEnvironmentPolicySupplier;
import Environment.State.PacManState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.api.experiment.StochasticStrategy;
import vahy.api.experiment.SystemConfig;
import vahy.api.experiment.SystemConfigBuilder;
import vahy.config.PaperAlgorithmConfig;
import vahy.paperGenerics.PaperExperimentBuilder;

import java.util.List;
import java.util.ArrayList;

public abstract class PacManMain implements IPacManMain {
    public static final Logger logger = LoggerFactory.getLogger(PacManMain.class.getName());

    public void run() {
        PaperAlgorithmConfig algorithmConfig = createAlgorithmConfig();
        List<PaperAlgorithmConfig> algorithmConfigList = new ArrayList<>();
        algorithmConfigList.add(algorithmConfig);
        SystemConfig systemConfig = createSystemConfig();
        PacManGameConfig gameConfig = createGameConfig();

        PaperExperimentBuilder<PacManGameConfig, PacManAction, PacManEnvironmentProbabilities, PacManState> experiment =
                new PaperExperimentBuilder<PacManGameConfig, PacManAction, PacManEnvironmentProbabilities, PacManState>()
                .setActionClass(PacManAction.class)
                .setSystemConfig(systemConfig)
                .setAlgorithmConfigList(algorithmConfigList)
                .setProblemConfig(gameConfig)
                .setOpponentSupplier(PacManEnvironmentPolicySupplier::new)
                .setProblemInstanceInitializerSupplier(PacManInitialStateSupplier::new);

        experiment.execute();
    }

    public SystemConfig createSystemConfig() {
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
}
