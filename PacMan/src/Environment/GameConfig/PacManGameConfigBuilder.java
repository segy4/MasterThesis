package Environment.GameConfig;

import Environment.State.PacManCell;
import Environment.State.PacManEnemyPosition;
import Environment.State.PacManEnemyType;
import Environment.State.PacManPlayerPosition;
import Exceptions.PacManInvalidTextRepresentationException;
import Mains.Instances.PacManGameInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(gameStringPath));
            String representation = new String(bytes);
            return new PacManGameConfig(maximalStepCountBound, bigBallReward, smallBallReward, stepPenalty, enemyType, createGameBoard(representation), enemyPositions, playerPosition, trapChance, oppositeMoveChance, randomMoveChance);
        } catch (IOException | PacManInvalidTextRepresentationException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean correctStringRepresentation(String[] lines) {
        if (lines.length == 0) {
            return false;
        }
        int firstSize = lines[0].length();
        for (String line : lines) {
            if (line.length() != firstSize) {
                return false;
            }
        }
        return true;
    }

    private List<List<PacManCell>> createGameBoard(String representation) throws PacManInvalidTextRepresentationException {
        String[] lines = representation.replace("\r\n", "\n").replace("\r", "\n").split("\\n");
        if (!correctStringRepresentation(lines)) {
            throw new PacManInvalidTextRepresentationException("There is a mistake with the map. It is not a rectangle");
        }
        List<List<PacManCell>> cells = new ArrayList<>();
        for (int i = 0; i < lines.length; ++i) {
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
