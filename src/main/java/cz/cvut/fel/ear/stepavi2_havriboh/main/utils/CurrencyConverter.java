package cz.cvut.fel.ear.stepavi2_havriboh.main.utils;

import cz.cvut.fel.ear.stepavi2_havriboh.main.exception.UnsupportedCurrencyException;

import java.math.BigDecimal;

public class CurrencyConverter {
    public static BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        return amount.multiply(exchangeRate);
    }

    private static BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        // Example: EUR -> USD = 1.1, USD -> EUR = 0.9, etc.
        if (fromCurrency.equals("EUR") && toCurrency.equals("USD")) {
            return new BigDecimal("1.1");
        }
        if (fromCurrency.equals("USD") && toCurrency.equals("EUR")) {
            return new BigDecimal("0.9");
        }
        if (fromCurrency.equals("EUR") && toCurrency.equals("CZK")) {
            return new BigDecimal("25.5");
        }
        if (fromCurrency.equals("CZK") && toCurrency.equals("EUR")) {
            return new BigDecimal("0.039");
        }
        if (fromCurrency.equals("USD") && toCurrency.equals("CZK")) {
            return new BigDecimal("23.2");
        }
        if (fromCurrency.equals("CZK") && toCurrency.equals("USD")) {
            return new BigDecimal("0.043");
        }
        throw new UnsupportedCurrencyException("Unsupported currency pair: " + fromCurrency + " -> " + toCurrency);
    }
}