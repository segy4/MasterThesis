package Environment.Action;

import vahy.api.model.Action;

import java.util.Arrays;
import java.util.Comparator;

public enum PacManAction implements Action {
    UP(true, true, 0, 0),
    DOWN(true, true, 1, 1),
    LEFT(true, true, 2, 2),
    RIGHT(true, true, 3, 3),
    NO_ACTION(false, true, 4, 4),

    TRAP(false, true, 5, 5),
    OPPOSITE_MOVE(false, true, 6, 6),
    RANDOM_MOVE(false, true, 7, 7);
    

    public static PacManAction[] playerActions = Arrays.stream(PacManAction.values()).filter(PacManAction::isPlayerAction).sorted(Comparator.comparing(PacManAction::getActionIndexInPlayerActions)).toArray(PacManAction[]::new);
    public static PacManAction[] opponentActions = Arrays.stream(PacManAction.values()).filter(PacManAction::isOpponentAction).sorted(Comparator.comparing(PacManAction::getActionIndexInPlayerActions)).toArray(PacManAction[]::new);
    private boolean isPlayerAction;
    private boolean isOpponentAction;
    private int localIndex;
    private int globalIndex;

    PacManAction(boolean playerAction, boolean opponentAction, int locIndex, int gloIndex) {
        isPlayerAction = playerAction;
        isOpponentAction = opponentAction;
        localIndex = locIndex;
        globalIndex = gloIndex;
    }

    @Override
    public Action[] getAllPlayerActions() {
        return playerActions;
    }

    @Override
    public Action[] getAllOpponentActions() {
        return opponentActions;
    }

    @Override
    public boolean isPlayerAction() {
        return isPlayerAction;
    }

    @Override
    public boolean isOpponentAction() {
        return isOpponentAction;
    }

    @Override
    public int getGlobalIndex() {
        return globalIndex;
    }

    @Override
    public int getActionIndexInPlayerActions() {
        if (isPlayerAction) {
            return localIndex;
        }
        return -1;
    }

    @Override
    public int getActionIndexInOpponentActions() {
        if (isOpponentAction) {
            return localIndex;
        }
        return -1;
    }
}
