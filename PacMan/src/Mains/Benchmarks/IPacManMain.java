package Mains.Benchmarks;

import Environment.GameConfig.PacManGameConfig;
import vahy.api.experiment.SystemConfig;
import vahy.config.PaperAlgorithmConfig;

public interface IPacManMain {

    void run();

    PaperAlgorithmConfig createAlgorithmConfig();
    SystemConfig createSystemConfig();
    PacManGameConfig createGameConfig();
}
