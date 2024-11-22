package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
