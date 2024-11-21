package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NegativeAmountException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.RemoveMoreThanCurrentBudgetException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.TargetAmountException;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.lang.annotation.Target;
import java.math.BigDecimal;

@Entity
public class Budget extends AbstractEntity {

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String currency;

    @OneToOne
    private Category category;

    public void increaseBudget(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount must be positive");
        }
        if (currentAmount.add(amount).compareTo(targetAmount) > 0) {
            throw new TargetAmountException("Cannot increase the budget over the target amount.");
        }
        this.currentAmount = this.currentAmount.add(amount);
    }

    public void decreaseBudget(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount must be positive");
        }
        BigDecimal newAmount = currentAmount.subtract(amount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RemoveMoreThanCurrentBudgetException("Cannot remove more than the current budget.");
        } else {
            this.currentAmount = newAmount;
        }
    }

    @Override
    public String toString() {
        return "Budget{" +
                "targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", currency='" + currency + '\'' +
                ", category=" + category +
                '}';
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
