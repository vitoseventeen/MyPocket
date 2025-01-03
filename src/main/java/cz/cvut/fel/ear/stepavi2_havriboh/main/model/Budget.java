package cz.cvut.fel.ear.stepavi2_havriboh.main.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "budgets")
@NamedQueries({
        @NamedQuery(
                name = "Budget.findByAccountId",
                query = "SELECT b FROM Budget b WHERE b.account.id = :accountId"
        ),
        @NamedQuery(
                name = "Budget.findByTransactionId",
                query = "SELECT b FROM Budget b JOIN b.transactions t WHERE t.id = :transactionId"
        ),
        @NamedQuery(
                name = "Budget.findById",
                query = "SELECT b FROM Budget b WHERE b.id = :id"
        )
})
public class Budget extends AbstractEntity {

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    @JsonProperty("balance")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    @JsonProperty("currency")
    private Currency currency;

    @OneToOne
    @JsonBackReference
    @OrderBy("id ASC")
    private Account account;

    @JsonProperty("account_id")
    public Integer getAccountId() {
        return account != null ? account.getId() : null;
    }

    @OneToMany (mappedBy = "budget")
    @JsonIgnore
    private List<Transaction> transactions;

    public Budget() {
    }


    @Override
    public String toString() {
        return "Budget{" +
                "balance=" + balance +
                ", currency=" + currency +
                ", account=" + account +
                ", transactions=" + transactions +
                '}';
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void addBalance(BigDecimal balance) {
        this.balance = this.balance.add(balance);
    }

    public void subtractBalance(BigDecimal balance) {
        this.balance = this.balance.subtract(balance);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
