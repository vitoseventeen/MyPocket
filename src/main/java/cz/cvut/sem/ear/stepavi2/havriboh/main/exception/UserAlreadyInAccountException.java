package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class UserAlreadyInAccountException extends RuntimeException {
    public UserAlreadyInAccountException(String message) {
        super(message);
    }
}
