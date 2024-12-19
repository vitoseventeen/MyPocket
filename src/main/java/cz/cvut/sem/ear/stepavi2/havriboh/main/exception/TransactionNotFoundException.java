package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
