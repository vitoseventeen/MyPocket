package cz.cvut.fel.ear.stepavi2_havriboh.main.model;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UnsupportedCurrencyException;

import java.util.HashMap;
import java.util.Map;

public enum Currency {
    EUR("EUR"), USD("USD"), CZK("CZK");

    private final String name;
    private static final Map<String, Currency> NAME_TO_CURRENCY = new HashMap<>();

    static {
        for (Currency currency : values()) {
            NAME_TO_CURRENCY.put(currency.name, currency);
        }
    }

    Currency(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }


    public static Currency fromString(String name) {
        Currency currency = NAME_TO_CURRENCY.get(name);
        if (currency == null) {
            throw new UnsupportedCurrencyException("Unsupported currency: " + name);
        }
        return currency;
    }
}
