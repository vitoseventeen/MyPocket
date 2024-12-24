package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
