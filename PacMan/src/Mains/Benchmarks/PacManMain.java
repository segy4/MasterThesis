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

import java.awt.*;

public abstract class PacManMain implements IPacManMain {
    public static final Logger logger = LoggerFactory.getLogger(PacManMain.class.getName());

    public void run() {
        PaperAlgorithmConfig algorithmConfig = createAlgorithmConfig();
        SystemConfig systemConfig = createSystemConfig();
        PacManGameConfig gameConfig = createGameConfig();

        PaperExperimentBuilder<PacManGameConfig, PacManAction, PacManEnvironmentProbabilities, PacManState> experiment =
                new PaperExperimentBuilder<>()
                .setActionClass(PacManAction.class)
                .setSystemConfig(systemConfig)
                .setAlgorithmConfigList(List.of(algorithmConfig))
                .setProblemConfig(gameConfig)
                .setOpponentSupplier(PacManEnvironmentPolicySupplier::new)
                .setProblemInstanceInitializerSupplier(PacManInitialStateSupplier::new)
                .execute();
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
