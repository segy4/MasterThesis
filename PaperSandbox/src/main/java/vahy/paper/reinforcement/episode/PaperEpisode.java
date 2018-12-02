package vahy.paper.reinforcement.episode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.api.model.StateActionReward;
import vahy.api.model.StateRewardReturn;
import vahy.environment.HallwayAction;
import vahy.environment.agent.policy.environment.EnvironmentPolicy;
import vahy.environment.state.HallwayStateImpl;
import vahy.impl.model.ImmutableStateActionRewardTuple;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.model.reward.DoubleReward;
import vahy.paper.policy.PaperPolicy;
import vahy.paperGenerics.reinforcement.episode.StepRecord;
import vahy.utils.ImmutableTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PaperEpisode {

    private static final Logger logger = LoggerFactory.getLogger(PaperEpisode.class);
    private final HallwayStateImpl initialState;
    private final PaperPolicy playerPaperPolicy;
    private final EnvironmentPolicy opponentPolicy;
    private final int stepCountLimit;

    private List<StateRewardReturn<HallwayAction, DoubleReward, DoubleVector, HallwayStateImpl>> episodeStateRewardReturnList = new ArrayList<>();
    private List<ImmutableTuple<StateActionReward<HallwayAction, DoubleReward, DoubleVector, HallwayStateImpl>, StepRecord>> episodeHistoryList = new ArrayList<>();
    private long millisecondDuration;

    private boolean episodeAlreadySimulated = false;

    public PaperEpisode(
        HallwayStateImpl initialState,
        PaperPolicy playerPaperPolicy,
        EnvironmentPolicy opponentPolicy, int stepCountLimit) {
        this.initialState = initialState;
        this.playerPaperPolicy = playerPaperPolicy;
        this.opponentPolicy = opponentPolicy;
        this.stepCountLimit = stepCountLimit;
    }

    public void runEpisode() {
        if(episodeAlreadySimulated) {
            throw new IllegalStateException("PaperEpisode was already simulated");
        }
        HallwayStateImpl state = this.initialState;
        logger.trace("State at the begin of episode: " + System.lineSeparator() + state.readableStringRepresentation());
        int playerActionCount = 0;
        long start = System.currentTimeMillis();
        int stepsDone = 0;
        while(!state.isFinalState()) {
            if(stepsDone >= stepCountLimit) {
                break;
            }
            HallwayAction action = playerPaperPolicy.getDiscreteAction(state);
            double[] actionProbabilities = playerPaperPolicy.getActionProbabilityDistribution(state);
            double[] priorProbabilities = playerPaperPolicy.getPriorActionProbabilityDistribution(state);
            DoubleReward estimatedReward = playerPaperPolicy.getEstimatedReward(state);
            double estimatedRisk = playerPaperPolicy.getEstimatedRisk(state);
            playerPaperPolicy.updateStateOnOpponentActions(Collections.singletonList(action));
            playerActionCount++;
            StateRewardReturn<HallwayAction, DoubleReward, DoubleVector, HallwayStateImpl> stateRewardReturn = state.applyAction(action);
            stepsDone++;
            logger.debug("Player's [{}]th action: [{}], getting reward [{}]", playerActionCount, action, stateRewardReturn.getReward().toPrettyString());
            episodeStateRewardReturnList.add(stateRewardReturn);
            episodeHistoryList.add(new ImmutableTuple<>(new ImmutableStateActionRewardTuple<>(state, action, stateRewardReturn.getReward()), new StepRecord<>(priorProbabilities, actionProbabilities, estimatedReward, estimatedRisk)));
            state = stateRewardReturn.getState();
            if(!state.isFinalState()) {
                action = opponentPolicy.getDiscreteAction(state);
                actionProbabilities = playerPaperPolicy.getActionProbabilityDistribution(state);
                priorProbabilities = playerPaperPolicy.getPriorActionProbabilityDistribution(state);
                estimatedReward = playerPaperPolicy.getEstimatedReward(state);
                estimatedRisk = playerPaperPolicy.getEstimatedRisk(state);
                stateRewardReturn = state.applyAction(action);
                playerPaperPolicy.updateStateOnOpponentActions(Collections.singletonList(action));
                logger.debug("Environment's [{}]th action: [{}], getting reward [{}]", playerActionCount, action, stateRewardReturn.getReward().toPrettyString());
                episodeStateRewardReturnList.add(stateRewardReturn);
                episodeHistoryList.add(new ImmutableTuple<>(new ImmutableStateActionRewardTuple<>(state, action, stateRewardReturn.getReward()), new StepRecord<>(priorProbabilities, actionProbabilities, estimatedReward, estimatedRisk)));
                state = stateRewardReturn.getState();
            }
            logger.debug("State at [{}]th timestamp: " + System.lineSeparator() + state.readableStringRepresentation(), playerActionCount);
        }
        long end = System.currentTimeMillis();
        millisecondDuration = end - start;
        logger.info("Total episode time: [{}]ms", millisecondDuration);
        logger.info("PaperEpisode actions: [" + episodeHistoryList.stream().map(x -> x.getFirst().getAction()).collect(Collectors.toList()) + "]");
        logger.info("Total reward: [" + episodeHistoryList.stream().mapToDouble(x -> x.getFirst().getReward().getValue()).sum() + "]");
        episodeAlreadySimulated = true;
    }

    public long getMillisecondDuration() {
        return millisecondDuration;
    }

    public boolean isEpisodeAlreadySimulated() {
        return episodeAlreadySimulated;
    }

    public boolean isAgentKilled() {
        return ((HallwayStateImpl) this.getFinalState()).isAgentKilled();
    }

    public List<StateRewardReturn<HallwayAction, DoubleReward, DoubleVector, HallwayStateImpl>> getEpisodeStateRewardReturnList() {
        return this.episodeStateRewardReturnList;
    }

    public List<ImmutableTuple<StateActionReward<HallwayAction, DoubleReward, DoubleVector, HallwayStateImpl>, StepRecord>> getEpisodeStateActionRewardList() {
        return episodeHistoryList;
    }

    public HallwayStateImpl getFinalState() {
        return this.episodeStateRewardReturnList.get(episodeStateRewardReturnList.size() - 1).getState();
    }

}
