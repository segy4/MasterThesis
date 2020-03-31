package Exceptions;

public class PacManInvalidPacManCellException extends RuntimeException {
    public PacManInvalidPacManCellException(String invalid_cell_in_gameBoard) {
        super(invalid_cell_in_gameBoard);
    }
}
