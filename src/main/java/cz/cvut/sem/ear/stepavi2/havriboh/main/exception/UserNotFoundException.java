package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
