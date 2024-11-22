package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
