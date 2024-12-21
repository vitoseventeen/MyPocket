package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
