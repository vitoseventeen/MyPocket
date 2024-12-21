package cz.cvut.fel.ear.stepavi2_havriboh.main.model;

public enum Currency {
    EUR("EUR"), USD("USD"), CZK("CZK");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
