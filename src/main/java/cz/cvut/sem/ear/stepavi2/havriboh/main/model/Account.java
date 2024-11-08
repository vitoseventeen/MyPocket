package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class Account extends AbstractEntity {
    private String accountName;
    private BigDecimal balance;
    private String currency;

    public void updateBalance() {};
}
