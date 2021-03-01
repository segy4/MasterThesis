package vahy.impl.episode;

import vahy.api.episode.EpisodeStepRecord;
import vahy.api.model.Action;
import vahy.api.model.State;
import vahy.api.model.observation.Observation;
import vahy.api.policy.PolicyRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EpisodeStepRecordImpl<
    TAction extends Enum<TAction> & Action,
    TObservation extends Observation<TObservation>,
    TState extends State<TAction, TObservation, TState>>
    implements EpisodeStepRecord<TAction, TObservation, TState> {

    private final int policyIdOnTurn;
    private final int inGameEntityId;
    private final TAction playedAction;
    private final PolicyRecord policyStepRecord;
    private final TState fromState;
    private final TState toState;
    private final double[] reward;

    public EpisodeStepRecordImpl(int policyIdOnTurn,
                                 int inGameEntityId,
                                 TAction playedAction,
                                 PolicyRecord policyStepRecord,
                                 TState fromState,
                                 TState toState,
                                 double[] reward) {
        this.policyIdOnTurn = policyIdOnTurn;
        this.inGameEntityId = inGameEntityId;
        this.playedAction = playedAction;
        this.policyStepRecord = policyStepRecord;
        this.fromState = fromState;
        this.toState = toState;
        this.reward = reward;
    }

    @Override
    public int getPolicyIdOnTurn() {
        return policyIdOnTurn;
    }

    @Override
    public int getInGameEntityIdOnTurn() {
        return inGameEntityId;
    }

    @Override
    public TAction getAction() {
        return playedAction;
    }

    @Override
    public PolicyRecord getPolicyStepRecord() {
        return policyStepRecord;
    }

    @Override
    public TState getFromState() {
        return fromState;
    }

    @Override
    public TState getToState() {
        return toState;
    }

    @Override
    public double[] getReward() {
        return reward;
    }

    @Override
    public String toString() {
        return "EpisodeStepRecord{" +
            "policyOnTurnId=" + policyIdOnTurn +
            ", inGameEntityIdOnTurnId=" + inGameEntityId +
            ", playedAction=" + playedAction +
            ", policyStepRecord=" + (policyStepRecord != null ? policyStepRecord.toString() : null) +
            ", fromState=" + fromState +
            ", toState=" + toState +
            ", reward=" + Arrays.toString(reward) +
            '}';
    }

    @Override
    public String toLogString() {
        StringBuilder sb = new StringBuilder();
        sb.append("action: ");
        sb.append(getAction());
        sb.append(" getting reward: ");
        sb.append(Arrays.toString(getReward()));
        sb.append(". PolicyStepLog: ");
        sb.append((policyStepRecord != null ? policyStepRecord.toLogString() : null));
        return sb.toString();
    }

    @Override
    public List<String> getCsvHeader() {
        var list = new ArrayList<String>();
        list.add("PolicyId");
        list.add("InGameEntityId");
        list.add("Action played");
        list.add("Obtained reward");
        if(policyStepRecord != null) {
            list.addAll(policyStepRecord.getCsvHeader());
        }
        return list;
    }

    @Override
    public List<String> getCsvRecord() {
        var list = new ArrayList<String>();
        list.add(Integer.toString(policyIdOnTurn));
        list.add(Integer.toString(inGameEntityId));
        list.add(playedAction.toString());
        list.add(Arrays.toString(reward));
        if(policyStepRecord != null) {
            list.addAll(policyStepRecord.getCsvRecord());
        }
        return list;
    }
}

