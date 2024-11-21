package cz.cvut.sem.ear.stepavi2.havriboh.main.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class Account extends AbstractEntity {
    private String accountName;
    private BigDecimal balance = BigDecimal.ZERO;
    private String currency;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    @ManyToMany
    private List<User> users;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void increaseBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
    }

    public void decreaseBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0 || balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive and less than balance");
        }
        balance = balance.subtract(amount);
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
