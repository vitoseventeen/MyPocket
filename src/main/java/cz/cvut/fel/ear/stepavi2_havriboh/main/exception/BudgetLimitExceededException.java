package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class BudgetLimitExceededException extends RuntimeException {
    public BudgetLimitExceededException(String message) {
        super(message);
    }
}
