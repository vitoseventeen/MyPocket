package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class UserAlreadyInAccountException extends RuntimeException {
    public UserAlreadyInAccountException(String message) {
        super(message);
    }
}
