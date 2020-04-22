package Environment.GameConfig;

import Environment.State.PacManCell;
import Environment.State.PacManEnemyPosition;
import Environment.State.PacManEnemyType;
import Environment.State.PacManPlayerPosition;
import Exceptions.PacManInvalidTextRepresentationException;
import Mains.Instances.PacManGameInstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PacManGameConfigBuilder {

    private int maximalStepCountBound = 1000;
    private double bigBallReward = 50.0;
    private double smallBallReward = 1.0;
    private double stepPenalty = 1.0;
    private PacManEnemyType enemyType = PacManEnemyType.TRAPS;
    private String gameStringPath = "";
    private List<PacManEnemyPosition> enemyPositions = new ArrayList<>();
    private PacManPlayerPosition playerPosition = null;
    private double trapChance = 0.0;
    private double noisyMoveChance = 0.0;
    private double oppositeMoveChance = 0.0;
    private double randomMoveChance = 0.0;

    public PacManGameConfigBuilder maximalStepCountBound(int maximalStepCountBound) {
        this.maximalStepCountBound = maximalStepCountBound;
        return this;
    }

    public PacManGameConfigBuilder bigBallReward(double bigBallReward) {
        this.bigBallReward = bigBallReward;
        return this;
    }

    public PacManGameConfigBuilder smallBallReward(double smallBallReward) {
        this.smallBallReward = smallBallReward;
        return this;
    }

    public PacManGameConfigBuilder stepPenalty(double stepPenalty) {
        this.stepPenalty = stepPenalty;
        return this;
    }

    public PacManGameConfigBuilder enemyType(PacManEnemyType enemyType) {
        this.enemyType = enemyType;
        return this;
    }

    public PacManGameConfigBuilder gameString(PacManGameInstance gameInstance) {
        this.gameStringPath = gameInstance.getPath();
        return this;
    }

    public PacManGameConfigBuilder trapChance(double trapChance) {
        this.trapChance = trapChance;
        return this;
    }

    public PacManGameConfigBuilder noisyMoveChance(double noisyMoveChance) {
        this.noisyMoveChance = noisyMoveChance;
        return this;
    }

    public PacManGameConfigBuilder oppositeMoveChance(double oppositeMoveChance) {
        this.oppositeMoveChance = oppositeMoveChance;
        return this;
    }

    public PacManGameConfigBuilder randomMoveChance(double randomMoveChance) {
        this.randomMoveChance = randomMoveChance;
        return this;
    }

    public PacManGameConfig build() {
        if (gameStringPath.equals("")) {
            throw new RuntimeException("No game instance selected.");
        }
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(gameStringPath);
        try {
            byte[] bytes = resourceAsStream.readAllBytes();
            String representation = new String(bytes);
            String[] lines = representation.replace("\r\n", "\n").replace("\r", "\n").split("\\n");
            adjustParametersFromFile(lines);
            return new PacManGameConfig(maximalStepCountBound, bigBallReward, smallBallReward, stepPenalty, enemyType, createGameBoard(lines), enemyPositions, playerPosition, trapChance, noisyMoveChance, oppositeMoveChance, randomMoveChance);
        } catch (IOException | PacManInvalidTextRepresentationException e) {
            throw new RuntimeException(e);
        }
    }

    private PacManEnemyType parseEnemyType(String type) throws PacManInvalidTextRepresentationException {
        if (type.equals("TRAP")) {
            return PacManEnemyType.TRAPS;
        }
        if (type.equals("NOISY")) {
            return PacManEnemyType.NOISY_MOVE;
        }
        if (type.equals("CHASE")) {
            return PacManEnemyType.WHEN_VISIBLE_CHASE;
        }
        if (type.equals("BFS")) {
            return PacManEnemyType.BFS_SHORTEST_PATH;
        }
        throw new PacManInvalidTextRepresentationException("Wrong parameters line. Wrong name for EnemyType. Look at README for PacMan to see correct ones.");
    }

    private void adjustParametersFromFile(String[] lines) throws PacManInvalidTextRepresentationException {
        String firstLine = lines[0];
        if (firstLine.equals("")) {
            return;
        }
        String[] parameters = firstLine.split(";");
        for (String parameter : parameters) {
            String[] splitted = parameter.split(":");
            if (splitted.length != 2) {
                throw new PacManInvalidTextRepresentationException("Wrong parameters line. Either too many arguments in one parameter or too little.");
            }
            if (splitted[0].length() != 1) {
                throw new PacManInvalidTextRepresentationException("Wrong parameters line. Too many letters in parameters. Look at README for PacMan to see correct ones.");
            }
            switch (splitted[0].charAt(0)) {
                case 'M':
                    maximalStepCountBound = Integer.parseInt(splitted[1]);
                    break;
                case 'S':
                    stepPenalty = Integer.parseInt(splitted[1]);
                    break;
                case '@':
                    bigBallReward = Double.parseDouble(splitted[1]);
                    break;
                case '*':
                    smallBallReward = Double.parseDouble(splitted[1]);
                    break;
                case 'E':
                    enemyType = parseEnemyType(splitted[1]);
                    break;
                case 'T':
                    trapChance = Double.parseDouble(splitted[1]);
                    break;
                case 'N':
                    noisyMoveChance = Double.parseDouble(splitted[1]);
                    break;
                case 'O':
                    oppositeMoveChance = Double.parseDouble(splitted[1]);
                    break;
                case 'R':
                    randomMoveChance = Double.parseDouble(splitted[1]);
                    break;
                default:
                    throw new PacManInvalidTextRepresentationException("Wrong parameters line. Wrong parameter name. Look at README for PacMan to see correct ones. " + splitted[1].charAt(0));
            }
        }
    }

    private boolean correctStringRepresentation(String[] lines) {
        if (lines.length == 0) {
            return false;
        }
        int firstSize = lines[1].length();
        for (int i = 1; i < lines.length; ++i) {
            if (lines[i].length() != firstSize) {
                return false;
            }
        }
        return true;
    }

    private List<List<PacManCell>> createGameBoard(String[] lines) throws PacManInvalidTextRepresentationException {
        if (!correctStringRepresentation(lines)) {
            throw new PacManInvalidTextRepresentationException("There is a mistake with the map. It is not a rectangle");
        }
        
        List<List<PacManCell>> cells = new ArrayList<>();
        for (int i = 1; i < lines.length; ++i) {
            List<PacManCell> lineCells = new ArrayList<>();
            for (int j = 0; j < lines[i].length(); ++j) {
                lineCells.add(createCell(lines[i].charAt(j), i, j));
            }
            cells.add(lineCells);
        }
        return cells;
    }

    private PacManCell createCell(char charAt, int xIndex, int yIndex) throws PacManInvalidTextRepresentationException {
        switch (charAt) {
            case 'W':
                return PacManCell.WALL;
            case '*':
                return PacManCell.SMALL_BALL;
            case '@':
                return PacManCell.BIG_BALL;
            case 'E':
                enemyPositions.add(new PacManEnemyPosition(xIndex, yIndex));
                return PacManCell.NOTHING;
            case 'P':
                playerPosition = new PacManPlayerPosition(xIndex, yIndex);
                return PacManCell.NOTHING;
            case 'T':
                return PacManCell.TRAP;
            case ' ':
                return PacManCell.NOTHING;
            default:
                throw new PacManInvalidTextRepresentationException("Instance created with bad letters. '" + charAt + "' shouldn't be there.");
        }
    }
}
