package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
