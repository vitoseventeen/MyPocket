package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Account extends AbstractEntity {
    private String accountName;
    private BigDecimal balance;
    private String currency;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    @ManyToMany
    private List<User> users;


    public String getAccountName() {
        return accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                '}';
    }
}
