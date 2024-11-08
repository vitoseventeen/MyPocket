package cz.cvut.sem.ear.stepavi2.havriboh.main.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class Budget extends AbstractEntity {

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String currency;

    public void updateBalance() {}
}
