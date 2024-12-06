package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.NegativeAmountException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.RemoveMoreThanCurrentBudgetException;
import cz.cvut.sem.ear.stepavi2.havriboh.main.exception.TargetAmountException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.lang.annotation.Target;
import java.math.BigDecimal;

@Entity
public class Budget extends AbstractEntity {

    @Column(name = "target_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @OneToOne
    private Category category;

    public Budget() {
    }

    public void increaseBudget(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
    }

    public void decreaseBudget(BigDecimal amount) {
        this.currentAmount = this.currentAmount.subtract(amount);
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
