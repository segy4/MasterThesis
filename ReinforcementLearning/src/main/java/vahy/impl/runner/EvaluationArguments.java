package vahy.impl.runner;

import vahy.api.benchmark.EpisodeStatistics;
import vahy.api.benchmark.EpisodeStatisticsCalculator;
import vahy.api.episode.EpisodeResultsFactory;
import vahy.api.episode.InitialStateSupplier;
import vahy.api.episode.StateWrapperInitializer;
import vahy.api.experiment.ProblemConfig;
import vahy.api.experiment.SystemConfig;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;

import java.util.List;
import java.util.SplittableRandom;

public class EvaluationArguments<TConfig extends ProblemConfig,
    TAction extends Enum<TAction> & Action,
    TObservation extends Observation<TObservation>,
    TState extends State<TAction, TObservation, TState>,
    TStatistics extends EpisodeStatistics> {

    private final String runName;

    private final TConfig problemConfig;
    private final SystemConfig systemConfig;
    private final SplittableRandom finalMasterRandom;

    private final InitialStateSupplier<TAction, TObservation, TState> initialStateSupplier;
    private final StateWrapperInitializer<TAction, TObservation, TState> stateStateWrapperInitializer;
    private final EpisodeResultsFactory<TAction, TObservation, TState> episodeResultsFactory;
    private final EpisodeStatisticsCalculator<TAction, TObservation, TState, TStatistics> episodeStatisticsCalculator;
    private final EpisodeWriter<TAction, TObservation, TState> episodeWriter;
    private final List<PolicyDefinition<TAction, TObservation, TState>> environmentPolicyArgumemntList;

    public EvaluationArguments(String runName,
                               TConfig problemConfig,
                               SystemConfig systemConfig,
                               SplittableRandom finalMasterRandom,
                               InitialStateSupplier<TAction, TObservation, TState> initialStateSupplier,
                               StateWrapperInitializer<TAction, TObservation, TState> stateStateWrapperInitializer,
                               EpisodeResultsFactory<TAction, TObservation, TState> episodeResultsFactory,
                               EpisodeStatisticsCalculator<TAction, TObservation, TState, TStatistics> episodeStatisticsCalculator,
                               EpisodeWriter<TAction, TObservation, TState> episodeWriter,
                               List<PolicyDefinition<TAction, TObservation, TState>> environmentPolicyArgumemntList)
    {
        this.runName = runName;
        this.problemConfig = problemConfig;
        this.systemConfig = systemConfig;
        this.finalMasterRandom = finalMasterRandom;
        this.initialStateSupplier = initialStateSupplier;
        this.stateStateWrapperInitializer = stateStateWrapperInitializer;
        this.episodeResultsFactory = episodeResultsFactory;
        this.episodeStatisticsCalculator = episodeStatisticsCalculator;
        this.episodeWriter = episodeWriter;
        this.environmentPolicyArgumemntList = environmentPolicyArgumemntList;
    }

    public String getRunName() {
        return runName;
    }

    public TConfig getProblemConfig() {
        return problemConfig;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    public InitialStateSupplier<TAction, TObservation, TState> getInitialStateSupplier() {
        return initialStateSupplier;
    }

    public StateWrapperInitializer<TAction, TObservation, TState> getStateStateWrapperInitializer() {
        return stateStateWrapperInitializer;
    }

    public EpisodeResultsFactory<TAction, TObservation, TState> getEpisodeResultsFactory() {
        return episodeResultsFactory;
    }

    public EpisodeStatisticsCalculator<TAction, TObservation, TState, TStatistics> getEpisodeStatisticsCalculator() {
        return episodeStatisticsCalculator;
    }

    public EpisodeWriter<TAction, TObservation, TState> getEpisodeWriter() {
        return episodeWriter;
    }

    public List<PolicyDefinition<TAction, TObservation, TState>> getEnvironmentPolicyArgumemntList() {
        return environmentPolicyArgumemntList;
    }

    public SplittableRandom getFinalMasterRandom() {
        return finalMasterRandom;
    }
}
