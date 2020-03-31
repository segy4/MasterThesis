package Template;

import vahy.api.model.StateRewardReturn;
import vahy.impl.model.observation.DoubleVector;
import vahy.paperGenerics.PaperState;

import java.util.List;

public class TemplateState implements PaperState<TemplateAction, DoubleVector, TemplateEnvironmentObservation, TemplateState> {

    /**
     * Method to determine if allowed risk is reached
     * E.g. risk can be the death of the PLAYER.
     * @return true/false if risk is reached
     */
    @Override
    public boolean isRiskHit() {
        return false;
    }

    /**
     * Method to return All possible actions at any given state.
     * You only return actions that are playable.
     * That means if it's PLAYER turn this will return possible PLAYER actions and similarly for OPPONENT.
     * @return array of playable actions
     */
    @Override
    public TemplateAction[] getAllPossibleActions() {
        return new TemplateAction[0];
    }

    /**
     * Method to return All possible PLAYER actions.
     * If it's not PLAYER turn this should return an empty array.
     * This method isn't used in algorithm. It's for your own convenience.
     * @return array of playable PLAYER actions
     */
    @Override
    public TemplateAction[] getPossiblePlayerActions() {
        return new TemplateAction[0];
    }

    /**
     * Method to return All possible OPPONENT actions.
     * If it's not OPPONENT turn this should return an empty array.
     * This method isn't used in algorithm. It's for your own convenience.
     * @return array of playable OPPONENT actions
     */
    @Override
    public TemplateAction[] getPossibleOpponentActions() {
        return new TemplateAction[0];
    }

    /**
     * Method to apply action and create a new State based on that action.
     * This method is used for both PLAYER and OPPONENT actions.
     * @tip: as return you can use ImmutableStateRewardReturnTuple.
     * It is a generic cimplementation of StateRewardReturn interface.
     * @param actionType instance of Action enum that is being played.
     * @return Immutable tuple with new State and acquired reward.
     */
    @Override
    public StateRewardReturn<TemplateAction, DoubleVector, TemplateEnvironmentObservation, TemplateState> applyAction(TemplateAction actionType) {
        return null;
    }

    /**
     * Method to create a deepCopy of this State
     * @return deepCopy of this State
     */
    @Override
    public TemplateState deepCopy() {
        return null;
    }

    /**
     * Method to create PLAYER Observation (DoubleVector) that holds information about the problem state
     * and feeds this information to the algorithm so he can make an educated guess about the best possible action.
     * @return Observation that is relevant to the Player.
     * E.g. The whole board in chess. State of table in poker.
     */
    @Override
    public DoubleVector getPlayerObservation() {
        return null;
    }

    /**
     * Method to create OPPONENT FixedModelObservation that holds information about the problem state
     * and feeds this information to the algorithm. He than chooses an action at random.
     * @return FixedModelObservation that holds map between actions and probabilities.
     */
    @Override
    public TemplateEnvironmentObservation getOpponentObservation() {
        return null;
    }

    /**
     * Method that created String representation of this State.
     * @return String representation of this State.
     */
    @Override
    public String readableStringRepresentation() {
        return null;
    }

    /**
     * Method to log this state
     * @return List of strings with csv headers for the log.
     */
    @Override
    public List<String> getCsvHeader() {
        return null;
    }

    /**
     * Method to log this state
     * @return List of strings with csv records for the log.
     */
    @Override
    public List<String> getCsvRecord() {
        return null;
    }

    /**
     * Method to check is it's OPPONENT's turn.
     * @return true/false if it is OPPONENT's turn
     */
    @Override
    public boolean isOpponentTurn() {
        return false;
    }

    /**
     * Method to check if this state is final.
     * The game can't continue from this point onward.
     * @return true/false if this is a final state
     */
    @Override
    public boolean isFinalState() {
        return false;
    }
}
