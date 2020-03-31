package Template;

import vahy.api.model.Action;

import java.util.Arrays;
import java.util.Comparator;

/**
 * This enum represents all possible PLAYER and OPPONENT actions
 * There is no limit on the number of actions, fields and methods
 * @Advice: Don't put too much functionality in this enum as it's only an enum. Other classes have more options for customization.
 * It needs to implement vahy.api.model.Action
 */
public enum TemplateAction implements Action {

    // any number of actions
    // These are just an advice on how they can look

    PLAYER_ACTION00(true, false, 0, 0),
    PLAYER_ACTION01(true, false, 1, 1),

    OPPONENT_ACTION00(false, true, 0, 2),
    OPPONENT_ACTION(false, true, 1, 3);

    // any number of local fields
    // These fields are only an tip how you can implement all methods easily.
    public static TemplateAction[] playerActions = Arrays.stream(TemplateAction.values()).filter(TemplateAction::isPlayerAction).sorted(Comparator.comparing(TemplateAction::getActionIndexInPlayerActions)).toArray(TemplateAction[]::new);
    public static TemplateAction[] opponentActions = Arrays.stream(TemplateAction.values()).filter(TemplateAction::isOpponentAction).sorted(Comparator.comparing(TemplateAction::getActionIndexInPlayerActions)).toArray(TemplateAction[]::new);
    private boolean isPlayerAction;
    private boolean isOpponentAction;
    private int localIndex;
    private int globalIndex;

    TemplateAction(boolean isPlayerAction, boolean isOpponentAction, int localIndex, int globalIndex) {
        this.isPlayerAction = isPlayerAction;
        this.isOpponentAction = isOpponentAction;
        this.localIndex = localIndex;
        this.globalIndex = globalIndex;
    }

    /**
     * Method to create an array of all possible PLAYER actions.
     * @return All possible PLAYER actions at any given moment
     */
    @Override
    public Action[] getAllPlayerActions() {
        return playerActions;
    }

    /**
     * Method to create an array of all possible OPPONENT actions
     * @return All possible OPPONENT actions at any given moment
     */
    @Override
    public Action[] getAllOpponentActions() {
        return opponentActions;
    }

    /**
     * Method to determine if this action is performable by the PLAYER
     * @tip: create a field that holds this information
     * @return true/false if this action is performable by the PLAYER
     */
    @Override
    public boolean isPlayerAction() {
        return isPlayerAction;
    }

    /**
     * Method to determine if this action is performable by the OPPONENT
     * @tip: create a field that holds this information
     * @return true/false if this action is performable by the OPPONENT
     */
    @Override
    public boolean isOpponentAction() {
        return isOpponentAction;
    }

    /**
     * Method to return index of an action from all possible enum values
     * Can be usefull for indexing an array of actions
     * @tip: create a field for this information
     * @return index of the action from all possible enum values
     */
    @Override
    public int getGlobalIndex() {
        return globalIndex;
    }

    /**
     * Method to return index of an action from all possible PLAYER actions
     * Can be usefull for indexing an array of PLAYER actions
     * @tip: create a field with local indexes that goes from 0 to (playerActions.length - 1)
     * @return index of PLAYER action from all possible PLAYER actions or -1 if this action isn't a PLAYER action
     */
    @Override
    public int getActionIndexInPlayerActions() {
        if (isPlayerAction()) {
            return localIndex;
        } else {
            return -1;
        }
    }

    /**
     * Method to return index of an action from all possible OPPONENT actions
     * Can be usefull for indexing an array of OPPONENT actions
     * @tip: create a field with local indexes that goes from 0 to (playerActions.length - 1)
     * @return index of OPPONENT action from all possible OPPONENT actions or -1 if this action isn't a OPPONENT action
     */
    @Override
    public int getActionIndexInOpponentActions() {
        if (isOpponentAction()) {
            return localIndex;
        } else {
            return -1;
        }
    }

    // Any number of your own methods
}
