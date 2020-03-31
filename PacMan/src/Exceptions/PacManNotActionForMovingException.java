package Exceptions;

public class PacManNotActionForMovingException extends RuntimeException {
    public PacManNotActionForMovingException(String s) {
        super(s);
    }
}
