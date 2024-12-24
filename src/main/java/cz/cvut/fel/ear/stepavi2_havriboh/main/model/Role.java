package cz.cvut.fel.ear.stepavi2_havriboh.main.model;

public enum Role {
    USER("USER"), PREMIUM("PREMIUM"), ADMIN("ADMIN");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
