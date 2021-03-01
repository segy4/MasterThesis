package vahy.impl.learning.trainer;

import vahy.api.benchmark.EpisodeStatistics;
import vahy.api.benchmark.EpisodeStatisticsCalculator;
import vahy.api.episode.EpisodeResults;
import vahy.api.episode.GameSampler;
import vahy.api.experiment.ProblemConfig;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.policy.PolicyMode;
import vahy.impl.episode.DataPointGeneratorGeneric;
import vahy.utils.ImmutableTuple;
import vahy.utils.StreamUtils;
import vahy.vizualization.LabelData;
import vahy.vizualization.ProgressTracker;
import vahy.vizualization.ProgressTrackerSettings;

import java.awt.Color;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Trainer<
    TAction extends Enum<TAction> & Action,
    TObservation extends Observation<TObservation>,
    TState extends State<TAction, TObservation, TState>,
    TStatistics extends EpisodeStatistics> {

    private final ProblemConfig problemConfig;
    private final GameSampler<TAction, TObservation, TState> gameSampler;
    private final ProgressTracker trainingProgressTracker;
    private final ProgressTracker samplingProgressTracker;
    private final ProgressTracker evaluationProgressTracker;
    private final EpisodeStatisticsCalculator<TAction, TObservation, TState, TStatistics> statisticsCalculator;

    private final List<PredictorTrainingSetup<TAction, TObservation, TState>> trainablePredictorSetupList;

    private final List<DataPointGeneratorGeneric<TStatistics>> samplingDataGeneratorList;
    private final List<DataPointGeneratorGeneric<TStatistics>> evalDataGeneratorList;

    private final DataPointGeneratorGeneric<LabelData> oobAvgMsPerEpisode = new DataPointGeneratorGeneric<>("OutOfBox avg ms per episode", List::of);
    private final DataPointGeneratorGeneric<LabelData> oobSamplingTime = new DataPointGeneratorGeneric<>("Sampling time [s]", List::of);
    private final DataPointGeneratorGeneric<List<LabelData>> trainingSampleCount = new DataPointGeneratorGeneric<>("Training sample count", x -> x);
    private final DataPointGeneratorGeneric<List<LabelData>> secTraining = new DataPointGeneratorGeneric<>("Training time [s]", x -> x);
    private final DataPointGeneratorGeneric<List<LabelData>> msTrainingPerSample = new DataPointGeneratorGeneric<>("Training per sample [ms]", x -> x);

    public Trainer(GameSampler<TAction, TObservation, TState> gameSampler,
                   List<PredictorTrainingSetup<TAction, TObservation, TState>> trainablePredictorSetupList,
                   ProgressTrackerSettings progressTrackerSettings,
                   ProblemConfig problemConfig,
                   EpisodeStatisticsCalculator<TAction, TObservation, TState, TStatistics> statisticsCalculator,
                   List<DataPointGeneratorGeneric<TStatistics>> additionalDataPointGeneratorList) {
        if (trainablePredictorSetupList.isEmpty()) {
            throw new IllegalArgumentException("TrainablePredictorSetupList can't be empty");
        }
        this.problemConfig = problemConfig;
        this.trainablePredictorSetupList = trainablePredictorSetupList;
        this.gameSampler = gameSampler;
        this.statisticsCalculator = statisticsCalculator;

        this.trainingProgressTracker = new ProgressTracker(progressTrackerSettings, "Training stats", Color.BLUE);
        this.samplingProgressTracker = new ProgressTracker(progressTrackerSettings, "Sampling stats", Color.GREEN);
        this.evaluationProgressTracker = new ProgressTracker(progressTrackerSettings, "Eval stats", Color.ORANGE);

        var baseDataGenerators = addBaseDataGenerators(additionalDataPointGeneratorList);

        samplingDataGeneratorList = baseDataGenerators.stream().map(DataPointGeneratorGeneric::createCopy).collect(Collectors.toList());
        evalDataGeneratorList = baseDataGenerators.stream().map(DataPointGeneratorGeneric::createCopy).collect(Collectors.toList());

        trainingProgressTracker.registerDataCollector(oobAvgMsPerEpisode);
        trainingProgressTracker.registerDataCollector(oobSamplingTime);
        trainingProgressTracker.registerDataCollector(trainingSampleCount);
        trainingProgressTracker.registerDataCollector(secTraining);
        trainingProgressTracker.registerDataCollector(msTrainingPerSample);

        registerDataGenerators(samplingDataGeneratorList, samplingProgressTracker);
        registerDataGenerators(evalDataGeneratorList, evaluationProgressTracker);

        trainingProgressTracker.finalizeRegistration();
        samplingProgressTracker.finalizeRegistration();
        evaluationProgressTracker.finalizeRegistration();
    }

    private List<DataPointGeneratorGeneric<TStatistics>> addBaseDataGenerators(List<DataPointGeneratorGeneric<TStatistics>> additionalDataPointGeneratorList) {
        var dataPointGeneratorList = new ArrayList<>(additionalDataPointGeneratorList == null ? new ArrayList<>() : additionalDataPointGeneratorList);

        dataPointGeneratorList.add(new DataPointGeneratorGeneric<>("Avg Player Step Count", x -> StreamUtils.labelWrapperFunction(x.getAveragePlayerStepCount())));
        dataPointGeneratorList.add(new DataPointGeneratorGeneric<>("Avg Total Payoff", x -> StreamUtils.labelWrapperFunction(x.getTotalPayoffAverage())));
        dataPointGeneratorList.add(new DataPointGeneratorGeneric<>("Stdev Total Payoff", x -> StreamUtils.labelWrapperFunction(x.getTotalPayoffStdev())));
        dataPointGeneratorList.add(new DataPointGeneratorGeneric<>("Avg episode duration [ms]", x -> List.of(new LabelData("", x.getAverageMillisPerEpisode()))));
//        dataPointGeneratorList.add(new DataPointGeneratorGeneric<>("Stdev episode duration [ms]", x -> List.of(x.getStdevMillisPerEpisode())));
        dataPointGeneratorList.add(new DataPointGeneratorGeneric<>("Avg decision time [ms]", x -> StreamUtils.labelWrapperFunction(x.getAverageDecisionTimeInMillis())));
        return dataPointGeneratorList;
    }

    protected void registerDataGenerators(List<DataPointGeneratorGeneric<TStatistics>> dataPointGeneratorList, ProgressTracker progressTracker) {
        for (var fromEpisodesDataPointGenerator : dataPointGeneratorList) {
            progressTracker.registerDataCollector(fromEpisodesDataPointGenerator);
        }
    }

    public ImmutableTuple<List<EpisodeResults<TAction, TObservation, TState>>, TStatistics> sampleTraining(int episodeBatchSize) {
        var result = run(episodeBatchSize, samplingDataGeneratorList, PolicyMode.TRAINING);
        oobAvgMsPerEpisode.addNewValue(new LabelData("", result.getSecond().getTotalDuration().toMillis() / (double) episodeBatchSize));
        oobSamplingTime.addNewValue(new LabelData("", result.getSecond().getTotalDuration().toMillis() / 1000.0));
        return result;
    }

    public ImmutableTuple<List<EpisodeResults<TAction, TObservation, TState>>, TStatistics> evaluate(int episodeBatchSize) {
        return run(episodeBatchSize, evalDataGeneratorList, PolicyMode.INFERENCE);
    }

    private ImmutableTuple<List<EpisodeResults<TAction, TObservation, TState>>, TStatistics> run(int episodeBatchSize,
                                                                                                                List<DataPointGeneratorGeneric<TStatistics>> dataPointGeneratorList,
                                                                                                                PolicyMode policyMode) {
        var start = System.currentTimeMillis();
        var episodes = gameSampler.sampleEpisodes(episodeBatchSize, problemConfig.getMaximalStepCountBound(), policyMode);
        var samplingTime = System.currentTimeMillis() - start;

        var stats = statisticsCalculator.calculateStatistics(episodes, Duration.ofMillis(samplingTime));

        for (var fromEpisodesDataPointGenerator : dataPointGeneratorList) {
            fromEpisodesDataPointGenerator.addNewValue(stats);
        }
        return new ImmutableTuple<>(episodes, stats);
    }

    public void makeLog() {
        trainingProgressTracker.onNextLog();
        evaluationProgressTracker.onNextLog();
        samplingProgressTracker.onNextLog();
    }

    public void trainPredictors(List<EpisodeResults<TAction, TObservation, TState>> episodes) {
        var trainingSampleCountList = new ArrayList<LabelData>(trainablePredictorSetupList.size());
        var trainingTimeList = new ArrayList<LabelData>(trainablePredictorSetupList.size());
        var trainingMsPerSampleList = new ArrayList<LabelData>(trainablePredictorSetupList.size());

        for (var entry : trainablePredictorSetupList) {
            var dataAggregator = entry.getDataAggregator();
            for (var episode : episodes) {
                dataAggregator.addEpisodeSamples(entry.getEpisodeDataMaker().createEpisodeDataSamples(episode));
            }
            var trainingDataset = dataAggregator.getTrainingDataset();
            var datasetSize = trainingDataset.getFirst().length;

            var startTraining = System.currentTimeMillis();
            entry.getTrainablePredictor().train(trainingDataset);
            var endTraining = System.currentTimeMillis() - startTraining;

            trainingSampleCountList.add(new LabelData("Predictor: " + entry.getPredictorTrainingSetupId(), (double) datasetSize));
            trainingTimeList.add(new LabelData("Predictor: " + entry.getPredictorTrainingSetupId(),endTraining / 1000.0));
            trainingMsPerSampleList.add(new LabelData("Predictor: " + entry.getPredictorTrainingSetupId(),endTraining / (double) datasetSize));

        }
        trainingSampleCount.addNewValue(trainingSampleCountList);
        secTraining.addNewValue(trainingTimeList);
        msTrainingPerSample.addNewValue(trainingMsPerSampleList);
    }

}
