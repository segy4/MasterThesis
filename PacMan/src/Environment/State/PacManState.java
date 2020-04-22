package Environment.State;

import Environment.Action.PacManAction;
import Environment.Policy.PacManEnvironmentProbabilities;
import Exceptions.*;
import jdk.jshell.spi.ExecutionControl;
import org.apache.commons.lang3.NotImplementedException;
import vahy.api.model.StateRewardReturn;
import vahy.impl.model.ImmutableStateRewardReturnTuple;
import vahy.impl.model.observation.DoubleVector;
import vahy.paperGenerics.PaperState;
import vahy.utils.ImmutableTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PacManState implements PaperState<PacManAction, DoubleVector, PacManEnvironmentProbabilities, PacManState> {

    private static final int PLAYER_COORDS = 2;

    private static final int NOTHING = -1;
    private static final int SMALL_BALL = -2;
    private static final int BIG_BALL = -3;
    private static final int ENEMY = -4;
    private static final int WALL = -5;
    private static final int TRAP = -6;

    private final boolean isAgentTurn;
    private final boolean isAgentKilled;
    private final PacManPlayerPosition playerPosition;
    private final PacManPlayerPosition beforePlayerPosition;
    private final List<PacManEnemyPosition> enemyPositions;
    private final int enemyIndexOnMove;
    private final List<List<PacManCell>> gameBoard;

    private final boolean noisyMoveRight;
    private final boolean noisyMoveLeft;

    private PacManStaticPart staticPart;

    public PacManState(boolean isAgentTurn,
                       boolean isAgentKilled,
                       PacManPlayerPosition playerPosition,
                       PacManPlayerPosition beforePlayerPosition,
                       List<PacManEnemyPosition> enemyPositions,
                       int enemyIndexOnMove,
                       List<List<PacManCell>> gameBoard,
                       boolean noisyMoveRight,
                       boolean noisyMoveLeft,
                       PacManStaticPart staticPart) {
        this.isAgentTurn = isAgentTurn;
        this.isAgentKilled = isAgentKilled;
        this.playerPosition = playerPosition;
        this.beforePlayerPosition = beforePlayerPosition;
        this.enemyPositions = enemyPositions;
        this.enemyIndexOnMove = enemyIndexOnMove;
        this.gameBoard = gameBoard;
        this.noisyMoveRight = noisyMoveRight;
        this.noisyMoveLeft = noisyMoveLeft;
        this.staticPart = staticPart;
    }

    @Override
    public boolean isRiskHit() {
        return isAgentKilled;
    }

    @Override
    public PacManAction[] getAllPossibleActions() {
        if (isAgentTurn) {
            return getPossiblePlayerActions();
        } else {
            return getPossibleOpponentActions();
        }
    }

    @Override
    public PacManAction[] getPossiblePlayerActions() {
        if (isAgentTurn) {
            return PacManAction.playerActions;
        } else {
            return new PacManAction[0];
        }
    }

    @Override
    public PacManAction[] getPossibleOpponentActions() {
        if (!isAgentTurn) {
            return getOpponentObservation().getProbabilities().getFirst().toArray(new PacManAction[0]);
        } else {
            return new PacManAction[0];
        }
    }

    private boolean canMove(PacManEnemyPosition pos, PacManAction action) {
        switch (action) {
            case NO_ACTION:
                return true;
            case UP:
                return gameBoard.get(pos.getX() - 1).get(pos.getY()) != PacManCell.WALL;
            case DOWN:
                return gameBoard.get(pos.getX() + 1).get(pos.getY()) != PacManCell.WALL;
            case LEFT:
                return gameBoard.get(pos.getX()).get(pos.getY() - 1) != PacManCell.WALL;
            case RIGHT:
                return gameBoard.get(pos.getX()).get(pos.getY() + 1) != PacManCell.WALL;
        }
        throw new PacManNotActionForMovingException("Not action for moving. " + action.toString());
    }

    private double changeCell(PacManAction action, List<List<PacManCell>> cells) {
        int xCoord = playerPosition.getX();
        int yCoord = playerPosition.getY();
        switch (action) {
            case NO_ACTION:
                throw new PacManNotPlayerActionException("Not player action. " + action.toString());
            case UP:
                xCoord = xCoord - 1;
                break;
            case DOWN:
                xCoord = xCoord + 1;
                break;
            case LEFT:
                yCoord = yCoord - 1;
                break;
            case RIGHT:
                yCoord = yCoord + 1;
                break;
        }
        switch (cells.get(xCoord).get(yCoord)) {
            case SMALL_BALL:
                cells.get(xCoord).set(yCoord, PacManCell.NOTHING);
                return staticPart.getSmallBallReward();
            case BIG_BALL:
                cells.get(xCoord).set(yCoord, PacManCell.NOTHING);
                return staticPart.getBigBallReward();
            case NOTHING:
            case WALL:
            case TRAP:
                return 0.0;
        }
        throw new PacManInvalidPacManCellException("Invalid cell in gameBoard. " + cells.get(xCoord).get(yCoord));
    }

    private StateRewardReturn<PacManAction, DoubleVector, PacManEnvironmentProbabilities, PacManState> playerTurn(PacManAction action) {
        List<List<PacManCell>> newCells = copyGameBoard();
        double reward = -staticPart.getStepPenalty();
        PacManPlayerPosition newPosition = new PacManPlayerPosition(playerPosition);
        switch (action) {
            case UP:
                if (canMove(playerPosition, PacManAction.UP)) {
                    reward += changeCell(PacManAction.UP, newCells);
                    newPosition.setX(playerPosition.getX() - 1);
                }
                break;
            case DOWN:
                if (canMove(playerPosition, PacManAction.DOWN)) {
                    reward += changeCell(PacManAction.DOWN, newCells);
                    newPosition.setX(playerPosition.getX() + 1);
                }
                break;
            case LEFT:
                if (canMove(playerPosition, PacManAction.LEFT)) {
                    reward += changeCell(PacManAction.LEFT, newCells);
                    newPosition.setY(playerPosition.getY() - 1);
                }
                break;
            case RIGHT:
                if (canMove(playerPosition, PacManAction.RIGHT)) {
                    reward += changeCell(PacManAction.RIGHT, newCells);
                    newPosition.setY(playerPosition.getY() + 1);
                }
                break;
            case NO_ACTION:
            case TRAP:
            case RANDOM_MOVE:
            case OPPOSITE_MOVE:
                throw new PacManNotPlayerActionException("Not player action. " + action.toString());
        }
        if (noisyMoveLeft) {
            if (canMove(newPosition, PacManAction.LEFT)) {
                reward += changeCell(PacManAction.LEFT, newCells);
                newPosition.setY(newPosition.getY() - 1);
            }
        }
        if (noisyMoveRight) {
            if (canMove(newPosition, PacManAction.RIGHT)) {
                reward += changeCell(PacManAction.RIGHT, newCells);
                newPosition.setY(newPosition.getY() - 1);
            }
        }
        return new ImmutableStateRewardReturnTuple<>(
                new PacManState(!isAgentTurn,
                        isAgentKilled,
                        newPosition,
                        playerPosition,
                        new ArrayList<>(enemyPositions),
                        0,
                        newCells,
                        false,
                        false,
                        staticPart),
                reward);
    }

    private PacManAction pickRandomPlayerOption() {
        return PacManAction.playerActions[staticPart.getRandom().nextInt(0, PacManAction.playerActions.length)];
    }

    private StateRewardReturn<PacManAction, DoubleVector, PacManEnvironmentProbabilities, PacManState> opponentTurn(PacManAction action, int enemyIndex) {
        if (staticPart.getEnemyType().equals(PacManEnemyType.TRAPS)) {
            switch (action) {
                case TRAP:
                    return new ImmutableStateRewardReturnTuple<>(
                            new PacManState(!isAgentTurn,
                                    true,
                                    playerPosition,
                                    playerPosition,
                                    new ArrayList<>(enemyPositions),
                                    0,
                                    copyGameBoard(),
                                    false,
                                    false,
                                    staticPart),
                            0.0);
                case OPPOSITE_MOVE:
                    return new ImmutableStateRewardReturnTuple<>(
                            new PacManState(!isAgentTurn,
                                    isAgentKilled,
                                    beforePlayerPosition,
                                    playerPosition,
                                    new ArrayList<>(enemyPositions),
                                    0,
                                    copyGameBoard(),
                                    false,
                                    false,
                                    staticPart),
                            -staticPart.getStepPenalty());
                case RANDOM_MOVE:
                    return playerTurn(pickRandomPlayerOption());
                case NO_ACTION:
                    return new ImmutableStateRewardReturnTuple<>(
                            new PacManState(!isAgentTurn,
                                    isAgentKilled,
                                    playerPosition,
                                    playerPosition,
                                    new ArrayList<>(enemyPositions),
                                    0,
                                    copyGameBoard(),
                                    false,
                                    false,
                                    staticPart),
                            0.0);
                case NOISY_MOVE_LEFT:
                case NOISY_MOVE_RIGHT:
                case RIGHT:
                case LEFT:
                case DOWN:
                case UP:
                    throw new PacManInvalidOpponentMoveException("These are for game with enemies. " + action.toString());
            }
        } else if (staticPart.getEnemyType().equals(PacManEnemyType.NOISY_MOVE)) {
            switch (action) {
                case TRAP:
                    return new ImmutableStateRewardReturnTuple<>(
                            new PacManState(!isAgentTurn,
                                    true,
                                    playerPosition,
                                    playerPosition,
                                    new ArrayList<>(enemyPositions),
                                    0,
                                    copyGameBoard(),
                                    false,
                                    false,
                                    staticPart),
                            0.0);
                case NOISY_MOVE_LEFT:
                    return new ImmutableStateRewardReturnTuple<>(
                            new PacManState(!isAgentTurn,
                                    true,
                                    playerPosition,
                                    playerPosition,
                                    new ArrayList<>(enemyPositions),
                                    0,
                                    copyGameBoard(),
                                    false,
                                    true,
                                    staticPart),
                            0.0);
                case NOISY_MOVE_RIGHT:
                    return new ImmutableStateRewardReturnTuple<>(
                            new PacManState(!isAgentTurn,
                                    true,
                                    playerPosition,
                                    playerPosition,
                                    new ArrayList<>(enemyPositions),
                                    0,
                                    copyGameBoard(),
                                    true,
                                    false,
                                    staticPart),
                            0.0);
                case NO_ACTION:
                    return new ImmutableStateRewardReturnTuple<>(
                            new PacManState(!isAgentTurn,
                                    isAgentKilled,
                                    playerPosition,
                                    playerPosition,
                                    new ArrayList<>(enemyPositions),
                                    0,
                                    copyGameBoard(),
                                    false,
                                    false,
                                    staticPart),
                            0.0);
                case OPPOSITE_MOVE:
                case RANDOM_MOVE:
                case RIGHT:
                case LEFT:
                case DOWN:
                case UP:
                    throw new PacManInvalidOpponentMoveException("These are for game with enemies. " + action.toString());
            }
        } else {
            List<PacManEnemyPosition> newEnemyPositions = new ArrayList<>(enemyPositions);
            PacManEnemyPosition newEnemyPosition = enemyPositions.get(enemyIndex);
            switch (action) {
                case NO_ACTION:
                    break;
                case UP:
                    if (canMove(newEnemyPosition, PacManAction.UP)) {
                        newEnemyPosition = new PacManEnemyPosition(newEnemyPosition.getX() - 1, newEnemyPosition.getY());
                    }
                    break;
                case DOWN:
                    if (canMove(newEnemyPosition, PacManAction.DOWN)) {
                        newEnemyPosition = new PacManEnemyPosition(newEnemyPosition.getX() + 1, newEnemyPosition.getY());
                    }
                    break;
                case LEFT:
                    if (canMove(newEnemyPosition, PacManAction.LEFT)) {
                        newEnemyPosition = new PacManEnemyPosition(newEnemyPosition.getX(), newEnemyPosition.getY() - 1);
                    }
                    break;
                case RIGHT:
                    if (canMove(newEnemyPosition, PacManAction.RIGHT)) {
                        newEnemyPosition = new PacManEnemyPosition(newEnemyPosition.getX(), newEnemyPosition.getY() + 1);
                    }
                    break;
                case NOISY_MOVE_LEFT:
                case NOISY_MOVE_RIGHT:
                case TRAP:
                case RANDOM_MOVE:
                case OPPOSITE_MOVE:
                    throw new PacManInvalidOpponentMoveException("This is meant for with traps game. " + action.toString());
            }
            newEnemyPositions.set(enemyIndex, newEnemyPosition);
            return new ImmutableStateRewardReturnTuple<>(
                    new PacManState((enemyIndexOnMove + 1) == enemyPositions.size(),
                            playerPosition.equals(newEnemyPosition),
                            playerPosition,
                            beforePlayerPosition,
                            newEnemyPositions,
                            (enemyIndexOnMove + 1) == enemyPositions.size() ? 0 : enemyIndexOnMove + 1,
                            gameBoard,
                            false,
                            false,
                            staticPart),
                    0.0);
        }
        throw new PacManInvalidOpponentMoveException("Never seen move like this. " + action.toString());
    }

    @Override
    public StateRewardReturn<PacManAction, DoubleVector, PacManEnvironmentProbabilities, PacManState> applyAction(PacManAction actionType) {
        if (isAgentTurn) {
            return playerTurn(actionType);
        } else {
            return opponentTurn(actionType, enemyIndexOnMove);
        }
    }

    private List<List<PacManCell>> copyGameBoard() {
        List<List<PacManCell>> newCells = new ArrayList<>();
        for (List<PacManCell> row : gameBoard) {
            newCells.add(new ArrayList<>(row));
        }
        return newCells;
    }

    @Override
    public PacManState deepCopy() {
        List<List<PacManCell>> newCells = copyGameBoard();
        return new PacManState(isAgentTurn,
                isAgentKilled,
                playerPosition,
                beforePlayerPosition,
                new ArrayList<>(enemyPositions),
                enemyIndexOnMove,
                newCells,
                noisyMoveRight,
                noisyMoveLeft,
                staticPart);
    }

    private int getPlayerObservation(int x, int y) {
        for (PacManEnemyPosition enemyPosition : enemyPositions) {
            if (enemyPosition.isSame(x, y)) {
                return ENEMY;
            }
        }
        switch (gameBoard.get(x).get(y)) {
            case TRAP:
                return TRAP;
            case NOTHING:
                return NOTHING;
            case WALL:
                return WALL;
            case SMALL_BALL:
                return SMALL_BALL;
            case BIG_BALL:
                return BIG_BALL;
        }
        throw new PacManInvalidPacManCellException("There exist a new type of cell. " + gameBoard.get(x).get(y));
    }

    @Override
    public DoubleVector getPlayerObservation() {
        double[] observation = new double[gameBoard.size()*gameBoard.get(0).size() + PLAYER_COORDS];
        int yLength = gameBoard.get(0).size();
        for (int x = 0; x < gameBoard.size(); ++x) {
            for (int y = 0; y < yLength; ++y) {
                observation[x*gameBoard.size() + y] = getPlayerObservation(x, y);
            }
        }
        observation[observation.length - PLAYER_COORDS] = playerPosition.getX();
        observation[observation.length - PLAYER_COORDS + 1] = playerPosition.getY();
        return new DoubleVector(observation);
    }

    private double getTrapActions(List<PacManAction> actions, List<Double> probabilities) {
        double returnSum = 0.0;
        if (gameBoard.get(playerPosition.getX()).get(playerPosition.getY()).equals(PacManCell.TRAP)) {
            actions.add(PacManAction.TRAP);
            probabilities.add(staticPart.getTrapProbability());
            returnSum += staticPart.getTrapProbability();
        }
        if (returnSum + staticPart.getOppositeMoveProbability() < 1.0) {
            actions.add(PacManAction.OPPOSITE_MOVE);
            probabilities.add(staticPart.getOppositeMoveProbability());
            returnSum += staticPart.getOppositeMoveProbability();
        }
        if (returnSum + staticPart.getRandomMoveProbability() < 1.0) {
            actions.add(PacManAction.RANDOM_MOVE);
            probabilities.add(staticPart.getRandomMoveProbability());
            returnSum += staticPart.getRandomMoveProbability();
        }
        return returnSum;
    }

    private double getNoisyMoveActions(List<PacManAction> actions, List<Double> probabilities) {
        double returnSum = 0.0;
        if (gameBoard.get(playerPosition.getX()).get(playerPosition.getY()).equals(PacManCell.TRAP)) {
            actions.add(PacManAction.TRAP);
            probabilities.add(staticPart.getTrapProbability());
            returnSum += staticPart.getTrapProbability();
        }
        if (returnSum + staticPart.getNoisyMoveProbability() < 1.0) {
            actions.add(PacManAction.NOISY_MOVE_RIGHT);
            probabilities.add(staticPart.getNoisyMoveProbability());
            returnSum += staticPart.getNoisyMoveProbability();
        }
        if (returnSum + staticPart.getNoisyMoveProbability() < 1.0) {
            actions.add(PacManAction.NOISY_MOVE_LEFT);
            probabilities.add(staticPart.getNoisyMoveProbability());
            returnSum += staticPart.getNoisyMoveProbability();
        }
        return returnSum;
    }

    private ImmutableTuple<int[], Integer> getDistancesAndMin() {
        int[] distances = new int[4];
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < gameBoard.size(); ++i) {
            if (enemyPositions.get(enemyIndexOnMove).isSame(playerPosition.getX() + 1, playerPosition.getY())) {
                distances[0] = i;
                min = i;
                break;
            }
            if (gameBoard.get(enemyPositions.get(enemyIndexOnMove).getX() - 1).get(enemyPositions.get(enemyIndexOnMove).getY()).equals(PacManCell.WALL)) {
                distances[0] = Integer.MAX_VALUE;
                min = i;
                break;
            }
        }
        for (int i = 0; i < gameBoard.size(); ++i) {
            if (enemyPositions.get(enemyIndexOnMove).isSame(playerPosition.getX() - 1, playerPosition.getY())) {
                distances[1] = i;
                if (min > i) {
                    min = i;
                }
                break;
            }
            if (gameBoard.get(enemyPositions.get(enemyIndexOnMove).getX() + 1).get(enemyPositions.get(enemyIndexOnMove).getY()).equals(PacManCell.WALL)) {
                distances[1] = Integer.MAX_VALUE;
                if (min > i) {
                    min = i;
                }
                break;
            }
        }
        for (int i = 0; i < gameBoard.get(enemyPositions.get(enemyIndexOnMove).getX()).size(); ++i) {
            if (enemyPositions.get(enemyIndexOnMove).isSame(playerPosition.getX(), playerPosition.getY() + 1)) {
                distances[2] = i;
                if (min > i) {
                    min = i;
                }
                break;
            }
            if (gameBoard.get(enemyPositions.get(enemyIndexOnMove).getX()).get(enemyPositions.get(enemyIndexOnMove).getY() - 1).equals(PacManCell.WALL)) {
                distances[2] = Integer.MAX_VALUE;
                if (min > i) {
                    min = i;
                }
                break;
            }
        }
        for (int i = 0; i < gameBoard.get(enemyPositions.get(enemyIndexOnMove).getX()).size(); ++i) {
            if (enemyPositions.get(enemyIndexOnMove).isSame(playerPosition.getX(), playerPosition.getY() - 1)) {
                distances[3] = i;
                if (min > i) {
                    min = i;
                }
                break;
            }
            if (gameBoard.get(enemyPositions.get(enemyIndexOnMove).getX()).get(enemyPositions.get(enemyIndexOnMove).getY() + 1).equals(PacManCell.WALL)) {
                distances[3] = Integer.MAX_VALUE;
                if (min > i) {
                    min = i;
                }
                break;
            }
        }
        return new ImmutableTuple<>(distances, min);
    }

    private List<PacManAction> getVisibleDirections() {
        List<PacManAction> directions = new ArrayList<>();
        ImmutableTuple<int[], Integer> distancesAndMin = getDistancesAndMin();
        for (int i = 0; i < 4; ++i) {
            if (distancesAndMin.getFirst()[i] == distancesAndMin.getSecond()) {
                directions.add(PacManAction.values()[i]);
            }
        }
        return directions;
    }

    private double getWhenVisibleChaseActions(List<PacManAction> actions, List<Double> probabilities) {
        double returnSum = 1.0 - staticPart.getRandomMoveProbability();
        List<PacManAction> directions = getVisibleDirections();
        for (PacManAction action : directions) {
            actions.add(action);
            probabilities.add(returnSum / directions.size());
        }
        return returnSum;
    }

    private double getBFSShortestPathActions(List<PacManAction> actions, List<Double> probabilities) {
        throw new NotImplementedException("Not implemented.");
    }

    @Override
    public PacManEnvironmentProbabilities getOpponentObservation() {
        if (isAgentTurn) {
            return new PacManEnvironmentProbabilities(new ImmutableTuple<>(new ArrayList<>(), new ArrayList<>()));
        }
        List<PacManAction> possibleActions = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        double sumProbabilities = 0.0;
        switch (staticPart.getEnemyType()) {
            case TRAPS:
                sumProbabilities = getTrapActions(possibleActions, probabilities);
                break;
            case NOISY_MOVE:
                sumProbabilities = getNoisyMoveActions(possibleActions, probabilities);
                break;
            case WHEN_VISIBLE_CHASE:
                sumProbabilities = getWhenVisibleChaseActions(possibleActions, probabilities);
                break;
            case BFS_SHORTEST_PATH:
                sumProbabilities = getBFSShortestPathActions(possibleActions, probabilities);
                break;
        }
        if (sumProbabilities > 1.0) {
            throw new PacManInvalidOpponentObservationException("Sum of all probabilities can't be more than 1.0.");
        }
        possibleActions.add(PacManAction.NO_ACTION);
        probabilities.add(1.0 - sumProbabilities);
        return new PacManEnvironmentProbabilities(new ImmutableTuple<>(possibleActions, probabilities));
    }

    @Override
    public String readableStringRepresentation() {
        if (isAgentKilled) {
            return "Agent is dead!";
        }
        StringBuilder returnString = new StringBuilder();
        boolean addedE = false;
        for (int i = 0; i < gameBoard.size(); ++i) {
            for (int j = 0; j < gameBoard.get(i).size(); ++j) {
                if (playerPosition.isSame(i, j)) {
                    returnString.append('P');
                    continue;
                }
                for (PacManEnemyPosition enemyPosition : enemyPositions) {
                    if (enemyPosition.isSame(i, j)) {
                        returnString.append('E');
                        addedE = true;
                        break;
                    }
                }
                if (addedE) {
                    addedE = false;
                    continue;
                }
                switch (gameBoard.get(i).get(j)) {
                    case WALL:
                        returnString.append('W');
                        break;
                    case NOTHING:
                        returnString.append(' ');
                        break;
                    case BIG_BALL:
                        returnString.append('@');
                        break;
                    case SMALL_BALL:
                        returnString.append('*');
                        break;
                }
            }
            returnString.append('\n');
        }
        return returnString.toString();
    }

    @Override
    public List<String> getCsvHeader() {
        List<String> csvHeader = new ArrayList<>();
        csvHeader.add("Is agent killed:");
        csvHeader.add("Is agent turn: ");
        csvHeader.add("Player position: ");
        csvHeader.add("Number of enemies: ");
        for (int i = 0; i < enemyPositions.size(); ++i) {
            csvHeader.add(String.valueOf(i+1) + ". enemy position: ");
        }
        return csvHeader;
    }

    @Override
    public List<String> getCsvRecord() {
        List<String> csvHeader = new ArrayList<>();
        csvHeader.add(String.valueOf(isAgentKilled));
        csvHeader.add(String.valueOf(isAgentTurn));
        csvHeader.add(playerPosition.toString());
        csvHeader.add(String.valueOf(enemyPositions.size()));
        for (PacManEnemyPosition enemyPosition : enemyPositions) {
            csvHeader.add(enemyPosition.toString());
        }
        return csvHeader;
    }

    @Override
    public boolean isOpponentTurn() {
        return !isAgentTurn;
    }

    private boolean noMoreRewards() {
        for (List<PacManCell> row : gameBoard) {
            for (PacManCell cell : row) {
                if (cell.equals(PacManCell.BIG_BALL) || cell.equals(PacManCell.SMALL_BALL)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isFinalState() {
        return isAgentKilled || noMoreRewards();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacManState that = (PacManState) o;
        return isAgentTurn == that.isAgentTurn &&
                isAgentKilled == that.isAgentKilled &&
                enemyIndexOnMove == that.enemyIndexOnMove &&
                noisyMoveRight == that.noisyMoveRight &&
                noisyMoveLeft == that.noisyMoveLeft &&
                Objects.equals(playerPosition, that.playerPosition) &&
                Objects.equals(beforePlayerPosition, that.beforePlayerPosition) &&
                Objects.equals(enemyPositions, that.enemyPositions) &&
                Objects.equals(gameBoard, that.gameBoard) &&
                Objects.equals(staticPart, that.staticPart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isAgentTurn, isAgentKilled, playerPosition, beforePlayerPosition, enemyPositions, enemyIndexOnMove, gameBoard, noisyMoveRight, noisyMoveLeft, staticPart);
    }
}
