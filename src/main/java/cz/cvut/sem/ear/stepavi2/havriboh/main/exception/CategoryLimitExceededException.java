package cz.cvut.sem.ear.stepavi2.havriboh.main.exception;

public class CategoryLimitExceededException extends RuntimeException {
    public CategoryLimitExceededException(String message) {
        super(message);
    }
}
