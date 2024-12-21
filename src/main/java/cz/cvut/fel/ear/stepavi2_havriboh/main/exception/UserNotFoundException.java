package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
