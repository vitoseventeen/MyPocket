package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

public enum Role {
    USER("USER"), PREMIUM("PREMIUM");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
