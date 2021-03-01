package vahy.impl.learning.trainer;

import vahy.api.learning.dataAggregator.DataAggregator;
import vahy.api.learning.trainer.EpisodeDataMaker;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.predictor.TrainablePredictor;

public class PredictorTrainingSetup<TAction extends Enum<TAction> & Action, TObservation extends Observation<TObservation>, TState extends State<TAction, TObservation, TState>> {

    private final int predictorTrainingSetupId;
    private final TrainablePredictor trainablePredictor;
    private final EpisodeDataMaker<TAction, TObservation, TState> episodeDataMaker;
    private final DataAggregator dataAggregator;

    public PredictorTrainingSetup(int predictorTrainingSetupId, TrainablePredictor trainablePredictor, EpisodeDataMaker<TAction, TObservation, TState> episodeDataMaker, DataAggregator dataAggregator) {
        this.predictorTrainingSetupId = predictorTrainingSetupId;
        this.trainablePredictor = trainablePredictor;
        this.episodeDataMaker = episodeDataMaker;
        this.dataAggregator = dataAggregator;
    }

    public int getPredictorTrainingSetupId() {
        return predictorTrainingSetupId;
    }

    public TrainablePredictor getTrainablePredictor() {
        return trainablePredictor;
    }

    public EpisodeDataMaker<TAction, TObservation, TState> getEpisodeDataMaker() {
        return episodeDataMaker;
    }

    public DataAggregator getDataAggregator() {
        return dataAggregator;
    }
}
