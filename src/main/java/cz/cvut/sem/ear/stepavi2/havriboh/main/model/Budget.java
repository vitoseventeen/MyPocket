package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Budget extends AbstractEntity {

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String currency;

    @OneToMany(mappedBy = "budget")
    private List<Category> categories;

    public void addBudget(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (currentAmount.add(amount).compareTo(targetAmount) > 0) {
            System.out.println("Warning: Exceeding target budget");
        }
        this.currentAmount = this.currentAmount.add(amount);
    }

    public void removeBudget(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        BigDecimal newAmount = currentAmount.subtract(amount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Warning: Removing more than current budget. Setting to zero.");
            this.currentAmount = BigDecimal.ZERO;
        } else {
            this.currentAmount = newAmount;
        }
    }

    public BigDecimal calculateRemainingBudget() {
        return targetAmount.subtract(currentAmount);
    }

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
