package Exceptions;

public class PacManInvalidOpponentMoveException extends RuntimeException {
    public PacManInvalidOpponentMoveException(String s) {
        super(s);
    }
}
