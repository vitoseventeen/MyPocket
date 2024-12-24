package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(String message) {
        super(message);
    }
}
