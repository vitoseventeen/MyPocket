package cz.cvut.fel.ear.stepavi2_havriboh.main.exception;

public class CategoryLimitExceededException extends RuntimeException {
    public CategoryLimitExceededException(String message) {
        super(message);
    }
}
