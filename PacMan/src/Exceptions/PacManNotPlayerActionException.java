package Exceptions;

public class PacManNotPlayerActionException extends RuntimeException {
    public PacManNotPlayerActionException(String not_player_action) {
        super(not_player_action);
    }
}
