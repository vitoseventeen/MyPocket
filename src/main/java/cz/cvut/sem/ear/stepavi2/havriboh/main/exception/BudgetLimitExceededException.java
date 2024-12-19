package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class BudgetLimitExceededException extends RuntimeException {
    public BudgetLimitExceededException(String message) {
        super(message);
    }
}
