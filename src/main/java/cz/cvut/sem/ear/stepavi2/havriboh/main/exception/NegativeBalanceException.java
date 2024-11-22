package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(String message) {
        super(message);
    }
}
