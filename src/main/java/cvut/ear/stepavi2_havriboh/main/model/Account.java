package cvut.ear.stepavi2_havriboh.main.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "accounts")
public class Account extends AbstractEntity {

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @OneToMany(mappedBy = "account", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "account_user",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();

    public Account() {
    }

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
                ", transactions=" + transactions +
                ", users=" + users +
                '}';
    }
}
