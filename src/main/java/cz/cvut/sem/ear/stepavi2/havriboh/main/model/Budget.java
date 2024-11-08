package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class Budget extends AbstractEntity {

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String currency;

    public void updateBalance() {}

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
