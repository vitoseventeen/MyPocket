package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

public enum TransactionType {
    INCOME("INCOME"), EXPENSE("EXPENSE");

    private final String type;

    TransactionType(String name) {
        this.type = name;
    }

    @Override
    public String toString() {
        return type;
    }
}
