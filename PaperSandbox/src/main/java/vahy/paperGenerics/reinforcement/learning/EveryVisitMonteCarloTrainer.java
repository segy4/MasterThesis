package vahy.paperGenerics.reinforcement.learning;

import vahy.api.episode.InitialStateSupplier;
import vahy.api.model.Action;
import vahy.api.model.StateActionReward;
import vahy.api.model.observation.Observation;
import vahy.api.model.reward.RewardAggregator;
import vahy.api.search.nodeEvaluator.TrainableNodeEvaluator;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.model.reward.DoubleReward;
import vahy.paperGenerics.PaperMetadata;
import vahy.paperGenerics.PaperState;
import vahy.paperGenerics.policy.PaperPolicySupplier;
import vahy.paperGenerics.policy.TrainablePaperPolicySupplier;
import vahy.paperGenerics.reinforcement.episode.EpisodeResults;
import vahy.paperGenerics.reinforcement.episode.StepRecord;
import vahy.utils.ImmutableTuple;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EveryVisitMonteCarloTrainer<
    TAction extends Enum<TAction> & Action,
    TOpponentObservation extends Observation,
    TSearchNodeMetadata extends PaperMetadata<TAction, DoubleReward>,
    TState extends PaperState<TAction, DoubleReward, DoubleVector, TOpponentObservation, TState>>
    extends AbstractMonteCarloTrainer<TAction, TOpponentObservation, TSearchNodeMetadata, TState> {

    public EveryVisitMonteCarloTrainer(InitialStateSupplier<TAction, DoubleReward, DoubleVector, TOpponentObservation, TState> initialStateSupplier,
                                       TrainablePaperPolicySupplier<TAction, DoubleReward, DoubleVector, TOpponentObservation, TSearchNodeMetadata, TState> paperTrainablePolicySupplier,
                                       PaperPolicySupplier<TAction, DoubleReward, DoubleVector, TOpponentObservation, TSearchNodeMetadata, TState> opponentPolicySupplier,
                                       TrainableNodeEvaluator<TAction, DoubleReward, DoubleVector, TOpponentObservation, TSearchNodeMetadata, TState> paperNodeEvaluator,
                                       double discountFactor,
                                       RewardAggregator<DoubleReward> rewardAggregator,
                                       int stepCountLimit) {
        super(initialStateSupplier, paperTrainablePolicySupplier, opponentPolicySupplier, paperNodeEvaluator, discountFactor, rewardAggregator, stepCountLimit);
    }

    @Override
    protected Map<DoubleVector, MutableDataSample> calculatedVisitedRewards(EpisodeResults<TAction, DoubleReward, DoubleVector, TOpponentObservation, TState> paperEpisode) {
        Map<DoubleVector, MutableDataSample> everyVisitSet = new LinkedHashMap<>();
        List<ImmutableTuple<StateActionReward<TAction, DoubleReward, DoubleVector, TOpponentObservation, TState>, StepRecord<DoubleReward>>> episodeHistory = paperEpisode.getEpisodeHistoryList();
        boolean isRiskHit = paperEpisode.isRiskHit();
        for (int i = 0; i < episodeHistory.size(); i++) {
            if(!episodeHistory.get(i).getFirst().getState().isOpponentTurn()) {
                MutableDataSample dataSample = createDataSample(episodeHistory, i, isRiskHit);
                DoubleVector experimentalObservation = episodeHistory.get(i).getFirst().getState().getPlayerObservation();
                if(!everyVisitSet.containsKey(experimentalObservation)) {
                    everyVisitSet.put(experimentalObservation, dataSample);
                } else {
                    everyVisitSet.get(experimentalObservation).addDataSample(dataSample);
                }
            }
        }
        return everyVisitSet;
    }
}
